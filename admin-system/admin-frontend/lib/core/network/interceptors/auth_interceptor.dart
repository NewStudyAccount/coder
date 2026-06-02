import 'package:dio/dio.dart';
import 'package:riverpod/riverpod.dart';
import '../constants/storage_keys.dart';
import '../storage/storage_service.dart';

class AuthInterceptor extends Interceptor {
  final StorageService _storage;

  AuthInterceptor(this._storage);

  @override
  void onRequest(RequestOptions options, RequestInterceptorHandler handler) {
    final token = _storage.getString(StorageKeys.accessToken);
    if (token != null && token.isNotEmpty) {
      options.headers['Authorization'] = 'Bearer $token';
    }
    handler.next(options);
  }

  @override
  void onError(DioException err, ErrorInterceptorHandler handler) {
    if (err.response?.statusCode == 401) {
      _storage.remove(StorageKeys.accessToken);
      _storage.remove(StorageKeys.refreshToken);
    }
    handler.next(err);
  }
}
