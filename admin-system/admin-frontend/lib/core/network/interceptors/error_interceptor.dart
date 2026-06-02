import 'package:dio/dio.dart';
import '../api_exception.dart';

class ErrorInterceptor extends Interceptor {
  @override
  void onError(DioException err, ErrorInterceptorHandler handler) {
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
        ApiException exception;
        switch (statusCode) {
          case 401:
            exception = ApiException.unauthorized();
            break;
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
