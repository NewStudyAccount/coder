import '../entities/user.dart';
import '../../../../core/network/api_result.dart';

abstract class AuthRepository {
  Future<ApiResult<User>> login({
    required String username,
    required String password,
  });

  Future<ApiResult<User>> register({
    required String username,
    required String email,
    required String password,
  });

  Future<void> logout();

  Future<ApiResult<User>> getUserInfo();

  Future<ApiResult<void>> refreshToken();
}
