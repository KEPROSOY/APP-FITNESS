package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import android.graphics.BitmapFactory
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.*
import com.example.data.repository.NutritionRepository
import com.example.fitness.FitnessEngine
import com.example.ui.components.Humanoid3DViewer
import com.example.data.ExerciseDatabase
import com.example.ui.components.LiquidGlassBackground
import com.example.ui.components.LiquidGlassCard
import com.example.viewmodel.GymViewModel
import com.example.viewmodel.OneRmCalculatorState
import kotlin.math.roundToInt

enum class GymSubTab(val title: String, val emoji: String) {
  ENTRENAMIENTO("Cargas & Gym", "🏋️"),
  SIMETRIA("Rangos Estéticos", "👑"),
  RUTINA("Rutinas IA", "🧠"),
  DEMO("Guía Ejercicios", "🎬")
}


@Composable
fun SymmetryScreen(
  gymViewModel: GymViewModel = viewModel(),
  isScanning: Boolean = false,
  scanResult: SymmetryResult? = null,
  onTriggerScan: (Bitmap) -> Unit = {},
  onClearScan: () -> Unit = {},
  isDarkTheme: Boolean? = null,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val isDark = isDarkTheme ?: (MaterialTheme.colorScheme.surface.luminance() < 0.5f)

  val gymUiState by gymViewModel.uiState.collectAsState()
  val calculatorState by gymViewModel.calculatorState.collectAsState()

  var currentSubTab by remember { mutableStateOf(GymSubTab.ENTRENAMIENTO) }
  var showAddWorkoutDialog by remember { mutableStateOf(false) }
  var editingSession by remember { mutableStateOf<WorkoutSession?>(null) }
  var showResetConfirmDialog by remember { mutableStateOf(false) }
  var showCalculatorDialog by remember { mutableStateOf(false) }
  var showExerciseDetailFullScreen by remember { mutableStateOf(false) }
  var selectedRoutineDay by remember { mutableStateOf(gymViewModel.getCurrentDayName()) }
  var selectedGuideId by remember { mutableStateOf("press_militar") }

  val launcher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      try {
        context.contentResolver.openInputStream(uri)?.use { stream ->
          val bitmap = BitmapFactory.decodeStream(stream)
          if (bitmap != null) {
            onTriggerScan(bitmap)
          }
        }
      } catch (_: Exception) {}
    }
  }

  LiquidGlassBackground(
    isDark = isDark,
    modifier = modifier.fillMaxSize()
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // Top Header & Sub-tab Switcher
      Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Gimnasio & Simetría",
              style = MaterialTheme.typography.headlineMedium,
              fontWeight = FontWeight.ExtraBold,
              letterSpacing = (-0.5).sp,
              color = MaterialTheme.colorScheme.onBackground
            )
            Text(
              text = "Cálculo 1RM, volumen de carga y estética V-Taper",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          IconButton(
            onClick = { showCalculatorDialog = true },
            modifier = Modifier
              .clip(CircleShape)
              .background(if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0))
              .testTag("button_open_1rm_calculator")
          ) {
            Icon(
              imageVector = Icons.Default.Calculate,
              contentDescription = "Calculadora 1RM",
              tint = MaterialTheme.colorScheme.primary
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Segmented Control (4 Tabs)
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = if (isDark) Color(0xFF0C1322) else Color(0xFFCBD5E1).copy(alpha = 0.55f),
          border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0xFF94A3B8).copy(alpha = 0.45f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          LazyRow(
            modifier = Modifier
              .fillMaxWidth()
              .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            items(GymSubTab.values()) { tab ->
              val isSelected = tab == currentSubTab
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(12.dp))
                  .background(
                    if (isSelected) {
                      if (isDark) MaterialTheme.colorScheme.primary else Color(0xFF334155)
                    } else Color.Transparent
                  )
                  .clickable { currentSubTab = tab }
                  .padding(horizontal = 14.dp, vertical = 9.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "${tab.emoji} ${tab.title}",
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }

      }

      // Content based on selected sub tab
      when (currentSubTab) {
        GymSubTab.ENTRENAMIENTO -> {
          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            // Card de Resumen Diario de Gimnasio & Tracking de Volumen Total
            item {
              val gym = gymUiState.todaySummary
              LiquidGlassCard(
                shape = RoundedCornerShape(24.dp),
                elevation = 8.dp,
                isDark = isDark,
                ambientGlow = true,
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(18.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Box(
                        modifier = Modifier
                          .size(40.dp)
                          .clip(CircleShape)
                          .background(Color(0xFF10B981).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                      ) {
                        Icon(
                          Icons.Default.Whatshot,
                          contentDescription = null,
                          tint = Color(0xFF10B981),
                          modifier = Modifier.size(24.dp)
                        )
                      }
                      Spacer(modifier = Modifier.width(12.dp))
                      Column {
                        Text(
                          text = "Gasto en Gym Hoy",
                          style = MaterialTheme.typography.titleMedium,
                          fontWeight = FontWeight.Bold
                        )
                        Text(
                          text = "Sincronizado con balance nutricional",
                          style = MaterialTheme.typography.bodySmall,
                          color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                      }
                    }

                    Text(
                      text = "${gym?.totalCaloriesBurned ?: 0} kcal",
                      style = MaterialTheme.typography.titleLarge,
                      fontWeight = FontWeight.ExtraBold,
                      color = Color(0xFF10B981)
                    )
                  }

                  Spacer(modifier = Modifier.height(14.dp))
                  HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                  Spacer(modifier = Modifier.height(12.dp))

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                  ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Text(
                        text = "Volumen Hoy",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                      Text(
                        text = "${((gym?.totalVolumeKg ?: 0f)).roundToInt()} kg",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                      )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Text(
                        text = "Volumen Total",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                      Text(
                        text = "${gymUiState.totalAllTimeVolumeKg.roundToInt()} kg",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                      )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Text(
                        text = "Sesiones",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                      Text(
                        text = "${gymUiState.totalSessionsCount}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                      )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Text(
                        text = "Media / Sesión",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                      Text(
                        text = "${gymUiState.averageSessionVolumeKg.roundToInt()} kg",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                      )
                    }
                  }
                }
              }
            }

            // Aviso de Persistencia Room (Historial estructurado que no se borra a las 24 horas)
            item {
              Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier
                      .size(36.dp)
                      .clip(CircleShape)
                      .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      Icons.Default.CalendarMonth,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(20.dp)
                    )
                  }
                  Spacer(modifier = Modifier.width(12.dp))
                  Column {
                    Text(
                      text = "Persistencia de Historial Gym Activa",
                      style = MaterialTheme.typography.labelMedium,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                      text = "A diferencia del contador de calorías diario, tus cargas, repeticiones, volumen total y récords 1RM no se borran a las 24 horas y quedan guardados permanentemente por días y semanas en Room.",
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }
              }
            }

            // Nivel de Gamificación y Progreso (Inicia desde 0 y otorga XP al completar ejercicios)
            item {
              GymUserLevelCard(
                userLevel = gymUiState.userLevel,
                isDark = isDark,
                onResetProgress = { showResetConfirmDialog = true }
              )
            }

            // Botón Registrar Nueva Sesión

            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Button(
                  onClick = { showAddWorkoutDialog = true },
                  modifier = Modifier
                    .weight(1.3f)
                    .height(52.dp)
                    .testTag("button_add_workout_session"),
                  shape = RoundedCornerShape(16.dp),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                  )
                ) {
                  Icon(Icons.Default.Add, contentDescription = null)
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Registrar Gym", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                OutlinedButton(
                  onClick = { showCalculatorDialog = true },
                  modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("button_open_brzycki_calculator"),
                  shape = RoundedCornerShape(16.dp),
                  border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                ) {
                  Icon(Icons.Default.Calculate, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("1RM Brzycki", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                }
              }
            }

            // Sección Récords Personales (PRs)
            if (gymUiState.personalRecords.isNotEmpty()) {
              item {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "🏆 Récords Personales (PRs)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = "${gymUiState.personalRecords.size} récords",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              item {
                LazyRow(
                  horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                  items(gymUiState.personalRecords) { pr ->
                    Card(
                      shape = RoundedCornerShape(18.dp),
                      colors = CardDefaults.cardColors(
                        containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC)
                      ),
                      border = BorderStroke(
                        1.dp,
                        if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
                      ),
                      modifier = Modifier.width(200.dp)
                    ) {
                      Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                          modifier = Modifier.fillMaxWidth(),
                          horizontalArrangement = Arrangement.SpaceBetween,
                          verticalAlignment = Alignment.CenterVertically
                        ) {
                          Text(
                            text = pr.category.iconEmoji,
                            fontSize = 20.sp
                          )
                          Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF59E0B).copy(alpha = 0.15f)
                          ) {
                            Text(
                              text = "1RM: ${pr.estimated1Rm}kg",
                              style = MaterialTheme.typography.labelSmall,
                              fontWeight = FontWeight.Bold,
                              color = Color(0xFFD97706),
                              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                          }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                          text = pr.exerciseName,
                          style = MaterialTheme.typography.titleSmall,
                          fontWeight = FontWeight.Bold,
                          maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                          text = "${pr.bestWeightKg} kg × ${pr.bestReps} reps",
                          style = MaterialTheme.typography.bodyMedium,
                          color = MaterialTheme.colorScheme.primary,
                          fontWeight = FontWeight.SemiBold
                        )
                      }
                    }
                  }
                }
              }
            }

            // Filtro por Categoría Estética
            item {
              Column {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "📋 Historial de Entrenamientos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                  )
                  if (gymUiState.selectedCategoryFilter != null) {
                    TextButton(onClick = { gymViewModel.filterByCategory(null) }) {
                      Text("Ver todos", fontSize = 12.sp)
                    }
                  }
                }

                LazyRow(
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                  modifier = Modifier.padding(vertical = 4.dp)
                ) {
                  item {
                    FilterChip(
                      selected = gymUiState.selectedCategoryFilter == null,
                      onClick = { gymViewModel.filterByCategory(null) },
                      label = { Text("Todos", fontSize = 12.sp) }
                    )
                  }
                  items(AestheticMuscleCategory.values()) { cat ->
                    val isSel = cat == gymUiState.selectedCategoryFilter
                    FilterChip(
                      selected = isSel,
                      onClick = { gymViewModel.filterByCategory(if (isSel) null else cat) },
                      label = { Text("${cat.iconEmoji} ${cat.displayName}", fontSize = 11.sp) }
                    )
                  }
                }
              }
            }

            if (gymUiState.sessions.isEmpty()) {
              item {
                Card(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(16.dp),
                  colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                  )
                ) {
                  Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    Text(
                      text = if (gymUiState.selectedCategoryFilter != null) {
                        "No hay entrenamientos para esta categoría."
                      } else {
                        "¡Historial listo desde 0!\nRegistra tu primer ejercicio o serie para empezar a ganar XP y subir de nivel."
                      },
                      textAlign = TextAlign.Center,
                      style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { showAddWorkoutDialog = true }) {
                      Icon(Icons.Default.Add, contentDescription = null)
                      Spacer(modifier = Modifier.width(6.dp))
                      Text("Registrar Mi Primer Ejercicio")
                    }
                  }
                }
              }
            } else {
              items(gymUiState.sessions) { session ->
                WorkoutSessionCard(
                  session = session,
                  volumeSummary = gymViewModel.getWorkoutVolumeSummary(session),
                  isDark = isDark,
                  onEdit = { editingSession = session },
                  onDelete = { gymViewModel.deleteWorkout(session.id) },
                  onOpenExerciseExecution = { exerciseName ->
                    val target = ExerciseDatabase.findExercise(exerciseName)
                    if (target != null) {
                      selectedGuideId = target.id
                      showExerciseDetailFullScreen = true
                    }
                  }
                )
              }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
          }
        }

        GymSubTab.SIMETRIA -> {
          val profile = gymUiState.aestheticProfile
          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            // 1. Tarjeta Perfil Global & Rango Estético
            item {
              LiquidGlassCard(
                shape = RoundedCornerShape(24.dp),
                elevation = 8.dp,
                isDark = isDark,
                ambientGlow = true,
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(20.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(profile.globalRank.symbol, fontSize = 34.sp)
                      Spacer(modifier = Modifier.width(12.dp))
                      Column {
                        Text(
                          text = "RANGO ${profile.globalRank.displayName.uppercase()}",
                          style = MaterialTheme.typography.titleLarge,
                          fontWeight = FontWeight.Black,
                          color = Color(profile.globalRank.colorHex)
                        )
                        Text(
                          text = profile.globalRank.description,
                          style = MaterialTheme.typography.bodySmall,
                          color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                      }
                    }

                    Surface(
                      shape = RoundedCornerShape(12.dp),
                      color = Color(profile.globalRank.colorHex).copy(alpha = 0.15f),
                      border = BorderStroke(1.dp, Color(profile.globalRank.colorHex).copy(alpha = 0.6f))
                    ) {
                      Text(
                        text = "Tier ${profile.globalRank.ordinal + 1}/7",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(profile.globalRank.colorHex),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(14.dp))
                  HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                  Spacer(modifier = Modifier.height(12.dp))

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                  ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Text(
                        text = "Ratio V-Taper",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                      Text(
                        text = "${profile.vTaperRatio}x",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                      )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Text(
                        text = "Puntaje Global",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                      Text(
                        text = "${profile.globalScore} pts",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                      )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Text(
                        text = "Músculos Rezagados",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                      Text(
                        text = if (profile.laggingCategories.isEmpty()) "0 (Óptimo)" else "${profile.laggingCategories.size}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (profile.laggingCategories.isEmpty()) Color(0xFF10B981) else MaterialTheme.colorScheme.error
                      )
                    }
                  }
                }
              }
            }

            // 2. Jerarquía de Rangos Estéticos (Escala Completa)
            item {
              Column {
                Text(
                  text = "🏆 Jerarquía de Rangos Estéticos",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  items(AestheticRank.values()) { rank ->
                    val isCurrent = rank == profile.globalRank
                    Surface(
                      shape = RoundedCornerShape(12.dp),
                      color = if (isCurrent) Color(rank.colorHex).copy(alpha = 0.25f)
                      else if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                      border = BorderStroke(
                        if (isCurrent) 2.dp else 1.dp,
                        if (isCurrent) Color(rank.colorHex) else Color.Transparent
                      )
                    ) {
                      Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Text(rank.symbol, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                          text = rank.displayName,
                          style = MaterialTheme.typography.labelSmall,
                          fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                          color = if (isCurrent) Color(rank.colorHex) else MaterialTheme.colorScheme.onSurface
                        )
                      }
                    }
                  }
                }
              }
            }

            // 3. Rangos y Progresión por Grupo Muscular
            item {
              Text(
                text = "⚡ Evaluación por Grupo Muscular",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
              )
            }

            items(profile.categoryRanks) { catRank ->
              AestheticMuscleRankCard(
                categoryRank = catRank,
                isLagging = profile.laggingCategories.contains(catRank.category),
                isDominant = profile.dominantCategories.contains(catRank.category),
                isDark = isDark
              )
            }

            // 4. Tarjeta de Proporción & V-Taper (Distribución de Volumen)
            item {
              val distribution = gymUiState.aestheticDistribution
              LiquidGlassCard(
                shape = RoundedCornerShape(24.dp),
                elevation = 8.dp,
                isDark = isDark,
                ambientGlow = true,
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(18.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column {
                      Text(
                        text = "Balance y Proporción Estética",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                      )
                      Text(
                        text = "Distribución de volumen hacia silueta V-Taper",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                    Icon(Icons.Default.PieChart, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                  }

                  Spacer(modifier = Modifier.height(16.dp))

                  distribution.forEach { dist ->
                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                          Text(dist.category.iconEmoji, fontSize = 16.sp)
                          Spacer(modifier = Modifier.width(6.dp))
                          Text(
                            text = dist.category.displayName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                          )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                          Text(
                            text = "${dist.percentageOfTotal.roundToInt()}% (${dist.actualVolumeKg.roundToInt()}kg)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                          )
                          if (dist.isLagging) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                              shape = RoundedCornerShape(6.dp),
                              color = MaterialTheme.colorScheme.errorContainer
                            ) {
                              Text(
                                "Rezagado",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                              )
                            }
                          } else if (dist.isDominant) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                              shape = RoundedCornerShape(6.dp),
                              color = Color(0xFF10B981).copy(alpha = 0.2f)
                            ) {
                              Text(
                                "Dominante",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF047857),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                              )
                            }
                          }
                        }
                      }

                      Spacer(modifier = Modifier.height(4.dp))
                      LinearProgressIndicator(
                        progress = { (dist.percentageOfTotal / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                          .fillMaxWidth()
                          .height(6.dp)
                          .clip(RoundedCornerShape(3.dp)),
                        color = when {
                          dist.isLagging -> MaterialTheme.colorScheme.error
                          dist.isDominant -> Color(0xFF10B981)
                          else -> MaterialTheme.colorScheme.primary
                        },
                        trackColor = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
                      )
                    }
                  }
                }
              }
            }

            // 5. Escáner Corporal AI (Gemini Vision)
            item {
              Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                  containerColor = if (isDark) Color(0xFF183B2D) else Color(0xFFD8F5E8)
                ),
                border = BorderStroke(
                  1.dp,
                  if (isDark) Color(0xFF34D399).copy(alpha = 0.4f) else Color(0xFF0FB477).copy(alpha = 0.3f)
                ),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(20.dp)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      Icons.Default.FlashOn,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                      text = "Escáner Corporal Symmetry AI",
                      style = MaterialTheme.typography.titleMedium,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                  }

                  Spacer(modifier = Modifier.height(8.dp))
                  Text(
                    text = "Analiza tu silueta con inteligencia artificial para recibir un plan de hipertrofia enfocado en corregir desbalances y potenciar tu radio hombros-cintura.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )

                  Spacer(modifier = Modifier.height(16.dp))

                  if (isScanning) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.Center,
                      modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                    ) {
                      CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                      Spacer(modifier = Modifier.width(12.dp))
                      Text("Procesando análisis estético con IA...", style = MaterialTheme.typography.bodyMedium)
                    }
                  } else {
                    Button(
                      onClick = {
                        launcher.launch(
                          PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                      },
                      modifier = Modifier.fillMaxWidth().height(48.dp),
                      shape = RoundedCornerShape(14.dp),
                      colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                      )
                    ) {
                      Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                      Spacer(modifier = Modifier.width(8.dp))
                      Text("Subir foto de cuerpo completo")
                    }
                  }
                }
              }
            }

            // Si hay resultado de escaneo
            if (scanResult != null) {
              item {
                Card(
                  shape = RoundedCornerShape(20.dp),
                  colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                  ),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Text(
                        text = "Diagnóstico Estético",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                      )
                      IconButton(onClick = onClearScan) {
                        Icon(Icons.Default.Delete, contentDescription = "Limpiar", tint = MaterialTheme.colorScheme.error)
                      }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = scanResult.advice, style = MaterialTheme.typography.bodyMedium)

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(text = "Fortalezas:", fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                    scanResult.strengths.forEach { s -> Text("• $s", style = MaterialTheme.typography.bodySmall) }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Áreas a potenciar:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    scanResult.weaknesses.forEach { w -> Text("• $w", style = MaterialTheme.typography.bodySmall) }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                      text = "Rutina Compensatoria Generada:",
                      fontWeight = FontWeight.Bold,
                      style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    scanResult.routine.forEach { day ->
                      Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                      ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                          Text(text = day.day, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                          day.exercises.forEach { ex ->
                            Text(
                              text = "• ${ex.name} — ${ex.sets} series × ${ex.reps}",
                              style = MaterialTheme.typography.bodySmall
                            )
                          }
                        }
                      }
                    }
                  }
                }
              }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
          }
        }

        GymSubTab.RUTINA -> {
          val profile = gymUiState.aestheticProfile
          val dailyRecommendation = FitnessEngine.generateDailyRecommendation(selectedRoutineDay, profile)
          val weekDays = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            // Selector de Días de la Semana
            item {
              Text(
                text = "📅 Planificación Inteligente de Simetría",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(6.dp))
              LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                items(weekDays) { day ->
                  val isSelected = day.equals(selectedRoutineDay, ignoreCase = true)
                  FilterChip(
                    selected = isSelected,
                    onClick = { selectedRoutineDay = day },
                    label = { Text(day) },
                    colors = FilterChipDefaults.filterChipColors(
                      selectedContainerColor = MaterialTheme.colorScheme.primary,
                      selectedLabelColor = Color.White
                    )
                  )
                }
              }
            }

            // Banner Recomendación Diaria
            item {
              Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                  containerColor = if (isDark) Color(0xFF132A22) else Color(0xFFE8F5E9)
                ),
                border = BorderStroke(
                  1.dp,
                  if (isDark) Color(0xFF10B981).copy(alpha = 0.4f) else Color(0xFF10B981).copy(alpha = 0.3f)
                ),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(18.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = "${dailyRecommendation.dayOfWeek} • ${dailyRecommendation.title}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                      )
                      Spacer(modifier = Modifier.height(4.dp))
                      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                          shape = RoundedCornerShape(8.dp),
                          color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ) {
                          Text(
                            text = "${dailyRecommendation.primaryFocus.iconEmoji} ${dailyRecommendation.primaryFocus.displayName}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                          )
                        }
                        if (dailyRecommendation.secondaryFocus != null) {
                          Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                          ) {
                            Text(
                              text = "${dailyRecommendation.secondaryFocus.iconEmoji} ${dailyRecommendation.secondaryFocus.displayName}",
                              style = MaterialTheme.typography.labelSmall,
                              fontWeight = FontWeight.Bold,
                              color = MaterialTheme.colorScheme.secondary,
                              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                          }
                        }
                      }
                    }

                    Icon(
                      Icons.Default.Psychology,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(36.dp)
                    )
                  }

                  Spacer(modifier = Modifier.height(14.dp))

                  // Razón biomecánica
                  Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isDark) Color(0xFF0F1E19) else Color(0xFFFFFFFF),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                      Icon(
                        Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = Color(0xFFEAB308),
                        modifier = Modifier.size(20.dp)
                      )
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(
                        text = dailyRecommendation.rationale,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                  }
                }
              }
            }

            // Lista de Ejercicios Sugeridos
            item {
              Text(
                text = "🏋️ Ejercicios Prescritos para Hoy",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
              )
            }

            items(dailyRecommendation.suggestedExercises) { exercise ->
              val matchingGuide = remember(exercise.name) { ExerciseDatabase.findExercise(exercise.name) }
              Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                  containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFFFFFFF)
                ),
                border = BorderStroke(
                  1.dp,
                  if (isDark) MaterialTheme.colorScheme.primary.copy(alpha = 0.35f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("suggested_exercise_${exercise.name.replace(" ", "_")}")
                  .clickable {
                    if (matchingGuide != null) {
                      selectedGuideId = matchingGuide.id
                      showExerciseDetailFullScreen = true
                    }
                  }
              ) {
                Column(modifier = Modifier.padding(16.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = exercise.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                      )
                      if (matchingGuide != null) {
                        Text(
                          text = "${matchingGuide.targetMuscleGroup} • ${matchingGuide.equipment}",
                          style = MaterialTheme.typography.bodySmall,
                          color = MaterialTheme.colorScheme.primary
                        )
                      }
                    }

                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ) {
                      Text(
                        text = "RPE ${exercise.targetRpe}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(8.dp))

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "⚡ ${exercise.sets} series × ${exercise.repsRange} reps",
                      style = MaterialTheme.typography.bodyMedium,
                      fontWeight = FontWeight.SemiBold,
                      color = Color(0xFF10B981)
                    )
                    if (matchingGuide != null) {
                      Text(
                        text = "⏱️ Tempo: ${matchingGuide.tempo}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(6.dp))

                  Text(
                    text = "🎯 Enfoque Biomecánico: ${exercise.focusNote}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )

                  Spacer(modifier = Modifier.height(12.dp))

                  // Botones interactivos para revisar la ejecución correcta
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    Button(
                      onClick = {
                        if (matchingGuide != null) {
                          selectedGuideId = matchingGuide.id
                          showExerciseDetailFullScreen = true
                        }
                      },
                      modifier = Modifier
                        .weight(1.2f)
                        .height(42.dp),
                      shape = RoundedCornerShape(12.dp),
                      colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                      )
                    ) {
                      Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                      )
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = "Revisar Ejecución",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }

                    OutlinedButton(
                      onClick = {
                        if (matchingGuide != null) {
                          selectedGuideId = matchingGuide.id
                          currentSubTab = GymSubTab.DEMO
                        }
                      },
                      modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                      shape = RoundedCornerShape(12.dp),
                      border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                    ) {
                      Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                      )
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = "Ver en 3D",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                      )
                    }
                  }
                }
              }
            }

            item {
              Button(
                onClick = { showAddWorkoutDialog = true },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp)
              ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Registrar este entrenamiento en Gym")
              }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
          }
        }

        GymSubTab.DEMO -> {
          val guides = gymUiState.visualGuides
          val demoMuscleGroups = remember { ExerciseDatabase.getMuscleGroups() }
          var demoSelectedMuscleGroup by remember { mutableStateOf("Todos") }
          var demoSearchQuery by remember { mutableStateOf("") }
          var demoUse3DModel by remember { mutableStateOf(true) }

          val filteredDemoGuides = remember(demoSelectedMuscleGroup, demoSearchQuery, guides) {
            guides.filter { guide ->
              val matchesGroup = demoSelectedMuscleGroup == "Todos" || guide.targetMuscleGroup.equals(demoSelectedMuscleGroup, ignoreCase = true)
              val matchesQuery = demoSearchQuery.isBlank() ||
                guide.exerciseName.contains(demoSearchQuery, ignoreCase = true) ||
                guide.primaryMuscles.any { it.contains(demoSearchQuery, ignoreCase = true) }
              matchesGroup && matchesQuery
            }
          }

          val selectedGuide = filteredDemoGuides.find { it.id == selectedGuideId }
            ?: guides.find { it.id == selectedGuideId }
            ?: filteredDemoGuides.firstOrNull()
            ?: guides.firstOrNull()

          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            // Buscador y Selector de Grupos Musculares (+20 por grupo)
            item {
              Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                  text = "🎬 Demostración de Ejercicios y Biomecánica 3D",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                  value = demoSearchQuery,
                  onValueChange = { demoSearchQuery = it },
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("demo_exercise_search_field"),
                  placeholder = {
                    Text("Buscar entre ${guides.size} ejercicios...", fontSize = 13.sp)
                  },
                  leadingIcon = {
                    Icon(
                      imageVector = Icons.Default.Search,
                      contentDescription = "Buscar",
                      tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  },
                  trailingIcon = {
                    if (demoSearchQuery.isNotEmpty()) {
                      IconButton(onClick = { demoSearchQuery = "" }) {
                        Icon(
                          imageVector = Icons.Default.Clear,
                          contentDescription = "Limpiar",
                          tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                      }
                    }
                  },
                  singleLine = true,
                  shape = RoundedCornerShape(12.dp)
                )

                // Filtro por Músculo
                LazyRow(
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  items(demoMuscleGroups) { group ->
                    val isSelected = demoSelectedMuscleGroup == group
                    val count = if (group == "Todos") guides.size else guides.count { it.targetMuscleGroup.equals(group, ignoreCase = true) }
                    FilterChip(
                      selected = isSelected,
                      onClick = { demoSelectedMuscleGroup = group },
                      label = {
                        Text(
                          text = "$group ($count)",
                          fontSize = 11.sp,
                          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                      },
                      colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                      )
                    )
                  }
                }

                // Selector de Ejercicios Filtrados
                LazyRow(
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  items(filteredDemoGuides) { guide ->
                    val isSelected = guide.id == selectedGuide?.id
                    FilterChip(
                      selected = isSelected,
                      onClick = { selectedGuideId = guide.id },
                      label = { Text("${guide.category.iconEmoji} ${guide.exerciseName}", fontSize = 12.sp) },
                      colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.secondary,
                        selectedLabelColor = Color.White
                      )
                    )
                  }
                }
              }
            }

            // Conmutador de Modelo 3D vs 2D
            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = if (demoUse3DModel) "VISTA: MODELO 3D HUMANOIDE" else "VISTA: ESQUEMA 2D",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  FilterChip(
                    selected = demoUse3DModel,
                    onClick = { demoUse3DModel = true },
                    label = { Text("3D Humano", fontSize = 11.sp) }
                  )
                  FilterChip(
                    selected = !demoUse3DModel,
                    onClick = { demoUse3DModel = false },
                    label = { Text("2D Trazo", fontSize = 11.sp) }
                  )
                }
              }
            }

            if (selectedGuide != null) {
              item {
                if (demoUse3DModel) {
                  Humanoid3DViewer(
                    guide = selectedGuide,
                    isDark = isDark
                  )
                } else {
                  ExerciseDemonstrationCard(
                    guide = selectedGuide,
                    isDark = isDark,
                    onOpenDetail = { showExerciseDetailFullScreen = true }
                  )
                }
              }

              // Instrucciones Biomecánicas integradas directamente en pantalla para ejecución correcta
              item {
                ExerciseInstructionsCard(
                  guide = selectedGuide,
                  isDark = isDark
                )
              }

              item {
                Button(
                  onClick = { showExerciseDetailFullScreen = true },
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("open_exercise_detail_button"),
                  shape = RoundedCornerShape(14.dp),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                  )
                ) {
                  Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "Ver Gráfica Histórica (D3.js) y Biomecánica Completa",
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }

            // Catálogo interactivo de ejercicios por categoría
            item {
              Spacer(modifier = Modifier.height(12.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "📚 Catálogo de Ejercicios (${filteredDemoGuides.size})",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "Toca para revisar ejecución",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }

            items(filteredDemoGuides) { guide ->
              val isCurrent = guide.id == selectedGuide?.id
              Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                  containerColor = if (isCurrent) {
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = if (isDark) 0.35f else 0.5f)
                  } else {
                    if (isDark) Color(0xFF1E293B) else Color(0xFFFFFFFF)
                  }
                ),
                border = BorderStroke(
                  if (isCurrent) 2.dp else 1.dp,
                  if (isCurrent) MaterialTheme.colorScheme.primary else (if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("exercise_catalog_card_${guide.id}")
                  .clickable {
                    selectedGuideId = guide.id
                  }
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                      Text(guide.category.iconEmoji, fontSize = 20.sp)
                      Spacer(modifier = Modifier.width(10.dp))
                      Column {
                        Text(
                          text = guide.exerciseName,
                          style = MaterialTheme.typography.titleSmall,
                          fontWeight = FontWeight.Bold
                        )
                        Text(
                          text = "${guide.targetMuscleGroup} • ${guide.equipment}",
                          style = MaterialTheme.typography.bodySmall,
                          color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                      }
                    }

                    if (isCurrent) {
                      Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary
                      ) {
                        Text(
                          text = "🟢 Activo 3D",
                          style = MaterialTheme.typography.labelSmall,
                          color = Color.White,
                          fontWeight = FontWeight.Bold,
                          modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                      }
                    }
                  }

                  Spacer(modifier = Modifier.height(8.dp))

                  // Músculos principales y tempo
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "🎯 ${guide.primaryMuscles.firstOrNull() ?: guide.targetMuscleGroup}",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.primary,
                      fontWeight = FontWeight.SemiBold
                    )
                    Text(
                      text = "⏱️ Tempo: ${guide.tempo}",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }

                  Spacer(modifier = Modifier.height(10.dp))

                  // Acciones interactivas
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    Button(
                      onClick = {
                        selectedGuideId = guide.id
                      },
                      modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                      shape = RoundedCornerShape(10.dp),
                      colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCurrent) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                      )
                    ) {
                      Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text(if (isCurrent) "Visualizando" else "Cargar en 3D", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                      onClick = {
                        selectedGuideId = guide.id
                        showExerciseDetailFullScreen = true
                      },
                      modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                      shape = RoundedCornerShape(10.dp),
                      border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                    ) {
                      Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("Técnica & D3", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                    }
                  }
                }
              }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
          }
        }

      }
    }
  }

  // Dialog Pantalla Completa: ExerciseDetailScreen con tema global sincronizado
  if (showExerciseDetailFullScreen) {
    Dialog(
      onDismissRequest = { showExerciseDetailFullScreen = false },
      properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
      ExerciseDetailScreen(
        exerciseId = selectedGuideId,
        gymViewModel = gymViewModel,
        isDarkTheme = isDark,
        onNavigateBack = { showExerciseDetailFullScreen = false }
      )
    }
  }

  // Dialog Calculadora 1RM (Fórmula de Brzycki con soporte para series y reps de ejercicio específico)
  if (showCalculatorDialog) {
    val initialEx = selectedGuideId.replace("_", " ").split(" ").joinToString(" ") { it.replaceFirstChar(Char::uppercase) }
    com.example.ui.components.Brzycki1RmCalculatorDialog(
      initialExerciseName = initialEx.ifBlank { "Press Militar con Barra" },
      isDark = isDark,
      onDismiss = { showCalculatorDialog = false },
      onSaveRecord = { exName, best1Rm, w, r ->
        gymViewModel.record1RmCalculation(exName, best1Rm, w, r)
        showCalculatorDialog = false
      }
    )
  }

  // Dialog Registrar / Editar Entrenamiento
  if (showAddWorkoutDialog || editingSession != null) {
    AddOrEditWorkoutSessionDialog(
      sessionToEdit = editingSession,
      onDismiss = {
        showAddWorkoutDialog = false
        editingSession = null
      },
      onSave = { session ->
        gymViewModel.saveWorkout(session)
        showAddWorkoutDialog = false
        editingSession = null
      }
    )
  }

  // Dialog Confirmación para Reiniciar a Nivel 1 (0 XP)
  if (showResetConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showResetConfirmDialog = false },
      title = { Text("¿Reiniciar progreso desde 0?", fontWeight = FontWeight.Bold) },
      text = {
        Text(
          "Se eliminarán las sesiones registradas y tu cuenta volverá a Nivel 1 con 0 XP y 0 series. Conforme entrenes y registres tus ejercicios reales irás ganando nivel."
        )
      },
      confirmButton = {
        Button(
          onClick = {
            gymViewModel.clearAllWorkoutHistory()
            showResetConfirmDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text("Sí, reiniciar a 0", color = Color.White)
        }
      },
      dismissButton = {
        TextButton(onClick = { showResetConfirmDialog = false }) {
          Text("Cancelar")
        }
      }
    )
  }
}

@Composable
fun WorkoutSessionCard(
  session: WorkoutSession,
  volumeSummary: com.example.viewmodel.WorkoutVolumeSummary,
  isDark: Boolean,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
  onOpenExerciseExecution: ((String) -> Unit)? = null
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFFFFFFF)
    ),
    border = BorderStroke(
      1.dp,
      if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
    ),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = session.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "${session.dateIso} • ${session.durationMinutes} min • 🔥 ${session.estimatedCaloriesBurned} kcal",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onEdit) {
            Icon(
              Icons.Default.Edit,
              contentDescription = "Editar sesión",
              tint = MaterialTheme.colorScheme.primary
            )
          }
          IconButton(onClick = onDelete) {
            Icon(
              Icons.Default.Delete,
              contentDescription = "Eliminar sesión",
              tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Resumen de Volumen de esta sesión (Tonnage)
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isDark) Color(0xFF0F172A) else Color(0xFFF1F5F9),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              Icons.AutoMirrored.Filled.TrendingUp,
              contentDescription = null,
              modifier = Modifier.size(16.dp),
              tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Volumen: ${volumeSummary.totalVolumeKg.roundToInt()} kg",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold
            )
          }

          Text(
            text = "${volumeSummary.totalCompletedSets} series efectivas",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
      HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
      Spacer(modifier = Modifier.height(10.dp))

      // Ejercicios y series
      session.exercises.forEach { ex ->
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = onOpenExerciseExecution != null) {
              onOpenExerciseExecution?.invoke(ex.exerciseName)
            }
            .padding(vertical = 4.dp, horizontal = 2.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
              Text(ex.category.iconEmoji, fontSize = 14.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = ex.exerciseName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
              )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "${ex.sets.size} series • 1RM ~${ex.bestEstimated1Rm}kg",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
              )
              if (onOpenExerciseExecution != null) {
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                  modifier = Modifier.clickable { onOpenExerciseExecution(ex.exerciseName) }
                ) {
                  Text(
                    text = "🎬 Técnica",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
            }
          }

          // Series resumen chips
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            ex.sets.forEach { s ->
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isDark) Color(0xFF0F172A) else Color(0xFFF1F5F9)
              ) {
                Text(
                  text = "S${s.setNumber}: ${s.weightKg}k×${s.reps}",
                  style = MaterialTheme.typography.labelSmall,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
          }
        }
      }

      if (session.notes.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Nota: ${session.notes}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}

@Composable
fun OneRmCalculatorDialog(
  calculatorState: OneRmCalculatorState,
  onUpdateInput: (Float, Int) -> Unit,
  onDismiss: () -> Unit
) {
  var weightText by remember(calculatorState.weightKg) { mutableStateOf(calculatorState.weightKg.toString()) }
  var repsText by remember(calculatorState.reps) { mutableStateOf(calculatorState.reps.toString()) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Calculate, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Calculadora & Estimador 1RM", fontWeight = FontWeight.Bold)
      }
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
          "Calcula tu Repetición Máxima (1RM) con fórmulas biomecánicas compuestas (Epley & Brzycki) y proyección de cargas:",
          style = MaterialTheme.typography.bodySmall
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = weightText,
            onValueChange = {
              weightText = it
              val w = it.toFloatOrNull() ?: 0f
              val r = repsText.toIntOrNull() ?: 1
              onUpdateInput(w, r)
            },
            label = { Text("Peso (kg)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f)
          )

          OutlinedTextField(
            value = repsText,
            onValueChange = {
              repsText = it
              val w = weightText.toFloatOrNull() ?: 0f
              val r = it.toIntOrNull() ?: 1
              onUpdateInput(w, r)
            },
            label = { Text("Reps") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f)
          )
        }

        // 1RM Result Banner con desglose compuesto
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = MaterialTheme.colorScheme.primaryContainer,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "1RM Estimado Compuesto",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
              text = "${calculatorState.estimated1Rm} kg",
              style = MaterialTheme.typography.headlineMedium,
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Epley: ${calculatorState.epley1Rm}kg • Brzycki: ${calculatorState.brzycki1Rm}kg",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
          }
        }

        Text(
          text = "Proyección de cargas por repeticiones:",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold
        )

        calculatorState.projections.forEach { proj ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("${proj.reps} repeticiones (${proj.percentage}%)", style = MaterialTheme.typography.bodySmall)
            Text("${proj.targetWeightKg} kg", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
          }
        }
      }
    },
    confirmButton = {
      Button(onClick = onDismiss) {
        Text("Cerrar")
      }
    }
  )
}

@Composable
fun GymUserLevelCard(
  userLevel: GymUserLevel,
  isDark: Boolean,
  onResetProgress: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFFFFFFF)
    ),
    border = BorderStroke(
      1.dp,
      if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
    ),
    modifier = modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            modifier = Modifier.size(46.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(userLevel.badgeEmoji, fontSize = 24.sp)
            }
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "NIVEL ${userLevel.level}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "• ${userLevel.currentXp} XP",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Text(
              text = userLevel.title,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }

        IconButton(
          onClick = onResetProgress,
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            Icons.Default.Refresh,
            contentDescription = "Reiniciar progreso a cero",
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "Progreso al siguiente nivel",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "${userLevel.xpInCurrentLevel} / ${userLevel.xpRequiredForNextLevel} XP (${(userLevel.progressFraction * 100).roundToInt()}%)",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
          progress = { userLevel.progressFraction },
          modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp)),
          color = MaterialTheme.colorScheme.primary,
          trackColor = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 8.dp),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "${userLevel.totalCompletedSets}",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Series",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Box(
            modifier = Modifier
              .width(1.dp)
              .height(24.dp)
              .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
          )

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "${userLevel.totalCompletedExercises}",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Ejercicios",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Box(
            modifier = Modifier
              .width(1.dp)
              .height(24.dp)
              .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
          )

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "${userLevel.totalVolumeKg.roundToInt()} kg",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
            Text(
              text = "Volumen Total",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Siguiente rango: ${userLevel.nextUnlockTitle} (+25 XP por serie, +50 XP por ejercicio)",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
        fontSize = 10.sp
      )
    }
  }
}

@Composable
fun AddOrEditWorkoutSessionDialog(
  sessionToEdit: WorkoutSession? = null,
  onDismiss: () -> Unit,
  onSave: (WorkoutSession) -> Unit
) {
  val isEditing = sessionToEdit != null
  var title by remember { mutableStateOf(sessionToEdit?.title ?: "Entrenamiento de Fuerza") }
  var durationText by remember { mutableStateOf((sessionToEdit?.durationMinutes ?: 60).toString()) }
  var notes by remember { mutableStateOf(sessionToEdit?.notes ?: "") }

  val firstEx = sessionToEdit?.exercises?.firstOrNull()
  var selectedCategory by remember { mutableStateOf(firstEx?.category ?: AestheticMuscleCategory.V_TAPER_DELTS) }
  var exerciseName by remember { mutableStateOf(firstEx?.exerciseName ?: "Press Militar con Barra") }
  var weightText by remember { mutableStateOf("50") }
  var repsText by remember { mutableStateOf("10") }

  var temporarySets by remember {
    mutableStateOf(
      firstEx?.sets?.ifEmpty { null } ?: listOf(
        ExerciseSet(setNumber = 1, weightKg = 40f, reps = 12, completed = true),
        ExerciseSet(setNumber = 2, weightKg = 45f, reps = 10, completed = true),
        ExerciseSet(setNumber = 3, weightKg = 50f, reps = 8, completed = true)
      )
    )
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        if (isEditing) "Editar Sesión de Gym" else "Registrar Sesión de Gym",
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(max = 420.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Nombre de la sesión") },
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = durationText,
          onValueChange = { durationText = it },
          label = { Text("Duración (minutos)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          modifier = Modifier.fillMaxWidth()
        )

        Text("Categoría estética:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(AestheticMuscleCategory.values()) { cat ->
            val isSel = cat == selectedCategory
            FilterChip(
              selected = isSel,
              onClick = { selectedCategory = cat },
              label = { Text("${cat.iconEmoji} ${cat.displayName}", fontSize = 11.sp) }
            )
          }
        }

        OutlinedTextField(
          value = exerciseName,
          onValueChange = { exerciseName = it },
          label = { Text("Ejercicio principal") },
          modifier = Modifier.fillMaxWidth()
        )

        // Registrar series
        Text("Series a registrar:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = weightText,
            onValueChange = { weightText = it },
            label = { Text("Kg") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = repsText,
            onValueChange = { repsText = it },
            label = { Text("Reps") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f)
          )
          IconButton(
            onClick = {
              val w = weightText.toFloatOrNull() ?: 20f
              val r = repsText.toIntOrNull() ?: 10
              val newSet = ExerciseSet(
                setNumber = temporarySets.size + 1,
                weightKg = w,
                reps = r,
                completed = true
              )
              temporarySets = temporarySets + newSet
            },
            modifier = Modifier
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.primary)
          ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar serie", tint = Color.White)
          }
        }

        // Lista de series temporales con cálculo de volumen preview
        val previewVolume = temporarySets.sumOf { (it.weightKg * it.reps).toDouble() }.toFloat()
        Text(
          text = "Volumen estimado sesión actual: ${previewVolume.roundToInt()} kg",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.SemiBold
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(temporarySets) { s ->
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.surfaceVariant
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 8.dp, end = 6.dp, top = 4.dp, bottom = 4.dp)
              ) {
                Text(
                  "S${s.setNumber}: ${s.weightKg}kg×${s.reps}",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                  Icons.Default.Clear,
                  contentDescription = "Quitar serie",
                  modifier = Modifier
                    .size(14.dp)
                    .clickable {
                      temporarySets = temporarySets.filter { it.setNumber != s.setNumber }
                        .mapIndexed { idx, set -> set.copy(setNumber = idx + 1) }
                    },
                  tint = MaterialTheme.colorScheme.error
                )
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val duration = durationText.toIntOrNull() ?: 60
          val today = sessionToEdit?.dateIso ?: NutritionRepository.getTodayIso()
          val exercise = WorkoutExercise(
            id = sessionToEdit?.exercises?.firstOrNull()?.id ?: 0,
            exerciseName = exerciseName.ifBlank { "Ejercicio de Fuerza" },
            category = selectedCategory,
            sets = temporarySets
          )
          val session = WorkoutSession(
            id = sessionToEdit?.id ?: 0,
            dateIso = today,
            title = title.ifBlank { "Entrenamiento de Gym" },
            durationMinutes = duration,
            exercises = listOf(exercise),
            notes = notes,
            startTimeMillis = sessionToEdit?.startTimeMillis ?: System.currentTimeMillis()
          )
          onSave(session)
        }
      ) {
        Text(if (isEditing) "Guardar Cambios" else "Guardar Entrenamiento")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancelar")
      }
    }
  )
}

@Composable
fun AestheticMuscleRankCard(
  categoryRank: MuscleCategoryRank,
  isLagging: Boolean,
  isDominant: Boolean,
  isDark: Boolean
) {
  val rank = categoryRank.currentRank
  val rankColor = Color(rank.colorHex)

  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFFFFFFF)
    ),
    border = BorderStroke(
      1.dp,
      if (isLagging) MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
      else if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
    ),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(categoryRank.category.iconEmoji, fontSize = 20.sp)
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = categoryRank.category.displayName,
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Ej. referencia: ${categoryRank.primaryExercise}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = rankColor.copy(alpha = 0.2f),
            border = BorderStroke(1.dp, rankColor.copy(alpha = 0.5f))
          ) {
            Text(
              text = "${rank.symbol} ${rank.displayName}",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = rankColor,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }

          if (isLagging) {
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = MaterialTheme.colorScheme.errorContainer
            ) {
              Text(
                text = "Rezagado",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
              )
            }
          } else if (isDominant) {
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = Color(0xFF10B981).copy(alpha = 0.2f)
            ) {
              Text(
                text = "Dominante",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF047857),
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Ratio: ${categoryRank.relativeRatio}x peso (1RM: ${categoryRank.best1Rm} kg)",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
          text = if (categoryRank.nextRank != null) {
            "${(categoryRank.progressToNext * 100).roundToInt()}% hacia ${categoryRank.nextRank.displayName}"
          } else {
            "Rango Máximo"
          },
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.SemiBold,
          color = rankColor
        )
      }

      Spacer(modifier = Modifier.height(4.dp))
      LinearProgressIndicator(
        progress = { categoryRank.progressToNext },
        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
        color = rankColor,
        trackColor = if (isDark) Color(0xFF0F172A) else Color(0xFFE2E8F0)
      )
    }
  }
}

@Composable
fun ExerciseDemonstrationCard(
  guide: ExerciseVisualGuide,
  isDark: Boolean,
  onOpenDetail: () -> Unit = {}
) {
  val infiniteTransition = rememberInfiniteTransition(label = "exercise_phase")
  val phaseProgress by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 5000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "exercise_loop"
  )

  // Desglose de fases biomecánicas
  val (phaseName, phaseColor, phaseInstruction) = when {
    phaseProgress < 0.60f -> Triple(
      "⬇️ FASE EXCÉNTRICA (3s)",
      Color(0xFF38BDF8),
      "Bajada controlada frenando la carga con máxima tensión activa"
    )
    phaseProgress < 0.80f -> Triple(
      "⏸️ PAUSA ISOMÉTRICA (1s)",
      Color(0xFFFBBF24),
      "Máximo estiramiento activo y reclutamiento miofibrilar sin rebote"
    )
    else -> Triple(
      "⬆️ FASE CONCÉNTRICA (1s)",
      Color(0xFF10B981),
      "Impulso potente y explosivo con máxima aceleración intencional"
    )
  }

  Card(
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFFFFFFF)
    ),
    border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = guide.exerciseName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Tempo: ${guide.tempo} • Categoría: ${guide.category.displayName}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.primaryContainer
        ) {
          Text(
            text = guide.category.iconEmoji,
            fontSize = 22.sp,
            modifier = Modifier.padding(6.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Canvas animado de trayectoria en bucle
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1)),
        modifier = Modifier.fillMaxWidth().height(150.dp)
      ) {
        Box(modifier = Modifier.fillMaxSize()) {
          Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val width = size.width
            val height = size.height

            // Guías de trayectoria vertical
            drawLine(
              color = Color.Gray.copy(alpha = 0.25f),
              start = Offset(width / 2, 20f),
              end = Offset(width / 2, height - 20f),
              strokeWidth = 3f
            )

            // Posición vertical normalizada según la fase
            val normalizedY = when {
              phaseProgress < 0.60f -> {
                // De arriba hacia abajo (0 -> 1)
                20f + (height - 40f) * (phaseProgress / 0.60f)
              }
              phaseProgress < 0.80f -> {
                // Pausa en el fondo
                height - 20f
              }
              else -> {
                // De abajo hacia arriba explosivo (1 -> 0)
                (height - 20f) - (height - 40f) * ((phaseProgress - 0.80f) / 0.20f)
              }
            }

            // Barra / Carga animada
            val barbellWidth = width * 0.45f
            val barbellLeft = (width - barbellWidth) / 2
            drawLine(
              color = phaseColor,
              start = Offset(barbellLeft, normalizedY),
              end = Offset(barbellLeft + barbellWidth, normalizedY),
              strokeWidth = 7f
            )
            // Discos en extremos
            drawCircle(
              color = phaseColor,
              radius = 14f,
              center = Offset(barbellLeft, normalizedY)
            )
            drawCircle(
              color = phaseColor,
              radius = 14f,
              center = Offset(barbellLeft + barbellWidth, normalizedY)
            )
            // Marcador central
            drawCircle(
              color = Color.White,
              radius = 5f,
              center = Offset(width / 2, normalizedY)
            )
          }

          // Badge de fase actual
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = phaseColor.copy(alpha = 0.2f),
            border = BorderStroke(1.dp, phaseColor),
            modifier = Modifier.align(Alignment.TopCenter).padding(8.dp)
          ) {
            Text(
              text = phaseName,
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = phaseColor,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = phaseInstruction,
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.Medium,
        color = phaseColor
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Músculos Involucrados
      Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text("Primarios: ", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)) {
          Text(
            text = guide.primaryMuscles.joinToString(", "),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text("Secundarios: ", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        Text(
          text = guide.secondaryMuscles.joinToString(", "),
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Respiración
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
          Text("🫁", fontSize = 16.sp)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = guide.breathingCue,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Claves Biomecánicas
      Text(
        text = "Puntos Críticos de Ejecución:",
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(6.dp))
      guide.keyPoints.forEach { cue ->
        Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
          Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF10B981),
            modifier = Modifier.size(16.dp).padding(top = 2.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = cue, style = MaterialTheme.typography.bodySmall)
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      OutlinedButton(
        onClick = onOpenDetail,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.TrendingUp,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Abrir Análisis Completo & Historial D3.js",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary
        )
      }
    }
  }
}

