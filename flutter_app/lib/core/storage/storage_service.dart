import 'dart:convert';

import 'package:hive/hive.dart';

import '../../features/auth/data/models/token_model.dart';
import '../constants/storage_keys.dart';
import 'user_preferences_model.dart';

class StorageService {
  static const String _boxName = 'app_storage';
  late Box _box;

  Future<void> init() async {
    _box = await Hive.openBox(_boxName);
  }

  // Token
  Future<void> saveToken(TokenModel token) async {
    await _box.put(StorageKeys.authToken, jsonEncode(token.toJson()));
  }

  TokenModel? getToken() {
    final raw = _box.get(StorageKeys.authToken) as String?;
    if (raw == null) return null;
    try {
      return TokenModel.fromJson(jsonDecode(raw) as Map<String, dynamic>);
    } catch (_) {
      return null;
    }
  }

  Future<void> removeToken() async {
    await _box.delete(StorageKeys.authToken);
  }

  // UserPreferences
  Future<void> saveUserPreferences(UserPreferencesModel prefs) async {
    await _box.put(
        StorageKeys.userPreferences, jsonEncode(prefs.toJson()));
  }

  UserPreferencesModel getUserPreferences() {
    final raw = _box.get(StorageKeys.userPreferences) as String?;
    if (raw == null) return const UserPreferencesModel();
    try {
      return UserPreferencesModel.fromJson(
          jsonDecode(raw) as Map<String, dynamic>);
    } catch (_) {
      return const UserPreferencesModel();
    }
  }

  // Generic
  Future<void> setString(String key, String value) async {
    await _box.put(key, value);
  }

  String? getString(String key) => _box.get(key) as String?;

  Future<void> remove(String key) async {
    await _box.delete(key);
  }
}
