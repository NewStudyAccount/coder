import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:hive_flutter/hive_flutter.dart';
import '../constants/storage_keys.dart';

final storageServiceProvider = Provider<StorageService>((ref) {
  return StorageService();
});

class StorageService {
  late final Box _box;

  Future<void> init() async {
    await Hive.initFlutter();
    _box = await Hive.openBox('admin_app');
  }

  String? getString(String key) => _box.get(key) as String?;

  Future<void> setString(String key, String value) async {
    await _box.put(key, value);
  }

  dynamic get(String key) => _box.get(key);

  Future<void> set(String key, dynamic value) async {
    await _box.put(key, value);
  }

  Future<void> remove(String key) async {
    await _box.delete(key);
  }

  Future<void> clear() async {
    await _box.clear();
  }

  bool get isLoggedIn => getString(StorageKeys.accessToken) != null;

  Future<void> saveTokens(String accessToken, String refreshToken) async {
    await setString(StorageKeys.accessToken, accessToken);
    await setString(StorageKeys.refreshToken, refreshToken);
  }

  Future<void> clearAuth() async {
    await remove(StorageKeys.accessToken);
    await remove(StorageKeys.refreshToken);
    await remove(StorageKeys.userInfo);
    await remove(StorageKeys.menuData);
  }
}
