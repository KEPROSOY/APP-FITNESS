package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailySummary
import com.example.data.model.MealRecord
import com.example.data.model.MealType
import com.example.ui.theme.CalorieRing
import com.example.ui.theme.CalorieRingBg
import com.example.ui.theme.CalorieRingDarkBg
import com.example.ui.theme.MacroCarbs
import com.example.ui.theme.MacroCarbsBg
import com.example.ui.theme.MacroFat
import com.example.ui.theme.MacroFatBg
import com.example.ui.theme.MacroProtein
import com.example.ui.theme.MacroProteinBg
import com.example.ui.theme.NutriGreenPrimaryLight

@Composable
fun CalorieProgressCard(
  summary: DailySummary,
  modifier: Modifier = Modifier,
  ringSize: Dp = 195.dp,
  isDark: Boolean = MaterialTheme.colorScheme.background.luminance() < 0.5f
) {
  val animatedProgress by animateFloatAsState(
    targetValue = summary.calorieProgress,
    animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
    label = "calorie_progress"
  )

  LiquidGlassCard(
    modifier = modifier
      .fillMaxWidth()
      .testTag("calorie_progress_card"),
    shape = RoundedCornerShape(28.dp),
    elevation = 10.dp,
    isDark = isDark,
    ambientGlow = true
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(ringSize)
      ) {
        val trackColor = if (isDark) Color(0xFF1E293B) else Color(0xFF94A3B8).copy(alpha = 0.25f)

        Canvas(modifier = Modifier.size(ringSize)) {
          val strokeWidth = 16.dp.toPx()

          // Background Liquid Track
          drawArc(
            color = trackColor,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
          )

          // Glowing Liquid Progress Arc with soothing Mint & Emerald
          drawArc(
            brush = Brush.sweepGradient(
              listOf(
                Color(0xFF34D399),
                Color(0xFF0FB477),
                Color(0xFF2DD4BF),
                Color(0xFF34D399)
              )
            ),
            startAngle = -90f,
            sweepAngle = animatedProgress * 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
          )
        }

        // Center Content with Liquid Glass Typography
        Column(
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "%,d".format(summary.totalCaloriesConsumed),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.5).sp,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "/ %,d kcal".format(summary.targetCalories),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(8.dp))

          // Liquid Glass Pill Badge
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(
                if (isDark) Color(0xFF163328).copy(alpha = 0.85f)
                else Color(0xFFD8F5E8).copy(alpha = 0.90f)
              )
              .border(
                BorderStroke(
                  1.dp,
                  if (isDark) Color(0xFF34D399).copy(alpha = 0.4f)
                  else Color(0xFF0FB477).copy(alpha = 0.3f)
                ),
                RoundedCornerShape(20.dp)
              )
              .padding(horizontal = 12.dp, vertical = 5.dp)
          ) {
            Text(
              text = if (summary.workoutCaloriesBurned > 0) {
                "%,d kcal netas (🔥 -%,d gym)".format(summary.netCalories, summary.workoutCaloriesBurned)
              } else {
                "%,d kcal restantes".format(summary.caloriesRemaining)
              },
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onPrimaryContainer,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

@Composable
fun MacroMetricsRow(
  summary: DailySummary,
  modifier: Modifier = Modifier,
  isDark: Boolean = MaterialTheme.colorScheme.background.luminance() < 0.5f
) {
  val goodFats = (summary.totalFat - summary.totalSaturatedFat - summary.totalTransFat).coerceAtLeast(0f)

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // 3 Main Macro Cards
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      MacroItemCard(
        title = "Proteína",
        current = summary.totalProtein.toInt(),
        target = summary.targetProtein,
        unit = "g",
        color = MacroProtein,
        bgColor = MacroProteinBg,
        isDark = isDark,
        modifier = Modifier.weight(1f)
      )

      MacroItemCard(
        title = "Carbohidratos",
        current = summary.totalCarbs.toInt(),
        target = summary.targetCarbs,
        unit = "g",
        color = MacroCarbs,
        bgColor = MacroCarbsBg,
        isDark = isDark,
        modifier = Modifier.weight(1f)
      )

      MacroItemCard(
        title = "Grasas",
        current = summary.totalFat.toInt(),
        target = summary.targetFat,
        unit = "g",
        color = MacroFat,
        bgColor = MacroFatBg,
        isDark = isDark,
        modifier = Modifier.weight(1f)
      )
    }

    // Liquid Glass Sub-card: Desglose de Grasas (Buenas vs. Malas / Trans)
    LiquidGlassCard(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("fats_breakdown_card"),
      shape = RoundedCornerShape(20.dp),
      elevation = 4.dp,
      isDark = isDark
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Calidad de Grasas",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Total: ${summary.totalFat.toInt()}g",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Grasas Buenas (Insaturadas)
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isDark) Color(0xFF0F2624) else Color(0xFFE0F7FA),
            border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.25f)),
            modifier = Modifier.weight(1f)
          ) {
            Column(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "Grasas Buenas",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF00B4D8)
              )
              Text(
                text = "%.1fg".format(goodFats),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }

          // Grasas Saturadas (Malas)
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isDark) Color(0xFF2E2416) else Color(0xFFFFF7ED),
            border = BorderStroke(1.dp, Color(0xFFF97316).copy(alpha = 0.25f)),
            modifier = Modifier.weight(1f)
          ) {
            Column(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "Saturadas (Malas)",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFEA580C)
              )
              Text(
                text = "%.1fg".format(summary.totalSaturatedFat),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }

          // Grasas Trans (Objetivo 0g)
          val hasTrans = summary.totalTransFat > 0.05f
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (hasTrans) {
              if (isDark) Color(0xFF3B1818) else Color(0xFFFEE2E2)
            } else {
              if (isDark) Color(0xFF162E20) else Color(0xFFDCFCE7)
            },
            border = BorderStroke(
              1.dp,
              if (hasTrans) Color(0xFFEF4444).copy(alpha = 0.4f) else Color(0xFF22C55E).copy(alpha = 0.3f)
            ),
            modifier = Modifier.weight(1f)
          ) {
            Column(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = if (hasTrans) "Trans ⚠️" else "Trans ✨",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (hasTrans) Color(0xFFDC2626) else Color(0xFF16A34A)
              )
              Text(
                text = if (hasTrans) "%.1fg".format(summary.totalTransFat) else "0.0g",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun MacroItemCard(
  title: String,
  current: Int,
  target: Int,
  unit: String,
  color: Color,
  bgColor: Color,
  modifier: Modifier = Modifier,
  isDark: Boolean = MaterialTheme.colorScheme.background.luminance() < 0.5f
) {
  val progress = if (target > 0) (current.toFloat() / target).coerceIn(0f, 1f) else 0f
  val animatedProg by animateFloatAsState(
    targetValue = progress,
    animationSpec = tween(700),
    label = "macro_prog"
  )

  LiquidGlassCard(
    modifier = modifier.testTag("macro_card_${title.lowercase()}"),
    shape = RoundedCornerShape(22.dp),
    elevation = 6.dp,
    isDark = isDark
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Box(
          modifier = Modifier
            .size(9.dp)
            .shadow(4.dp, CircleShape, spotColor = color)
            .clip(CircleShape)
            .background(color)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "$current / $target $unit",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(10.dp))

      LinearProgressIndicator(
        progress = { animatedProg },
        modifier = Modifier
          .fillMaxWidth()
          .height(7.dp)
          .clip(RoundedCornerShape(4.dp)),
        color = color,
        trackColor = color.copy(alpha = 0.18f)
      )
    }
  }
}

@Composable
fun MealItemRow(
  mealType: MealType,
  meal: MealRecord?,
  onAddClick: () -> Unit,
  onDeleteClick: (() -> Unit)? = null,
  onMealClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier,
  isDark: Boolean = MaterialTheme.colorScheme.background.luminance() < 0.5f
) {
  LiquidGlassCard(
    modifier = modifier
      .fillMaxWidth()
      .testTag("meal_card_${mealType.name.lowercase()}")
      .then(
        if (meal != null && onMealClick != null) {
          Modifier.clickable(onClick = onMealClick)
        } else Modifier
      ),
    shape = RoundedCornerShape(22.dp),
    elevation = 5.dp,
    isDark = isDark
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Meal Emoji Icon Avatar with Liquid Glass finish
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(48.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(
            if (isDark) Color(0xFF1B2C24).copy(alpha = 0.8f)
            else Color(0xFF94A3B8).copy(alpha = 0.22f)
          )
          .border(
            BorderStroke(
              1.dp,
              if (isDark) Color.White.copy(alpha = 0.15f)
              else Color(0xFF94A3B8).copy(alpha = 0.40f)
            ),
            RoundedCornerShape(16.dp)
          )
      ) {
        if (meal?.imageUrl != null) {
          coil.compose.AsyncImage(
            model = meal.imageUrl,
            contentDescription = "Meal photo",
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
          )
        } else {
          Text(text = mealType.emoji, fontSize = 22.sp)
        }
      }

      Spacer(modifier = Modifier.width(14.dp))

      // Meal Details
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = mealType.displayName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          if (meal != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = meal.timeFormatted,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (meal != null) {
          Text(
            text = "${meal.totalCalories} kcal  •  P: ${meal.totalProtein.toInt()}g  C: ${meal.totalCarbs.toInt()}g  G: ${meal.totalFat.toInt()}g",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(3.dp))
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (isDark) Color(0xFF2B2116) else Color(0xFFFFF7ED),
              border = BorderStroke(0.5.dp, Color(0xFFF97316).copy(alpha = 0.3f))
            ) {
              Text(
                text = "Sat: %.1fg".format(meal.totalSaturatedFat),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFEA580C),
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
              )
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (meal.totalTransFat > 0.05f) {
                if (isDark) Color(0xFF3B1818) else Color(0xFFFEE2E2)
              } else {
                if (isDark) Color(0xFF162E20) else Color(0xFFDCFCE7)
              },
              border = BorderStroke(
                0.5.dp,
                if (meal.totalTransFat > 0.05f) Color(0xFFEF4444).copy(alpha = 0.4f)
                else Color(0xFF22C55E).copy(alpha = 0.3f)
              )
            ) {
              Text(
                text = if (meal.totalTransFat > 0.05f) "Trans: %.1fg ⚠️".format(meal.totalTransFat) else "0g Trans ✨",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (meal.totalTransFat > 0.05f) Color(0xFFDC2626) else Color(0xFF16A34A),
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
              )
            }
          }

          if (meal.ingredients.isNotEmpty()) {
            Spacer(modifier = Modifier.height(2.dp))
            val itemsPreview = meal.ingredients.take(2).joinToString(", ") { it.foodName }
            Text(
              text = itemsPreview + if (meal.ingredients.size > 2) " +${meal.ingredients.size - 2}" else "",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.SemiBold
            )
          }
        } else {
          Text(
            text = "Sin registrar",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
          )
        }
      }

      // Action buttons
      if (meal != null) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onAddClick,
            modifier = Modifier.testTag("add_more_meal_${mealType.name.lowercase()}")
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Agregar más alimentos",
              tint = MaterialTheme.colorScheme.primary
            )
          }
          if (onMealClick != null) {
            IconButton(
              onClick = onMealClick,
              modifier = Modifier.testTag("info_meal_${mealType.name.lowercase()}")
            ) {
              Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Ver nutrientes y macros",
                tint = MaterialTheme.colorScheme.primary
              )
            }
          }
          if (onDeleteClick != null) {
            IconButton(
              onClick = onDeleteClick,
              modifier = Modifier.testTag("delete_meal_${mealType.name.lowercase()}")
            ) {
              Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Eliminar",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
              )
            }
          }
        }
      } else {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(38.dp)
            .shadow(4.dp, CircleShape, spotColor = LiquidMintGlow.copy(alpha = 0.3f))
            .clip(CircleShape)
            .background(
              if (isDark) Color(0xFF17382B) else Color(0xFFD8F5E8)
            )
            .border(
              BorderStroke(
                1.dp,
                if (isDark) Color(0xFF34D399).copy(alpha = 0.35f)
                else Color(0xFF0FB477).copy(alpha = 0.3f)
              ),
              CircleShape
            )
            .clickable(onClick = onAddClick)
            .testTag("add_meal_button_${mealType.name.lowercase()}")
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Registrar",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}

/**
 * Dialog to inspect full macronutrients, saturated/trans fats, and ingredients of any logged meal.
 */
@Composable
fun MealNutrientDetailDialog(
  meal: MealRecord,
  onDismiss: () -> Unit,
  onDeleteMeal: (() -> Unit)? = null,
  onRemoveIngredient: ((Int) -> Unit)? = null,
  isDark: Boolean = MaterialTheme.colorScheme.background.luminance() < 0.5f
) {
  val goodFats = (meal.totalFat - meal.totalSaturatedFat - meal.totalTransFat).coerceAtLeast(0f)

  val scrollState = rememberScrollState()

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      shape = RoundedCornerShape(28.dp),
      colors = CardDefaults.cardColors(
        containerColor = if (isDark) Color(0xFF0F172A) else Color.White
      ),
      border = BorderStroke(
        1.2.dp,
        if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
      ),
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .padding(vertical = 16.dp)
        .testTag("dialog_meal_nutrient_detail")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(scrollState)
          .padding(20.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = meal.mealType.emoji, fontSize = 28.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = meal.mealType.displayName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Registrado a las ${meal.timeFormatted}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
          IconButton(onClick = onDismiss) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Cerrar",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Big Calories Callout
        Surface(
          shape = RoundedCornerShape(18.dp),
          color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Calorías Totales",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "${meal.totalCalories} kcal",
              style = MaterialTheme.typography.headlineSmall,
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.primary
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "Macronutrientes Principales",
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Proteína
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = MacroProteinBg.copy(alpha = if (isDark) 0.35f else 0.8f),
            modifier = Modifier.weight(1f)
          ) {
            Column(
              modifier = Modifier.padding(8.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(text = "Proteína", style = MaterialTheme.typography.labelSmall, color = MacroProtein, fontWeight = FontWeight.Bold)
              Spacer(modifier = Modifier.height(2.dp))
              Text(text = "%.1fg".format(meal.totalProtein), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
            }
          }
          // Carbohidratos
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = MacroCarbsBg.copy(alpha = if (isDark) 0.35f else 0.8f),
            modifier = Modifier.weight(1f)
          ) {
            Column(
              modifier = Modifier.padding(8.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(text = "Carbos", style = MaterialTheme.typography.labelSmall, color = MacroCarbs, fontWeight = FontWeight.Bold)
              Spacer(modifier = Modifier.height(2.dp))
              Text(text = "%.1fg".format(meal.totalCarbs), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
            }
          }
          // Grasas
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = MacroFatBg.copy(alpha = if (isDark) 0.35f else 0.8f),
            modifier = Modifier.weight(1f)
          ) {
            Column(
              modifier = Modifier.padding(8.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(text = "Grasas", style = MaterialTheme.typography.labelSmall, color = MacroFat, fontWeight = FontWeight.Bold)
              Spacer(modifier = Modifier.height(2.dp))
              Text(text = "%.1fg".format(meal.totalFat), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Desglose de Grasas
        Text(
          text = "Calidad de Grasas",
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Grasas Buenas
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isDark) Color(0xFF0F2624) else Color(0xFFE0F7FA),
            modifier = Modifier.weight(1f)
          ) {
            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "Buenas", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = Color(0xFF00B4D8), fontWeight = FontWeight.Bold)
              Text(text = "%.1fg".format(goodFats), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            }
          }
          // Saturadas
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isDark) Color(0xFF2E2416) else Color(0xFFFFF7ED),
            modifier = Modifier.weight(1f)
          ) {
            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "Saturadas", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = Color(0xFFEA580C), fontWeight = FontWeight.Bold)
              Text(text = "%.1fg".format(meal.totalSaturatedFat), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            }
          }
          // Trans
          val hasTrans = meal.totalTransFat > 0.05f
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (hasTrans) (if (isDark) Color(0xFF3B1818) else Color(0xFFFEE2E2)) else (if (isDark) Color(0xFF162E20) else Color(0xFFDCFCE7)),
            modifier = Modifier.weight(1f)
          ) {
            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = if (hasTrans) "Trans ⚠️" else "Trans ✨", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = if (hasTrans) Color(0xFFDC2626) else Color(0xFF16A34A), fontWeight = FontWeight.Bold)
              Text(text = "%.1fg".format(meal.totalTransFat), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            }
          }
        }

        if (meal.ingredients.isNotEmpty()) {
          Spacer(modifier = Modifier.height(14.dp))
          Text(
            text = "Ingredientes (${meal.ingredients.size})",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(6.dp))
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            meal.ingredients.forEachIndexed { index, ing ->
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                  ) {
                    Text(text = ing.emoji, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                      Text(text = ing.foodName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                      Text(
                        text = "${ing.grams.toInt()}g • ${ing.calories} kcal • P: ${ing.protein.toInt()}g C: ${ing.carbs.toInt()}g G: ${ing.fat.toInt()}g",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                  }

                  if (onRemoveIngredient != null) {
                    IconButton(
                      onClick = { onRemoveIngredient(index) },
                      modifier = Modifier.size(32.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Quitar alimento",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                      )
                    }
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          if (onDeleteMeal != null) {
            Button(
              onClick = onDeleteMeal,
              shape = RoundedCornerShape(16.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
              ),
              modifier = Modifier.weight(1f)
            ) {
              Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(text = "Borrar todo", fontWeight = FontWeight.Bold)
            }
          }

          Button(
            onClick = onDismiss,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.weight(1f)
          ) {
            Text(text = "Cerrar", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
