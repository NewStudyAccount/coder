import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../storage/storage_service.dart';

final storageServiceProvider = Provider<StorageService>((ref) {
  throw UnimplementedError('storageServiceProvider must be overridden');
});

final themeModeProvider =
    StateNotifierProvider<ThemeModeNotifier, ThemeMode>((ref) {
  final storageService = ref.read(storageServiceProvider);
  return ThemeModeNotifier(storageService);
});

class ThemeModeNotifier extends StateNotifier<ThemeMode> {
  final StorageService _storageService;

  ThemeModeNotifier(this._storageService)
      : super(_parseThemeMode(_storageService.getUserPreferences().themeMode));

  void setThemeMode(ThemeMode mode) {
    state = mode;
    final prefs = _storageService.getUserPreferences();
    _storageService.saveUserPreferences(
      prefs.copyWith(themeMode: mode.name),
    );
  }

  static ThemeMode _parseThemeMode(String mode) {
    switch (mode) {
      case 'light':
        return ThemeMode.light;
      case 'dark':
        return ThemeMode.dark;
        default:
        return ThemeMode.system;
    }
  }
}
