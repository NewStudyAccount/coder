import 'package:dio/dio.dart';

import '../api_exception.dart';

class ErrorInterceptor extends Interceptor {
  final void Function() onUnauthorized;

  ErrorInterceptor({required this.onUnauthorized});

  @override
  void onError(DioException err, ErrorInterceptorHandler handler) {
    if (err.response?.statusCode == 401) {
      onUnauthorized();
      handler.reject(err);
      return;
    }

    // Try to parse backend error format: { code, message, data }
    final responseData = err.response?.data;
    if (responseData is Map<String, dynamic> &&
        responseData.containsKey('code')) {
      handler.reject(
        DioException(
          requestOptions: err.requestOptions,
          response: err.response,
          error: ApiException(
            code: responseData['code'] as int? ?? err.response?.statusCode ?? -1,
            message: responseData['message'] as String? ?? '未知错误',
            data: responseData['data'],
          ),
        ),
      );
      return;
    }

    // Network errors
    switch (err.type) {
      case DioExceptionType.connectionTimeout:
      case DioExceptionType.sendTimeout:
      case DioExceptionType.receiveTimeout:
        handler.reject(
          DioException(
            requestOptions: err.requestOptions,
            error: ApiException(code: -1, message: '连接超时，请重试'),
          ),
        );
        return;
      case DioExceptionType.connectionError:
        handler.reject(
          DioException(
            requestOptions: err.requestOptions,
            error: ApiException(code: -2, message: '网络连接失败，请检查网络'),
          ),
        );
        return;
      default:
        handler.reject(
          DioException(
            requestOptions: err.requestOptions,
            error: ApiException(
              code: err.response?.statusCode ?? -3,
              message: '请求失败',
            ),
          ),
        );
    }
  }
}
