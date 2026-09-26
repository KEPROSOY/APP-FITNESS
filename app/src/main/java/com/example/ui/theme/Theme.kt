package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = NutriGreenPrimaryDark,
  onPrimary = Color(0xFF042F24),
  primaryContainer = NutriGreenContainerDark,
  onPrimaryContainer = NutriGreenOnContainerDark,
  secondary = Color(0xFF94A3B8),
  onSecondary = Color(0xFF0F172A),
  background = DarkBackground,
  onBackground = DarkTextPrimary,
  surface = DarkSurface,
  onSurface = DarkTextPrimary,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = DarkTextSecondary,
  outline = DarkOutline,
)

private val LightColorScheme = lightColorScheme(
  primary = NutriGreenPrimaryLight,
  onPrimary = Color.White,
  primaryContainer = NutriGreenContainerLight,
  onPrimaryContainer = NutriGreenOnContainerLight,
  secondary = Color(0xFF475569),
  onSecondary = Color.White,
  background = LightBackground,
  onBackground = LightTextPrimary,
  surface = LightSurface,
  onSurface = LightTextPrimary,
  surfaceVariant = LightSurfaceVariant,
  onSurfaceVariant = LightTextSecondary,
  outline = LightOutline,
)

@Composable
fun NutriAITheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

@Composable
fun SyvraTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  NutriAITheme(darkTheme = darkTheme, content = content)
}

// Backward compatibility alias for tests
@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  NutriAITheme(darkTheme = darkTheme, content = content)
}
