import '../../../../core/constants/api_constants.dart';
import '../../../../core/network/dio_client.dart';
import '../models/login_request.dart';
import '../models/register_request.dart';

class AuthRemoteDataSource {
  final DioClient _dioClient;

  AuthRemoteDataSource(this._dioClient);

  Future<Map<String, dynamic>> login(LoginRequest request) async {
    final response = await _dioClient.post<Map<String, dynamic>>(
      ApiConstants.login,
      data: request.toJson(),
    );
    return response.data!;
  }

  Future<Map<String, dynamic>> register(RegisterRequest request) async {
    final response = await _dioClient.post<Map<String, dynamic>>(
      ApiConstants.register,
      data: request.toJson(),
    );
    return response.data!;
  }

  Future<Map<String, dynamic>> getUserInfo() async {
    final response = await _dioClient.get<Map<String, dynamic>>(
      ApiConstants.userinfo,
    );
    return response.data!;
  }
}
