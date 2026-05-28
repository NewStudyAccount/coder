import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../core/network/api_result.dart';
import '../../../../core/network/dio_client.dart';
import '../../../../core/storage/storage_service.dart';
import '../../../../core/theme/theme_provider.dart';
import '../../data/datasources/auth_remote_data_source.dart';
import '../../data/repositories/auth_repository_impl.dart';
import '../../domain/entities/user.dart';
import '../../domain/repositories/auth_repository.dart';

final dioClientProvider = Provider<DioClient>((ref) {
  final storageService = ref.read(storageServiceProvider);
  return DioClient(storageService);
});

final authRepositoryProvider = Provider<AuthRepository>((ref) {
  final dioClient = ref.read(dioClientProvider);
  final storageService = ref.read(storageServiceProvider);
  return AuthRepositoryImpl(
    remoteDataSource: AuthRemoteDataSource(dioClient),
    storageService: storageService,
  );
});

final authProvider =
    StateNotifierProvider<AuthNotifier, AsyncValue<User?>>((ref) {
  final authRepository = ref.read(authRepositoryProvider);
  final storageService = ref.read(storageServiceProvider);
  return AuthNotifier(authRepository, storageService);
});

class AuthNotifier extends StateNotifier<AsyncValue<User?>> {
  final AuthRepository _authRepository;
  final StorageService _storageService;

  AuthNotifier(this._authRepository, this._storageService)
      : super(const AsyncValue.loading()) {
    _checkExistingAuth();
  }

  Future<void> _checkExistingAuth() async {
    final token = _storageService.getToken();
    if (token != null) {
      state = const AsyncValue.loading();
      final result = await _authRepository.getUserInfo();
      switch (result) {
        case Success(:final data):
          state = AsyncValue.data(data);
        case Failure():
          await _storageService.removeToken();
          state = const AsyncValue.data(null);
      }
    } else {
      state = const AsyncValue.data(null);
    }
  }

  Future<bool> login({required String username, required String password}) async {
    state = const AsyncValue.loading();
    final result = await _authRepository.login(
      username: username,
      password: password,
    );
    switch (result) {
      case Success(:final data):
        state = AsyncValue.data(data);
        return true;
      case Failure():
        state = const AsyncValue.data(null);
        return false;
    }
  }

  Future<bool> register({
    required String username,
    required String email,
    required String password,
  }) async {
    state = const AsyncValue.loading();
    final result = await _authRepository.register(
      username: username,
      email: email,
      password: password,
    );
    switch (result) {
      case Success(:final data):
        state = AsyncValue.data(data);
        return true;
      case Failure():
        state = const AsyncValue.data(null);
        return false;
    }
  }

  Future<void> logout() async {
    await _authRepository.logout();
    state = const AsyncValue.data(null);
  }

  bool get isAuthenticated => state.value != null;
}
