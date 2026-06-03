import 'package:dio/dio.dart';
import '../api_exception.dart';
import '../../storage/storage_service.dart';

class ErrorInterceptor extends Interceptor {
  final StorageService storageService;

  ErrorInterceptor({required this.storageService});

  @override
  void onError(DioException err, ErrorInterceptorHandler handler) async {
    switch (err.type) {
      case DioExceptionType.connectionTimeout:
      case DioExceptionType.sendTimeout:
      case DioExceptionType.receiveTimeout:
        handler.next(DioException(
          requestOptions: err.requestOptions,
          error: ApiException(code: -1, message: '连接超时'),
        ));
        break;
      case DioExceptionType.connectionError:
        handler.next(DioException(
          requestOptions: err.requestOptions,
          error: ApiException.networkError(),
        ));
        break;
      case DioExceptionType.badResponse:
        final statusCode = err.response?.statusCode;
        final data = err.response?.data;
        String message = '请求失败';
        if (data is Map && data['message'] != null) {
          message = data['message'];
        }

        if (statusCode == 401) {
          final refreshToken = storageService.refreshToken;
          if (refreshToken != null && refreshToken.isNotEmpty) {
            try {
              final dio = Dio(BaseOptions(
                baseUrl: err.requestOptions.baseUrl,
                headers: {'Authorization': 'Bearer $refreshToken'},
              ));
              final response = await dio.post('/auth/refresh');
              if (response.statusCode == 200 && response.data['code'] == 200) {
                final newAccessToken = response.data['data']['accessToken'] as String;
                final newRefreshToken = response.data['data']['refreshToken'] as String? ?? refreshToken;
                await storageService.saveToken(newAccessToken);
                await storageService.saveRefreshToken(newRefreshToken);

                err.requestOptions.headers['Authorization'] = 'Bearer $newAccessToken';
                final retryResponse = await dio.fetch(err.requestOptions);
                handler.resolve(retryResponse);
                return;
              }
            } catch (_) {
              await storageService.clearAll();
            }
          }
          handler.next(DioException(
            requestOptions: err.requestOptions,
            error: ApiException.unauthorized(),
          ));
          break;
        }

        ApiException exception;
        switch (statusCode) {
          case 403:
            exception = ApiException.forbidden();
            break;
          case 404:
            exception = ApiException(code: 404, message: '资源不存在');
            break;
          default:
            exception = ApiException(code: statusCode, message: message);
        }
        handler.next(DioException(
          requestOptions: err.requestOptions,
          error: exception,
        ));
        break;
      default:
        handler.next(err);
    }
  }
}
