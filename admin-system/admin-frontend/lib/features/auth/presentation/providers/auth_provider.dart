import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../../../core/storage/storage_service.dart';
import '../../data/datasources/auth_remote_datasource.dart';
import '../../data/models/auth_models.dart';

final authProvider = StateNotifierProvider<AuthNotifier, AuthState>((ref) {
  return AuthNotifier(
    ref.watch(authRemoteDataSourceProvider),
    ref.watch(storageServiceProvider),
  );
});

class AuthState {
  final bool isLoading;
  final String? error;
  final UserInfo? userInfo;
  final bool isLoggedIn;

  const AuthState({
    this.isLoading = false,
    this.error,
    this.userInfo,
    this.isLoggedIn = false,
  });

  AuthState copyWith({
    bool? isLoading,
    String? error,
    UserInfo? userInfo,
    bool? isLoggedIn,
  }) =>
      AuthState(
        isLoading: isLoading ?? this.isLoading,
        error: error,
        userInfo: userInfo ?? this.userInfo,
        isLoggedIn: isLoggedIn ?? this.isLoggedIn,
      );
}

class AuthNotifier extends StateNotifier<AuthState> {
  final AuthRemoteDataSource _dataSource;
  final StorageService _storage;

  AuthNotifier(this._dataSource, this._storage) : super(const AuthState()) {
    _checkLogin();
  }

  void _checkLogin() {
    state = state.copyWith(isLoggedIn: _storage.isLoggedIn);
  }

  Future<void> login(String username, String password) async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      final result = await _dataSource.login(LoginRequest(username: username, password: password));
      final response = LoginResponse.fromJson(result);
      await _storage.saveTokens(response.accessToken, response.refreshToken);

      final infoResult = await _dataSource.getUserInfo();
      final userInfo = UserInfo.fromJson(infoResult);
      await _storage.set('user_info', infoResult);

      state = state.copyWith(isLoading: false, isLoggedIn: true, userInfo: userInfo);
    } catch (e) {
      state = state.copyWith(isLoading: false, error: e.toString());
    }
  }

  Future<void> logout() async {
    try {
      await _dataSource.logout();
    } catch (_) {}
    await _storage.clearAuth();
    state = const AuthState(isLoggedIn: false);
  }

  Future<void> loadUserInfo() async {
    try {
      final result = await _dataSource.getUserInfo();
      final userInfo = UserInfo.fromJson(result);
      state = state.copyWith(userInfo: userInfo);
    } catch (_) {}
  }
}
