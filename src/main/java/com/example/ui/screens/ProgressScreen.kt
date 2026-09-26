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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.DailySummary
import com.example.data.model.UserProfile
import com.example.data.model.WeightEntry
import com.example.data.model.WorkoutSession
import com.example.ui.components.LiquidCyanGlow
import com.example.ui.components.LiquidDarkShinyWhite
import com.example.ui.components.LiquidGlassBackground
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidLightGrayBorder
import com.example.ui.components.NutritionAveragesCard
import com.example.ui.components.charts.DashboardChartsTableroView

/**
 * Tablero de Progreso con estética Liquid Glass:
 * - Modo Claro: Vidrio translúcido esmerilado con contorno en gris suave equilibrado ("gris agradable a la vista").
 * - Modo Oscuro: Vidrio ahumado translúcido con contorno blanco brillante/resplandeciente ("blanco más claro y brillosito").
 * - Totalmente vinculado con el historial persistente de peso, calorías y entrenamientos.
 */
@Composable
fun ProgressScreen(
  userProfile: UserProfile,
  weightEntries: List<WeightEntry>,
  onLogWeight: (Float) -> Unit,
  dailySummary: DailySummary = DailySummary(),
  workoutSessions: List<WorkoutSession> = emptyList(),
  cloudSyncStatus: String = "Sincronizado",
  isCloudSyncing: Boolean = false,
  onForceSyncClick: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val systemInDark = isSystemInDarkTheme()
  val isDark = if (userProfile.followSystemTheme) systemInDark else userProfile.isDarkTheme
  var showLogWeightDialog by remember { mutableStateOf(false) }

  LiquidGlassBackground(
    isDark = isDark,
    modifier = modifier
      .fillMaxSize()
      .statusBarsPadding()
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .testTag("progress_screen_scroll"),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
      verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
      // Header Section
      item {
        Column {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column {
              Text(
                text = "Tablero de Progreso",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
              )
              Spacer(modifier = Modifier.height(3.dp))
              Text(
                text = "Historial biométrico y balance calórico persistente",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      // Interactive Tablero View (Weight curves, Calorie balances, Workouts)
      item {
        DashboardChartsTableroView(
          userProfile = userProfile,
          dailySummary = dailySummary,
          weightEntries = weightEntries,
          workoutSessions = workoutSessions,
          cloudSyncStatus = cloudSyncStatus,
          isCloudSyncing = isCloudSyncing,
          isDark = isDark,
          onLogWeightClick = { showLogWeightDialog = true },
          onForceSyncClick = onForceSyncClick
        )
      }

      // Goal Evolution Card in Liquid Glass
      item {
        val diff = userProfile.weightKg - userProfile.targetWeightKg
        val isLoss = diff > 0
        val isTargetReached = Math.abs(diff) < 0.2f

        LiquidGlassCard(
          shape = RoundedCornerShape(24.dp),
          isDark = isDark,
          ambientGlow = isDark,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(
                  if (isDark) Color.White.copy(alpha = 0.08f)
                  else Color(0xFFF1F5F9).copy(alpha = 0.90f)
                )
                .border(
                  BorderStroke(
                    1.dp,
                    if (isDark) Color.White.copy(alpha = 0.22f)
                    else Color(0xFFE2E8F0)
                  ),
                  CircleShape
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (isTargetReached) Icons.Default.CheckCircle else if (isLoss) Icons.AutoMirrored.Filled.TrendingDown else Icons.AutoMirrored.Filled.TrendingUp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = when {
                  isTargetReached -> "¡Objetivo alcanzado con éxito!"
                  isLoss -> "A %.1f kg de tu meta estética".format(diff)
                  else -> "A %.1f kg de tu meta de volumen".format(-diff)
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "Peso actual: %.1f kg • Meta: %.1f kg".format(userProfile.weightKg, userProfile.targetWeightKg),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Surface(
              onClick = { showLogWeightDialog = true },
              shape = RoundedCornerShape(14.dp),
              color = if (isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.90f),
              border = BorderStroke(
                1.dp,
                if (isDark) Color.White.copy(alpha = 0.20f) else Color(0xFFCBD5E1)
              ),
              modifier = Modifier.testTag("button_progress_log_weight")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Scale,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  "Pesar",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }
        }
      }

      // Nutrition Daily/Weekly Averages linked to real intake
      item {
        val calculatedAvgCals = if (dailySummary.totalCaloriesConsumed > 0) dailySummary.totalCaloriesConsumed else userProfile.dailyCalories
        val calculatedAvgProtein = if (dailySummary.totalProtein > 0) dailySummary.totalProtein.toInt() else userProfile.proteinGoalGrams
        val calculatedAvgCarbs = if (dailySummary.totalCarbs > 0) dailySummary.totalCarbs.toInt() else userProfile.carbsGoalGrams
        val calculatedAvgFat = if (dailySummary.totalFat > 0) dailySummary.totalFat.toInt() else userProfile.fatGoalGrams

        NutritionAveragesCard(
          avgCalories = calculatedAvgCals,
          avgProtein = calculatedAvgProtein,
          avgCarbs = calculatedAvgCarbs,
          avgFat = calculatedAvgFat,
          isDark = isDark
        )
      }

      // Gym & Consistency Widget linked to persistent workout history
      item {
        val totalSessions = workoutSessions.size
        val thisWeekWorkouts = workoutSessions.take(7).size

        LiquidGlassCard(
          shape = RoundedCornerShape(24.dp),
          isDark = isDark,
          ambientGlow = isDark,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                  if (isDark) Color.White.copy(alpha = 0.08f)
                  else Color(0xFFF1F5F9).copy(alpha = 0.90f)
                )
                .border(
                  BorderStroke(
                    1.dp,
                    if (isDark) Color.White.copy(alpha = 0.22f)
                    else Color(0xFFE2E8F0)
                  ),
                  CircleShape
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
              Text(
                text = "Historial de Gimnasio Persistente",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "$thisWeekWorkouts entrenamientos registrados ($totalSessions en historial total)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(72.dp))
      }
    }
  }

  if (showLogWeightDialog) {
    LogWeightDialog(
      currentWeight = userProfile.weightKg,
      isDark = isDark,
      onDismiss = { showLogWeightDialog = false },
      onConfirm = { newWeight ->
        onLogWeight(newWeight)
        showLogWeightDialog = false
      }
    )
  }
}

@Composable
fun LogWeightDialog(
  currentWeight: Float,
  isDark: Boolean,
  onDismiss: () -> Unit,
  onConfirm: (Float) -> Unit
) {
  var weightText by remember { mutableStateOf("%.1f".format(currentWeight)) }

  Dialog(onDismissRequest = onDismiss) {
    LiquidGlassCard(
      shape = RoundedCornerShape(28.dp),
      isDark = isDark,
      ambientGlow = isDark,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp)
      ) {
        Text(
          text = "Registrar peso actual",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Tu peso se actualizará en la base de datos persistente y en las gráficas de evolución.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
          value = weightText,
          onValueChange = { weightText = it },
          label = { Text("Peso en kilogramos (kg)") },
          singleLine = true,
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_new_weight")
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          TextButton(onClick = onDismiss) {
            Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Surface(
            onClick = {
              val w = weightText.replace(',', '.').toFloatOrNull() ?: currentWeight
              onConfirm(w)
            },
            shape = RoundedCornerShape(16.dp),
            color = if (isDark) Color.White.copy(alpha = 0.10f) else Color.White.copy(alpha = 0.90f),
            border = BorderStroke(
              1.dp,
              if (isDark) Color.White.copy(alpha = 0.25f) else Color(0xFFCBD5E1)
            ),
            modifier = Modifier.testTag("confirm_weight_button")
          ) {
            Text(
              "Guardar peso",
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          }
        }
      }
    }
  }
}
