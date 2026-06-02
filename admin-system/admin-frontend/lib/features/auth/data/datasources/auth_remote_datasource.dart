import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../../core/constants/api_constants.dart';
import '../../../../core/network/dio_client.dart';
import '../models/auth_models.dart';

final authRemoteDataSourceProvider = Provider<AuthRemoteDataSource>((ref) {
  return AuthRemoteDataSource(ref.watch(dioProvider));
});

class AuthRemoteDataSource {
  final Dio _dio;

  AuthRemoteDataSource(this._dio);

  Future<Map<String, dynamic>> login(LoginRequest request) async {
    final response = await _dio.post(ApiConstants.login, data: request.toJson());
    return response.data;
  }

  Future<void> logout() async {
    await _dio.post(ApiConstants.logout);
  }

  Future<Map<String, dynamic>> getUserInfo() async {
    final response = await _dio.get(ApiConstants.userInfo);
    return response.data;
  }

  Future<Map<String, dynamic>> refreshToken(String token) async {
    final response = await _dio.post(ApiConstants.refreshToken, data: {
      'refreshToken': token,
    });
    return response.data;
  }
}
