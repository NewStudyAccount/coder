import 'package:dio/dio.dart';

import '../../storage/storage_service.dart';

class AuthInterceptor extends Interceptor {
  final StorageService _storageService;

  AuthInterceptor(this._storageService);

  @override
  void onRequest(
      RequestOptions options, RequestInterceptorHandler handler) async {
    final token = _storageService.getToken();
    if (token != null) {
      options.headers['Authorization'] = 'Bearer ${token.accessToken}';
    }
    handler.next(options);
  }
}
