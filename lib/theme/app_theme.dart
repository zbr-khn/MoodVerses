import 'package:flutter/material.dart';

class AppColors {
  static const Color emeraldDeep = Color(0xFF051C14);
  static const Color emeraldSurface = Color(0xFF0A2B20);
  static const Color emeraldSurfaceVariant = Color(0xFF11382B);
  static const Color emeraldPrimary = Color(0xFF10B981);
  static const Color emeraldDark = Color(0xFF047857);

  static const Color goldAccent = Color(0xFFD4AF37);
  static const Color goldLight = Color(0xFFF5E6BE);
  static const Color goldDark = Color(0xFFB8860B);

  static const Color textPrimary = Color(0xFFFFFFFF);
  static const Color textSecondary = Color(0xFFD1D5DB);
  static const Color textMuted = Color(0xFF9CA3AF);

  static const Color borderSubtle = Color(0x33D4AF37);
  static const Color cardBorder = Color(0x22FFFFFF);
}

class AppTheme {
  // Common convenience aliases
  static const Color darkBackground = AppColors.emeraldDeep;
  static const Color cardBackground = AppColors.emeraldSurface;
  static const Color emeraldPrimary = AppColors.emeraldPrimary;
  static const Color emeraldLight = Color(0xFF34D399);
  static const Color goldColor = AppColors.goldAccent;
  static const Color textPrimary = AppColors.textPrimary;
  static const Color textSecondary = AppColors.textSecondary;

  static ThemeData get darkTheme {
    return ThemeData(
      brightness: Brightness.dark,
      scaffoldBackgroundColor: AppColors.emeraldDeep,
      primaryColor: AppColors.emeraldPrimary,
      colorScheme: const ColorScheme.dark(
        primary: AppColors.emeraldPrimary,
        secondary: AppColors.goldAccent,
        surface: AppColors.emeraldSurface,
      ),
      cardTheme: CardThemeData(
        color: AppColors.emeraldSurface,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(16),
          side: const BorderSide(color: AppColors.borderSubtle, width: 0.5),
        ),
      ),
      appBarTheme: const AppBarTheme(
        backgroundColor: AppColors.emeraldDeep,
        elevation: 0,
        centerTitle: false,
        titleTextStyle: TextStyle(
          color: AppColors.textPrimary,
          fontSize: 20,
          fontWeight: FontWeight.bold,
        ),
      ),
      navigationBarTheme: NavigationBarThemeData(
        backgroundColor: AppColors.emeraldSurface,
        indicatorColor: AppColors.goldAccent,
        iconTheme: WidgetStateProperty.resolveWith((states) {
          if (states.contains(WidgetState.selected)) {
            return const IconThemeData(color: AppColors.emeraldDeep);
          }
          return const IconThemeData(color: Colors.grey);
        }),
        labelTextStyle: WidgetStateProperty.resolveWith((states) {
          if (states.contains(WidgetState.selected)) {
            return const TextStyle(
              color: AppColors.goldAccent,
              fontSize: 12,
              fontWeight: FontWeight.bold,
            );
          }
          return const TextStyle(color: Colors.grey, fontSize: 12);
        }),
      ),
    );
  }
}
