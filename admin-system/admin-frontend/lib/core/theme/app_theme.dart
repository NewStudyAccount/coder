import 'package:flutter/material.dart';

class AppTheme {
  static ThemeData lightTheme() {
    final base = ThemeData(
      useMaterial3: true,
      colorSchemeSeed: const Color(0xFF1976D2),
      brightness: Brightness.light,
    );
    return _buildTheme(base);
  }

  static ThemeData darkTheme() {
    final base = ThemeData(
      useMaterial3: true,
      colorSchemeSeed: const Color(0xFF1976D2),
      brightness: Brightness.dark,
    );
    return _buildTheme(base);
  }

  static ThemeData _buildTheme(ThemeData base) {
    return base.copyWith(
      scaffoldBackgroundColor: base.colorScheme.surface,
      appBarTheme: AppBarTheme(
        elevation: 0,
        centerTitle: false,
        backgroundColor: base.colorScheme.surface,
        foregroundColor: base.colorScheme.onSurface,
      ),
      cardTheme: CardTheme(
        elevation: 0,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(12),
          side: BorderSide(color: base.colorScheme.outlineVariant),
        ),
      ),
      inputDecorationTheme: InputDecorationTheme(
        filled: true,
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(8),
        ),
        contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
      ),
      dataTableTheme: DataTableThemeData(
        headingRowColor: WidgetStateProperty.all(
          base.colorScheme.surfaceContainerHighest,
        ),
      ),
    );
  }
}
