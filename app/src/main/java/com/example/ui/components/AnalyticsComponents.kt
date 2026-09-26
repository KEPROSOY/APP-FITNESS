package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WeightEntry
import com.example.ui.theme.NutriGreenPrimaryLight

@Composable
fun WeightProgressChartCard(
  entries: List<WeightEntry>,
  currentWeight: Float,
  targetWeight: Float,
  onLogWeightClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedRange by remember { mutableStateOf("30 días") }
  val ranges = listOf("7 días", "30 días", "3 meses", "1 año")

  val isDark = isSystemInDarkTheme()

  LiquidGlassCard(
    modifier = modifier
      .fillMaxWidth()
      .testTag("weight_chart_card"),
    shape = RoundedCornerShape(26.dp),
    elevation = 8.dp,
    isDark = isDark,
    ambientGlow = true
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Peso actual",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Row(verticalAlignment = Alignment.Bottom) {
            Text(
              text = "%.1f".format(currentWeight),
              style = MaterialTheme.typography.headlineLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "kg",
              style = MaterialTheme.typography.titleMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(bottom = 3.dp)
            )
          }
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "Objetivo",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Row(verticalAlignment = Alignment.Bottom) {
            Text(
              text = "%.1f".format(targetWeight),
              style = MaterialTheme.typography.headlineSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "kg",
              style = MaterialTheme.typography.titleSmall,
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.padding(bottom = 2.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Time Range Filter Chips
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        ranges.forEach { range ->
          val isSelected = range == selectedRange
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (isSelected) {
              if (isDark) MaterialTheme.colorScheme.primary else Color(0xFF334155)
            } else {
              if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFCBD5E1).copy(alpha = 0.45f)
            },
            border = BorderStroke(
              1.dp,
              if (isSelected) Color.Transparent else if (isDark) Color(0xFF1E354C) else Color(0xFF94A3B8).copy(alpha = 0.45f)
            ),
            modifier = Modifier
              .clickable { selectedRange = range }
              .testTag("filter_chip_$range")
          ) {
            Text(
              text = range,
              style = MaterialTheme.typography.labelMedium,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Animated Canvas Chart with real user entries (no phantom previous data)
      val weights = if (entries.isNotEmpty()) entries.map { it.weightKg } else listOf(currentWeight)
      WeightCanvasChart(
        weights = weights,
        modifier = Modifier
          .fillMaxWidth()
          .height(160.dp)
      )

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.primaryContainer,
          modifier = Modifier
            .clickable(onClick = onLogWeightClick)
            .testTag("button_log_weight")
        ) {
          Text(
            text = "+ Registrar peso",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
          )
        }
      }
    }
  }
}

@Composable
fun WeightCanvasChart(
  weights: List<Float>,
  modifier: Modifier = Modifier
) {
  val primaryColor = MaterialTheme.colorScheme.primary

  val animProgress by animateFloatAsState(
    targetValue = 1f,
    animationSpec = tween(durationMillis = 1000),
    label = "chart_anim"
  )

  Canvas(modifier = modifier) {
    if (weights.isEmpty()) return@Canvas

    val width = size.width
    val height = size.height
    val paddingHorizontal = 16.dp.toPx()
    val paddingVertical = 20.dp.toPx()

    if (weights.size == 1) {
      // Draw single baseline point at start (Punto de Partida)
      val singleY = height / 2f
      drawLine(
        color = Color.Gray.copy(alpha = 0.25f),
        start = Offset(paddingHorizontal, singleY),
        end = Offset(width - paddingHorizontal, singleY),
        strokeWidth = 1.dp.toPx()
      )
      drawCircle(
        color = primaryColor,
        radius = 7.dp.toPx(),
        center = Offset(width / 2f, singleY)
      )
      drawCircle(
        color = Color.White,
        radius = 3.dp.toPx(),
        center = Offset(width / 2f, singleY)
      )
      return@Canvas
    }

    val minW = weights.minOrNull() ?: 50f
    val maxW = weights.maxOrNull() ?: 80f
    val rangeW = (maxW - minW).coerceAtLeast(1f)

    val stepX = (width - paddingHorizontal * 2) / (weights.size - 1)

    // Calculate curve points
    val points = weights.mapIndexed { index, weight ->
      val x = paddingHorizontal + index * stepX
      val normalized = (weight - minW) / rangeW
      val y = height - paddingVertical - (normalized * (height - paddingVertical * 2) * animProgress)
      Offset(x, y)
    }

    // Grid baseline
    drawLine(
      color = Color.Gray.copy(alpha = 0.2f),
      start = Offset(paddingHorizontal, height - paddingVertical),
      end = Offset(width - paddingHorizontal, height - paddingVertical),
      strokeWidth = 1.dp.toPx()
    )

    // Gradient area below curve
    val fillPath = Path().apply {
      moveTo(points.first().x, height - paddingVertical)
      lineTo(points.first().x, points.first().y)
      for (i in 0 until points.size - 1) {
        val p0 = points[i]
        val p1 = points[i + 1]
        val midX = (p0.x + p1.x) / 2
        cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
      }
      lineTo(points.last().x, height - paddingVertical)
      close()
    }

    drawPath(
      path = fillPath,
      brush = Brush.verticalGradient(
        colors = listOf(
          primaryColor.copy(alpha = 0.28f),
          primaryColor.copy(alpha = 0.02f)
        ),
        startY = 0f,
        endY = height
      )
    )

    // Main line curve
    val linePath = Path().apply {
      moveTo(points.first().x, points.first().y)
      for (i in 0 until points.size - 1) {
        val p0 = points[i]
        val p1 = points[i + 1]
        val midX = (p0.x + p1.x) / 2
        cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
      }
    }

    drawPath(
      path = linePath,
      color = primaryColor,
      style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
    )

    // Draw point circles
    points.forEachIndexed { i, pt ->
      drawCircle(
        color = Color.White,
        radius = 5.dp.toPx(),
        center = pt
      )
      drawCircle(
        color = primaryColor,
        radius = 3.dp.toPx(),
        center = pt
      )
    }
  }
}

@Composable
fun NutritionAveragesCard(
  avgCalories: Int = 2140,
  avgProtein: Int = 128,
  avgCarbs: Int = 235,
  avgFat: Int = 62,
  avgSatFat: Float = 11.5f,
  avgTransFat: Float = 0.1f,
  isDark: Boolean = false,
  modifier: Modifier = Modifier
) {
  LiquidGlassCard(
    modifier = modifier
      .fillMaxWidth()
      .testTag("nutrition_averages_card"),
    shape = RoundedCornerShape(24.dp),
    isDark = isDark,
    ambientGlow = isDark
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)
    ) {
      Text(
        text = "Promedios semanales",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = "Calculados con base en tus registros activos",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(16.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        AverageStatItem("Calorías", "%,d".format(avgCalories), "kcal")
        AverageStatItem("Proteína", "$avgProtein", "g")
        AverageStatItem("Carbos", "$avgCarbs", "g")
        AverageStatItem("Grasas", "$avgFat", "g")
      }

      Spacer(modifier = Modifier.height(14.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(if (isDark) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.70f))
          .border(BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.10f) else Color(0xFFE2E8F0)), RoundedCornerShape(12.dp))
          .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Calidad lipídica: Sat. %.1fg".format(avgSatFat),
          style = MaterialTheme.typography.labelSmall,
          color = Color(0xFFEA580C),
          fontWeight = FontWeight.Medium
        )
        Text(
          text = if (avgTransFat > 0.05f) "Trans: %.1fg ⚠️".format(avgTransFat) else "Trans: 0g ✨",
          style = MaterialTheme.typography.labelSmall,
          color = if (avgTransFat > 0.05f) Color(0xFFDC2626) else Color(0xFF16A34A),
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

@Composable
private fun AverageStatItem(
  label: String,
  value: String,
  unit: String
) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = value,
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onSurface
    )
    Text(
      text = unit,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}
