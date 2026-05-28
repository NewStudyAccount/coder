import '../../../../core/network/api_exception.dart';
import '../../../../core/network/api_result.dart';
import '../../../../core/storage/storage_service.dart';
import '../../domain/entities/user.dart';
import '../../domain/repositories/auth_repository.dart';
import '../datasources/auth_remote_data_source.dart';
import '../models/login_request.dart';
import '../models/register_request.dart';
import '../models/token_model.dart';
import '../models/user_model.dart';

class AuthRepositoryImpl implements AuthRepository {
  final AuthRemoteDataSource _remoteDataSource;
  final StorageService _storageService;

  AuthRepositoryImpl({
    required AuthRemoteDataSource remoteDataSource,
    required StorageService storageService,
  })  : _remoteDataSource = remoteDataSource,
        _storageService = storageService;

  @override
  Future<ApiResult<User>> login({
    required String username,
    required String password,
  }) async {
    try {
      final data = await _remoteDataSource.login(
        LoginRequest(username: username, password: password),
      );
      final token = TokenModel.fromJson(data['token'] as Map<String, dynamic>);
      await _storageService.saveToken(token);
      final user = await _fetchAndCacheUser();
      return Success(user);
    } on ApiException catch (e) {
      return Failure(e);
    } catch (e) {
      return Failure(ApiException(code: -1, message: e.toString()));
    }
  }

  @override
  Future<ApiResult<User>> register({
    required String username,
    required String email,
    required String password,
  }) async {
    try {
      final data = await _remoteDataSource.register(
        RegisterRequest(username: username, email: email, password: password),
      );
      final token = TokenModel.fromJson(data['token'] as Map<String, dynamic>);
      await _storageService.saveToken(token);
      final user = await _fetchAndCacheUser();
      return Success(user);
    } on ApiException catch (e) {
      return Failure(e);
    } catch (e) {
      return Failure(ApiException(code: -1, message: e.toString()));
    }
  }

  @override
  Future<void> logout() async {
    await _storageService.removeToken();
  }

  @override
  Future<ApiResult<User>> getUserInfo() async {
    try {
      final user = await _fetchAndCacheUser();
      return Success(user);
    } on ApiException catch (e) {
      return Failure(e);
    } catch (e) {
      return Failure(ApiException(code: -1, message: e.toString()));
    }
  }

  @override
  Future<ApiResult<void>> refreshToken() async {
    try {
      final token = _storageService.getToken();
      if (token?.refreshToken == null) {
        return Failure(ApiException(code: 401, message: '无刷新令牌'));
      }
      // TODO: 实现刷新 Token 的 API 调用
      return const Success(null);
    } on ApiException catch (e) {
      return Failure(e);
    } catch (e) {
      return Failure(ApiException(code: -1, message: e.toString()));
    }
  }

  Future<User> _fetchAndCacheUser() async {
    final data = await _remoteDataSource.getUserInfo();
    final userModel = UserModel.fromJson(data['data'] as Map<String, dynamic>);
    return _mapToEntity(userModel);
  }

  User _mapToEntity(UserModel model) {
    return User(
      id: model.id,
      username: model.username,
      email: model.email,
      avatar: model.avatar,
      phone: model.phone,
      roles: model.roles,
    );
  }
}
