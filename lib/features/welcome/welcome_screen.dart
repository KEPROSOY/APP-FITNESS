import 'package:flutter/material.dart';
import '../../core/theme/app_theme.dart';

/// Pantalla de bienvenida ('WelcomeScreen') para NutriAI.
/// Diseño con estética premium, minimalista y acento verde suave.
class WelcomeScreen extends StatelessWidget {
  final VoidCallback onStart;
  final VoidCallback onExistingAccount;

  const WelcomeScreen({
    super.key,
    required this.onStart,
    required this.onExistingAccount,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return Scaffold(
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 24.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              // Encabezado con Logo y Marca NutriAI
              Column(
                children: [
                  const SizedBox(height: 32),
                  // Logo circular estilizado con acento verde suave
                  Container(
                    width: 72,
                    height: 72,
                    decoration: BoxDecoration(
                      color: isDark 
                          ? AppTheme.primaryContainerDark 
                          : AppTheme.primaryContainerLight,
                      shape: BoxShape.circle,
                      boxShadow: [
                        BoxShadow(
                          color: AppTheme.primaryLight.withOpacity(0.18),
                          blurRadius: 20,
                          offset: const Offset(0, 8),
                        )
                      ],
                    ),
                    child: const Icon(
                      Icons.eco_rounded,
                      size: 38,
                      color: AppTheme.primaryLight,
                    ),
                  ),
                  const SizedBox(height: 18),
                  // Nombre de la marca
                  Text(
                    'NutriAI',
                    style: theme.textTheme.displayMedium?.copyWith(
                      fontWeight: FontWeight.w900,
                      letterSpacing: -0.5,
                    ),
                  ),
                  const SizedBox(height: 8),
                  // Eslogan en chip minimalista
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 6),
                    decoration: BoxDecoration(
                      color: theme.colorScheme.surfaceContainerHighest,
                      borderRadius: BorderRadius.circular(16),
                      border: Border.all(
                        color: AppTheme.primaryLight.withOpacity(0.2),
                        width: 1,
                      ),
                    ),
                    child: const Text(
                      'Tus alimentos. Tu progreso. Tu IA.',
                      style: TextStyle(
                        fontSize: 13,
                        fontWeight: FontWeight.w600,
                        color: AppTheme.primaryLight,
                        letterSpacing: 0.2,
                      ),
                    ),
                  ),
                ],
              ),

              // Contenedor visual hero / ilustración
              Container(
                height: 240,
                width: double.infinity,
                decoration: BoxDecoration(
                  color: isDark ? AppTheme.surfaceDark : AppTheme.surfaceLight,
                  borderRadius: BorderRadius.circular(32),
                  border: Border.all(
                    color: isDark ? const Color(0xFF26362F) : const Color(0xFFE2E8F0),
                    width: 1,
                  ),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.black.withOpacity(isDark ? 0.4 : 0.04),
                      blurRadius: 24,
                      offset: const Offset(0, 12),
                    )
                  ],
                ),
                child: Center(
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Container(
                        padding: const EdgeInsets.all(20),
                        decoration: BoxDecoration(
                          color: AppTheme.primaryLight.withOpacity(0.1),
                          shape: BoxShape.circle,
                        ),
                        child: const Icon(
                          Icons.restaurant_rounded,
                          size: 56,
                          color: AppTheme.primaryLight,
                        ),
                      ),
                      const SizedBox(height: 16),
                      Text(
                        'Nutrición Inteligente',
                        style: theme.textTheme.titleMedium?.copyWith(
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        'Conteo automático y sugerencias personalizadas',
                        style: theme.textTheme.bodyMedium,
                      ),
                    ],
                  ),
                ),
              ),

              // Texto descriptivo y Botones de acción
              Column(
                children: [
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 12.0),
                    child: Text(
                      'Registra lo que comes, conoce tus nutrientes y alcanza tus objetivos de salud con el poder de la inteligencia artificial.',
                      textAlign: TextAlign.center,
                      style: theme.textTheme.bodyLarge?.copyWith(
                        color: theme.colorScheme.onSurfaceVariant,
                        height: 1.4,
                      ),
                    ),
                  ),
                  const SizedBox(height: 28),

                  // Botón principal: Comenzar
                  SizedBox(
                    width: double.infinity,
                    height: 56,
                    child: ElevatedButton(
                      onPressed: onStart,
                      style: ElevatedButton.styleFrom(
                        backgroundColor: AppTheme.primaryLight,
                        foregroundColor: Colors.white,
                        elevation: 0,
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(20),
                        ),
                      ),
                      child: const Text(
                        'Comenzar',
                        style: TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                  ),
                  const SizedBox(height: 12),

                  // Botón secundario: Ya tengo una cuenta
                  SizedBox(
                    width: double.infinity,
                    height: 54,
                    child: OutlinedButton(
                      onPressed: onExistingAccount,
                      style: OutlinedButton.styleFrom(
                        foregroundColor: theme.colorScheme.onSurface,
                        side: BorderSide(
                          color: isDark ? const Color(0xFF26362F) : const Color(0xFFE2E8F0),
                          width: 1.5,
                        ),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(20),
                        ),
                      ),
                      child: const Text(
                        'Ya tengo una cuenta',
                        style: TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ),
                  ),
                  const SizedBox(height: 24),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}
