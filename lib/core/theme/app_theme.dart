import 'package:flutter/material.dart';

/// Sistema de diseño y temas de NutriAI con estética premium, minimalista
/// y verde suave como color de acento principal.
class AppTheme {
  // Paleta de verdes suaves y naturales
  static const Color primaryLight = Color(0xFF10B981); // Verde menta / esmeralda suave
  static const Color primaryDark = Color(0xFF34D399);  // Verde salvia suave
  static const Color primaryContainerLight = Color(0xFFD1FAE5);
  static const Color primaryContainerDark = Color(0xFF064E3B);

  // Superficies y fondos en modo claro
  static const Color bgLight = Color(0xFFF8FAF8);
  static const Color surfaceLight = Color(0xFFFFFFFF);
  static const Color surfaceVariantLight = Color(0xFFEFF3F0);
  static const Color textPrimaryLight = Color(0xFF0F172A);
  static const Color textSecondaryLight = Color(0xFF64748B);

  // Superficies y fondos en modo oscuro
  static const Color bgDark = Color(0xFF0A0F0D);
  static const Color surfaceDark = Color(0xFF131A16);
  static const Color surfaceVariantDark = Color(0xFF1C2621);
  static const Color textPrimaryDark = Color(0xFFF1F5F9);
  static const Color textSecondaryDark = Color(0xFF94A3B8);

  // Colores de macronutrientes
  static const Color proteinColor = Color(0xFF38BDF8); // Azul cielo
  static const Color carbsColor = Color(0xFFF59E0B);   // Ámbar suave
  static const Color fatColor = Color(0xFFFB7185);     // Coral suave

  /// Tema en Modo Claro (Light Mode)
  static ThemeData get lightTheme {
    return ThemeData(
      useMaterial3: true,
      brightness: Brightness.light,
      scaffoldBackgroundColor: bgLight,
      colorScheme: const ColorScheme.light(
        primary: primaryLight,
        onPrimary: Colors.white,
        primaryContainer: primaryContainerLight,
        onPrimaryContainer: Color(0xFF065F46),
        secondary: Color(0xFF475569),
        surface: surfaceLight,
        onSurface: textPrimaryLight,
        surfaceContainerHighest: surfaceVariantLight,
        onSurfaceVariant: textSecondaryLight,
      ),
      textTheme: const TextTheme(
        displayMedium: TextStyle(fontSize = 34, fontWeight: FontWeight.w800, color: textPrimaryLight, letterSpacing: -0.5),
        titleLarge: TextStyle(fontSize: 20, fontWeight: FontWeight.w700, color: textPrimaryLight),
        titleMedium: TextStyle(fontSize: 16, fontWeight: FontWeight.w600, color: textPrimaryLight),
        bodyLarge: TextStyle(fontSize: 16, fontWeight: FontWeight.w400, color: textSecondaryLight, height: 1.5),
        bodyMedium: TextStyle(fontSize: 14, fontWeight: FontWeight.w400, color: textSecondaryLight),
        labelLarge: TextStyle(fontSize: 15, fontWeight: FontWeight.w700, color: Colors.white),
        labelMedium: TextStyle(fontSize: 12, fontWeight: FontWeight.w600),
      ),
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: primaryLight,
          foregroundColor: Colors.white,
          shape: RoundedCornerShape(borderRadius: BorderRadius.circular(20)),
          padding: const EdgeInsets.symmetric(vertical: 16, horizontal: 24),
          elevation: 0,
        ),
      ),
      outlinedButtonTheme: OutlinedButtonThemeData(
        style: OutlinedButton.styleFrom(
          foregroundColor: textPrimaryLight,
          side: const BorderSide(color: Color(0xFFE2E8F0), width: 1.5),
          shape: RoundedCornerShape(borderRadius: BorderRadius.circular(20)),
          padding: const EdgeInsets.symmetric(vertical: 16, horizontal: 24),
        ),
      ),
    );
  }

  /// Tema en Modo Oscuro (Dark Mode)
  static ThemeData get darkTheme {
    return ThemeData(
      useMaterial3: true,
      brightness: Brightness.dark,
      scaffoldBackgroundColor: bgDark,
      colorScheme: const ColorScheme.dark(
        primary: primaryDark,
        onPrimary: Color(0xFF042F24),
        primaryContainer: primaryContainerDark,
        onPrimaryContainer: Color(0xFFA7F3D0),
        secondary: Color(0xFF94A3B8),
        surface: surfaceDark,
        onSurface: textPrimaryDark,
        surfaceContainerHighest: surfaceVariantDark,
        onSurfaceVariant: textSecondaryDark,
      ),
      textTheme: const TextTheme(
        displayMedium: TextStyle(fontSize = 34, fontWeight: FontWeight.w800, color: textPrimaryDark, letterSpacing: -0.5),
        titleLarge: TextStyle(fontSize: 20, fontWeight: FontWeight.w700, color: textPrimaryDark),
        titleMedium: TextStyle(fontSize: 16, fontWeight: FontWeight.w600, color: textPrimaryDark),
        bodyLarge: TextStyle(fontSize: 16, fontWeight: FontWeight.w400, color: textSecondaryDark, height: 1.5),
        bodyMedium: TextStyle(fontSize: 14, fontWeight: FontWeight.w400, color: textSecondaryDark),
        labelLarge: TextStyle(fontSize: 15, fontWeight: FontWeight.w700, color: Color(0xFF042F24)),
        labelMedium: TextStyle(fontSize: 12, fontWeight: FontWeight.w600),
      ),
    );
  }
}

class RoundedCornerShape extends RoundedRectangleBorder {
  const RoundedCornerShape({super.borderRadius});
}
