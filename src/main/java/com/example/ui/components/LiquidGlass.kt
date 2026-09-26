package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Liquid Glass System (iOS 26 / VisionOS aesthetic)
 * Reference styles:
 * - Dark Mode: Translucent smoked glass with high-intensity bright white/crystalline shiny border ("brillosito").
 * - Light Mode: Frosted luminous crystal glass with pleasant soft slate-gray border ("gris agradable").
 * - Lens Refraction: Specular top-light glare, curved depth, and organic ambient diffuse glow.
 */

val LiquidMintGlow = Color(0xFF38BDF8) // Crystal Ice Cyan
val LiquidCyanGlow = Color(0xFF38BDF8)
val LiquidTealGlow = Color(0xFF22D3EE)
val LiquidSageSoft = Color(0xFFBAE6FD)

// Grayish Liquid Glass palette for Light Mode (sin colores sólidos, acabado translúcido ahumado/esmerilado)
val LiquidLightGrayBorder = Color(0xFF94A3B8)
val LiquidLightGraySubtle = Color(0xFFCBD5E1)
val LiquidLightGrayDeep = Color(0xFF64748B)

// Shiny crystalline white border palette for Dark Mode
val LiquidDarkShinyWhite = Color(0xFFFFFFFF)
val LiquidDarkSilverGlow = Color(0xFFE0E7FF)

/**
 * Fullscreen ambient background with subtle liquid orbs of light.
 * Gives frosted glass cards a luminous substrate to refract and glow against.
 */
@Composable
fun LiquidGlassBackground(
  modifier: Modifier = Modifier,
  isDark: Boolean = MaterialTheme.colorScheme.background.luminance() < 0.5f,
  content: @Composable BoxScope.() -> Unit
) {
  val orb1Color = if (isDark) Color(0xFF10B981).copy(alpha = 0.28f) else Color(0xFF86EFAC).copy(alpha = 0.35f)
  val orb2Color = if (isDark) Color(0xFF0284C7).copy(alpha = 0.26f) else Color(0xFFBAE6FD).copy(alpha = 0.40f)
  val orb3Color = if (isDark) Color(0xFF059669).copy(alpha = 0.20f) else Color(0xFFE2E8F0).copy(alpha = 0.50f)
  val orb4Color = if (isDark) Color(0xFF38BDF8).copy(alpha = 0.16f) else Color(0xFFDCFCE7).copy(alpha = 0.35f)

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        if (isDark) {
          Brush.verticalGradient(listOf(Color(0xFF03070E), Color(0xFF060F18), Color(0xFF02050A)))
        } else {
          Brush.verticalGradient(
            listOf(
              Color(0xFFFFFFFF),
              Color(0xFFF8FAFC),
              Color(0xFFF0FDF4)
            )
          )
        }
      )
      .drawBehind {
        // Top-right emerald radiant orb
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(orb1Color, Color.Transparent),
            center = Offset(size.width * 0.90f, size.height * 0.12f),
            radius = size.width * 0.75f
          ),
          center = Offset(size.width * 0.90f, size.height * 0.12f),
          radius = size.width * 0.75f
        )
        // Mid-left cyan soothing glow
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(orb2Color, Color.Transparent),
            center = Offset(size.width * 0.05f, size.height * 0.48f),
            radius = size.width * 0.70f
          ),
          center = Offset(size.width * 0.05f, size.height * 0.48f),
          radius = size.width * 0.70f
        )
        // Center-low ambient diffuse aura
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(orb4Color, Color.Transparent),
            center = Offset(size.width * 0.50f, size.height * 0.65f),
            radius = size.width * 0.60f
          ),
          center = Offset(size.width * 0.50f, size.height * 0.65f),
          radius = size.width * 0.60f
        )
        // Bottom-right liquid glow
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(orb3Color, Color.Transparent),
            center = Offset(size.width * 0.85f, size.height * 0.88f),
            radius = size.width * 0.65f
          ),
          center = Offset(size.width * 0.85f, size.height * 0.88f),
          radius = size.width * 0.65f
        )
      }
  ) {
    content()
  }
}

/**
 * Reusable iPhone Liquid Glass Card container with dynamic light/dark styling.
 */
@Composable
fun LiquidGlassCard(
  modifier: Modifier = Modifier,
  shape: Shape = RoundedCornerShape(26.dp),
  elevation: Dp = 8.dp,
  isDark: Boolean = MaterialTheme.colorScheme.background.luminance() < 0.5f,
  ambientGlow: Boolean = false,
  content: @Composable () -> Unit
) {
  // Translucent glass fill:
  // - Dark: Smoked glass with optical gradient
  // - Light: Grayish translucent liquid glass (Liquid Glass Grisáceo translúcido, sin colores sólidos)
  val glassGradient = if (isDark) {
    Brush.verticalGradient(
      listOf(
        Color(0xFF141C2B).copy(alpha = 0.82f),
        Color(0xFF0C1322).copy(alpha = 0.88f),
        Color(0xFF070B14).copy(alpha = 0.92f)
      )
    )
  } else {
    // Frosted luminous white liquid glass (translúcido, puro y vinculado al tema blanco)
    Brush.verticalGradient(
      listOf(
        Color.White.copy(alpha = 0.90f),
        Color(0xFFF8FAFC).copy(alpha = 0.82f),
        Color(0xFFF1F5F9).copy(alpha = 0.72f)
      )
    )
  }

  // Specular Border:
  // - Dark: Bright shiny white gradient highlight ("blanco más claro y brillosito")
  // - Light: White and soft silver specular border with crystalline reflection
  val specularBorderBrush = if (isDark) {
    Brush.linearGradient(
      colors = listOf(
        LiquidDarkShinyWhite.copy(alpha = 0.72f),
        LiquidDarkSilverGlow.copy(alpha = 0.42f),
        LiquidDarkShinyWhite.copy(alpha = 0.20f),
        LiquidCyanGlow.copy(alpha = 0.38f),
        LiquidDarkShinyWhite.copy(alpha = 0.55f)
      ),
      start = Offset(0f, 0f),
      end = Offset(500f, 750f)
    )
  } else {
    Brush.linearGradient(
      colors = listOf(
        Color.White.copy(alpha = 0.95f),
        Color(0xFFE2E8F0).copy(alpha = 0.85f),
        Color.White.copy(alpha = 0.65f),
        Color(0xFFCBD5E1).copy(alpha = 0.70f),
        Color.White.copy(alpha = 0.90f)
      ),
      start = Offset(0f, 0f),
      end = Offset(500f, 750f)
    )
  }

  val shadowColor = if (isDark) {
    Color(0x77000000)
  } else {
    Color(0x0C0F172A)
  }

  val spotColor = if (ambientGlow) {
    if (isDark) LiquidDarkShinyWhite.copy(alpha = 0.40f) else Color(0xFF64748B).copy(alpha = 0.25f)
  } else {
    if (isDark) Color.White.copy(alpha = 0.15f) else shadowColor
  }

  Box(
    modifier = modifier
      .shadow(
        elevation = elevation,
        shape = shape,
        spotColor = spotColor,
        ambientColor = shadowColor
      )
      .clip(shape)
      .background(glassGradient)
      .border(
        BorderStroke(
          width = if (isDark) 1.4.dp else 1.2.dp,
          brush = specularBorderBrush
        ),
        shape = shape
      )
      .drawBehind {
        // Subtle top lens specular gleam line (internal refraction)
        val gleamHeight = size.height * 0.22f
        drawRect(
          brush = Brush.verticalGradient(
            colors = listOf(
              (if (isDark) Color.White.copy(alpha = 0.14f) else Color.White.copy(alpha = 0.45f)),
              Color.Transparent
            ),
            startY = 0f,
            endY = gleamHeight
          ),
          size = androidx.compose.ui.geometry.Size(size.width, gleamHeight)
        )
      }
  ) {
    content()
  }
}

/**
 * Pill-shaped glass container for navigation bars, chips, floating actions.
 */
@Composable
fun LiquidGlassPill(
  modifier: Modifier = Modifier,
  isDark: Boolean = MaterialTheme.colorScheme.background.luminance() < 0.5f,
  content: @Composable () -> Unit
) {
  LiquidGlassCard(
    modifier = modifier,
    shape = CircleShape,
    elevation = 10.dp,
    isDark = isDark,
    ambientGlow = isDark,
    content = content
  )
}
