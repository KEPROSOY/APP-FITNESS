package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailySummary
import com.example.data.model.MealRecord
import com.example.data.model.MealType
import com.example.data.model.UserProfile
import com.example.data.model.calculateBmi
import com.example.ui.components.CalorieProgressCard
import com.example.ui.components.LiquidGlassBackground
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidMintGlow
import com.example.ui.components.MacroMetricsRow
import com.example.ui.components.MealItemRow
import com.example.ui.components.MealNutrientDetailDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
  userProfile: UserProfile,
  dailySummary: DailySummary,
  onAddMealClick: (MealType) -> Unit,
  onAiScanClick: () -> Unit,
  onDeleteMealClick: (Long) -> Unit,
  onRemoveIngredientFromMeal: ((Long, Int) -> Unit)? = null,
  onToggleTheme: () -> Unit = {},
  onLogout: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val formattedDate = rememberFormattedDate()
  val systemInDark = isSystemInDarkTheme()
  val isDark = if (userProfile.followSystemTheme) systemInDark else userProfile.isDarkTheme
  val bmiResult = calculateBmi(userProfile.weightKg, userProfile.heightCm)
  var showLogoutConfirm by remember { mutableStateOf(false) }
  var mealToDeleteConfirm by remember { mutableStateOf<MealRecord?>(null) }
  var selectedMealForDetail by remember { mutableStateOf<MealRecord?>(null) }

  LiquidGlassBackground(
    isDark = isDark,
    modifier = modifier.fillMaxSize()
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .testTag("home_screen_scroll"),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Top Greeting & Date Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Hola, ${userProfile.name} 👋",
              style = MaterialTheme.typography.headlineMedium,
              fontWeight = FontWeight.ExtraBold,
              letterSpacing = (-0.5).sp,
              color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              if (userProfile.streakCount > 0) {
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = if (isDark) Color(0xFF451A03) else Color(0xFFCBD5E1).copy(alpha = 0.6f),
                  contentColor = if (isDark) Color(0xFFFDE68A) else Color(0xFF0F172A),
                  border = BorderStroke(1.dp, if (isDark) Color(0xFF78350F) else Color(0xFF94A3B8).copy(alpha = 0.5f))
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(
                      Icons.Default.LocalFireDepartment,
                      contentDescription = "Racha",
                      tint = if (isDark) Color(0xFFF59E0B) else Color(0xFFD97706),
                      modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = "${userProfile.streakCount} días", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Theme toggle button (Modo Claro / Modo Oscuro)
            IconButton(
              onClick = onToggleTheme,
              modifier = Modifier
                .clip(CircleShape)
                .background(
                  if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
                )
                .size(40.dp)
                .testTag("button_home_toggle_theme")
            ) {
              Icon(
                imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                contentDescription = if (isDark) "Cambiar a modo claro" else "Cambiar a modo oscuro",
                tint = if (isDark) Color(0xFFFACC15) else Color(0xFF334155),
                modifier = Modifier.size(20.dp)
              )
            }

            // Logout button (Cerrar sesión)
            IconButton(
              onClick = { showLogoutConfirm = true },
              modifier = Modifier
                .clip(CircleShape)
                .background(
                  if (isDark) Color(0xFF331D1D) else Color(0xFFFEE2E2)
                )
                .size(40.dp)
                .testTag("button_home_logout")
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = "Cerrar sesión",
                tint = if (isDark) Color(0xFFF87171) else Color(0xFFDC2626),
                modifier = Modifier.size(18.dp)
              )
            }

            // Streak badge in Liquid Glass Pill
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(
                  if (isDark) Color(0xFF132B21).copy(alpha = 0.8f)
                  else Color(0xFFD8F5E8).copy(alpha = 0.85f)
                )
                .border(
                  BorderStroke(
                    1.dp,
                    if (isDark) Color(0xFF34D399).copy(alpha = 0.4f)
                    else Color(0xFF0FB477).copy(alpha = 0.35f)
                  ),
                  RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .testTag("streak_badge")
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.LocalFireDepartment,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "7 días",
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }
          }
        }
      }

      // IMC (Índice de Masa Corporal) Indicator Card
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(bmiResult.colorHex).copy(alpha = 0.18f) else Color(0xFFCBD5E1).copy(alpha = 0.50f)
          ),
          border = BorderStroke(1.dp, if (isDark) Color(bmiResult.colorHex).copy(alpha = 0.35f) else Color(0xFF94A3B8).copy(alpha = 0.45f)),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("card_home_bmi")
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = CircleShape,
              color = Color(bmiResult.colorHex).copy(alpha = if (isDark) 0.25f else 0.15f),
              border = BorderStroke(1.dp, Color(bmiResult.colorHex).copy(alpha = 0.4f)),
              modifier = Modifier.size(42.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(
                  text = "IMC",
                  fontWeight = FontWeight.Black,
                  fontSize = 12.sp,
                  color = Color(bmiResult.colorHex)
                )
              }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "${bmiResult.bmi} IMC",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = if (isDark) Color(bmiResult.colorHex) else Color(0xFF475569)
                ) {
                  Text(
                    text = bmiResult.category,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                  )
                }
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = bmiResult.detail,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      // Calorie Big Circular Progress Card
      item {
        CalorieProgressCard(summary = dailySummary, isDark = isDark)
      }

      // Macronutrient Split Row
      item {
        MacroMetricsRow(summary = dailySummary, isDark = isDark)
      }

      // AI Quick Banner Action in Liquid Glass
      item {
        LiquidGlassCard(
          shape = RoundedCornerShape(24.dp),
          elevation = 8.dp,
          isDark = isDark,
          ambientGlow = true,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                  .size(42.dp)
                  .shadow(6.dp, CircleShape, spotColor = if (isDark) LiquidMintGlow.copy(alpha = 0.4f) else Color(0x221E293B))
                  .clip(CircleShape)
                  .background(
                    if (isDark) {
                      Brush.linearGradient(listOf(Color(0xFF38BDF8), Color(0xFF0284C7)))
                    } else {
                      Brush.linearGradient(listOf(Color(0xFF475569), Color(0xFF1E293B)))
                    }
                  )
              ) {
                Icon(
                  imageVector = Icons.Default.AutoAwesome,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(22.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "¿Vas a comer?",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "Escanea tu plato con la IA de SYVRA",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            Button(
              onClick = onAiScanClick,
              shape = RoundedCornerShape(16.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = if (isDark) MaterialTheme.colorScheme.primary else Color(0xFF334155),
                contentColor = Color.White
              ),
              contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
              modifier = Modifier
                .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = if (isDark) LiquidMintGlow.copy(alpha = 0.4f) else Color(0x221E293B))
                .testTag("home_quick_ai_scan_button")
            ) {
              Text(
                text = "Escanear",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      // Section Header: Comidas de hoy
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Comidas de hoy",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )

          Button(
            onClick = { onAddMealClick(MealType.COMIDA) },
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isDark) MaterialTheme.colorScheme.primaryContainer else Color(0xFFCBD5E1).copy(alpha = 0.65f),
              contentColor = if (isDark) MaterialTheme.colorScheme.onPrimaryContainer else Color(0xFF0F172A)
            ),
            border = BorderStroke(1.dp, if (isDark) Color.Transparent else Color(0xFF94A3B8).copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
            modifier = Modifier.testTag("home_header_add_meal_button")
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Agregar",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      // List of Meals (Desayuno, Comida, Cena, Snack)
      item {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          MealType.entries.forEach { mealType ->
            val meal = dailySummary.meals.find { it.mealType == mealType }
            MealItemRow(
              mealType = mealType,
              meal = meal,
              onAddClick = { onAddMealClick(mealType) },
              onDeleteClick = if (meal != null) { { mealToDeleteConfirm = meal } } else null,
              onMealClick = if (meal != null) { { selectedMealForDetail = meal } } else null,
              isDark = isDark
            )
          }
        }
      }

      // Bottom padding for navigation bar
      item {
        Spacer(modifier = Modifier.height(72.dp))
      }
    }

    // Meal Nutrient & Macros Inspection Dialog
    selectedMealForDetail?.let { meal ->
      MealNutrientDetailDialog(
        meal = meal,
        onDismiss = { selectedMealForDetail = null },
        onDeleteMeal = {
          onDeleteMealClick(meal.id)
          selectedMealForDetail = null
        },
        onRemoveIngredient = if (onRemoveIngredientFromMeal != null) {
          { ingIndex ->
            onRemoveIngredientFromMeal(meal.id, ingIndex)
            val updatedIngredients = meal.ingredients.toMutableList().apply {
              if (ingIndex in indices) removeAt(ingIndex)
            }
            if (updatedIngredients.isEmpty()) {
              selectedMealForDetail = null
            } else {
              val mergedCals = updatedIngredients.sumOf { it.calories }
              val mergedP = updatedIngredients.sumOf { it.protein.toDouble() }.toFloat()
              val mergedC = updatedIngredients.sumOf { it.carbs.toDouble() }.toFloat()
              val mergedF = updatedIngredients.sumOf { it.fat.toDouble() }.toFloat()
              val mergedSat = updatedIngredients.sumOf { it.saturatedFat.toDouble() }.toFloat()
              val mergedTrans = updatedIngredients.sumOf { it.transFat.toDouble() }.toFloat()
              selectedMealForDetail = meal.copy(
                totalCalories = mergedCals,
                totalProtein = mergedP,
                totalCarbs = mergedC,
                totalFat = mergedF,
                totalSaturatedFat = mergedSat,
                totalTransFat = mergedTrans,
                ingredients = updatedIngredients
              )
            }
          }
        } else null,
        isDark = isDark
      )
    }

    // Meal Deletion Confirmation Dialog
    mealToDeleteConfirm?.let { meal ->
      AlertDialog(
        onDismissRequest = { mealToDeleteConfirm = null },
        title = { Text("¿Eliminar ${meal.mealType.displayName.lowercase()}?", fontWeight = FontWeight.Bold) },
        text = { Text("Se borrarán los alimentos registrados en esta comida (${meal.totalCalories} kcal).") },
        confirmButton = {
          Button(
            onClick = {
              onDeleteMealClick(meal.id)
              mealToDeleteConfirm = null
              if (selectedMealForDetail?.id == meal.id) {
                selectedMealForDetail = null
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
          ) {
            Text("Eliminar")
          }
        },
        dismissButton = {
          TextButton(onClick = { mealToDeleteConfirm = null }) {
            Text("Cancelar")
          }
        }
      )
    }

    // Logout Confirmation Dialog
    if (showLogoutConfirm) {
      AlertDialog(
        onDismissRequest = { showLogoutConfirm = false },
        title = { Text("¿Cerrar sesión?", fontWeight = FontWeight.Bold) },
        text = { Text("¿Estás seguro de que deseas cerrar tu sesión en SYVRA?") },
        confirmButton = {
          Button(
            onClick = {
              showLogoutConfirm = false
              onLogout()
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
          ) {
            Text("Cerrar sesión")
          }
        },
        dismissButton = {
          TextButton(onClick = { showLogoutConfirm = false }) {
            Text("Cancelar")
          }
        }
      )
    }
  }
}

@Composable
private fun rememberFormattedDate(): String {
  val date = Date()
  val spanishLocale = Locale.forLanguageTag("es-ES")
  val dayFormat = SimpleDateFormat("EEEE, d 'de' MMMM", spanishLocale)
  val raw = dayFormat.format(date)
  return raw.replaceFirstChar { if (it.isLowerCase()) it.titlecase(spanishLocale) else it.toString() }
}
