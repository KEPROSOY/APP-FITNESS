package com.example.ui.screens

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import com.example.ui.components.Humanoid3DViewer
import com.example.data.ExerciseDatabase
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.ExerciseHistoryPoint
import com.example.data.model.ExerciseVisualGuide
import com.example.fitness.FitnessEngine
import com.example.viewmodel.GymViewModel
import kotlin.math.roundToInt

enum class ChartMetric(val label: String, val unit: String) {
  ONE_RM("1RM Estimado", "kg"),
  WEIGHT("Carga Máxima", "kg"),
  VOLUME("Volumen Total", "kg")
}

enum class ChartRenderType(val label: String) {
  D3_RECHARTS("D3.js / SVG Interactivo"),
  NATIVE_COMPOSE("Nativo Compose")
}

/**
 * ExerciseDetailScreen
 *
 * Pantalla completa que respeta el tema global (Material 3 Dark/Light):
 * 1. Instrucciones biomecánicas paso a paso y desglose muscular.
 * 2. Visual guide placeholder en bucle con control interactivo de fases biomecánicas.
 * 3. Gráfica de evolución histórica de cargas y 1RM mediante integración con D3.js (y alternativa Compose).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
  exerciseId: String = "press_militar",
  gymViewModel: GymViewModel,
  isDarkTheme: Boolean? = null,
  onNavigateBack: () -> Unit = {}
) {
  // Sincronización estricta con el tema global de la app
  val isDark = isDarkTheme ?: (MaterialTheme.colorScheme.surface.luminance() < 0.5f)
  val allGuides = remember { FitnessEngine.getExerciseVisualGuides() }
  val muscleGroups = remember { ExerciseDatabase.getMuscleGroups() }
  var selectedMuscleGroup by remember { mutableStateOf("Todos") }
  var searchQuery by remember { mutableStateOf("") }
  var use3DModel by remember { mutableStateOf(true) }

  val filteredGuides = remember(selectedMuscleGroup, searchQuery, allGuides) {
    allGuides.filter { guide ->
      val matchesGroup = selectedMuscleGroup == "Todos" || guide.targetMuscleGroup.equals(selectedMuscleGroup, ignoreCase = true)
      val matchesQuery = searchQuery.isBlank() ||
        guide.exerciseName.contains(searchQuery, ignoreCase = true) ||
        guide.primaryMuscles.any { it.contains(searchQuery, ignoreCase = true) }
      matchesGroup && matchesQuery
    }
  }

  var currentGuideId by remember(exerciseId) { mutableStateOf(exerciseId) }
  val currentGuide = filteredGuides.find { it.id == currentGuideId }
    ?: allGuides.find { it.id == currentGuideId }
    ?: filteredGuides.firstOrNull()
    ?: allGuides.first()

  // Historial del ejercicio observado
  val historyPoints = gymViewModel.getHistoryForExercise(currentGuide.exerciseName)

  var selectedMetric by remember { mutableStateOf(ChartMetric.ONE_RM) }
  var chartRenderType by remember { mutableStateOf(ChartRenderType.D3_RECHARTS) }

  Scaffold(
    containerColor = MaterialTheme.colorScheme.background,
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = currentGuide.exerciseName,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "${currentGuide.targetMuscleGroup} • ${currentGuide.equipment}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("exercise_detail_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Volver",
              tint = MaterialTheme.colorScheme.onSurface
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface,
          titleContentColor = MaterialTheme.colorScheme.onSurface,
          navigationIconContentColor = MaterialTheme.colorScheme.onSurface
        )
      )
    }
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .testTag("exercise_detail_screen"),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Buscador y Filtro de Grupos Musculares (+20 ejercicios por músculo)
      item {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          // Buscador rápido
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("exercise_search_field"),
            placeholder = {
              Text(
                "Buscar entre ${allGuides.size} ejercicios...",
                fontSize = 13.sp
              )
            },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
              )
            },
            trailingIcon = {
              if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { searchQuery = "" }) {
                  Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Limpiar",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = MaterialTheme.colorScheme.surface,
              unfocusedContainerColor = MaterialTheme.colorScheme.surface,
              focusedBorderColor = MaterialTheme.colorScheme.primary,
              unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
          )

          // Selector de Grupos Musculares
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(muscleGroups) { group ->
              val isSelected = selectedMuscleGroup == group
              val count = if (group == "Todos") allGuides.size else allGuides.count { it.targetMuscleGroup.equals(group, ignoreCase = true) }
              FilterChip(
                selected = isSelected,
                onClick = { selectedMuscleGroup = group },
                label = {
                  Text(
                    text = "$group ($count)",
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                  )
                },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = MaterialTheme.colorScheme.primary,
                  selectedLabelColor = Color.White,
                  containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                  labelColor = MaterialTheme.colorScheme.onSurface
                ),
                border = FilterChipDefaults.filterChipBorder(
                  borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                  selectedBorderColor = MaterialTheme.colorScheme.primary,
                  enabled = true,
                  selected = isSelected
                )
              )
            }
          }

          // Selector horizontal de ejercicios filtrados
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(filteredGuides) { guide ->
              val isSelected = guide.id == currentGuide.id
              FilterChip(
                selected = isSelected,
                onClick = { currentGuideId = guide.id },
                label = {
                  Text(
                    text = "${guide.category.iconEmoji} ${guide.exerciseName}",
                    fontSize = 12.sp,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                  )
                },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = MaterialTheme.colorScheme.secondary,
                  selectedLabelColor = Color.White,
                  containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                  labelColor = MaterialTheme.colorScheme.onSurface
                ),
                border = FilterChipDefaults.filterChipBorder(
                  borderColor = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                  selectedBorderColor = MaterialTheme.colorScheme.secondary,
                  enabled = true,
                  selected = isSelected
                ),
                modifier = Modifier.testTag("filter_chip_${guide.id}")
              )
            }
          }
        }
      }

      // 2. Conmutador de Modo de Visualización (3D Humano vs Diagrama 2D)
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (use3DModel) "VISOR 3D BIOMECÁNICO INTERACTIVO" else "DIAGRAMA CINEMÁTICO 2D",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 0.8.sp
          )

          SingleChoiceSegmentedButtonRow {
            SegmentedButton(
              selected = use3DModel,
              onClick = { use3DModel = true },
              shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
            ) {
              Text("3D Humano", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            SegmentedButton(
              selected = !use3DModel,
              onClick = { use3DModel = false },
              shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
            ) {
              Text("2D Trazo", fontSize = 11.sp)
            }
          }
        }
      }

      // 3. Renderizado del Visual Guide (3D Humano o Diagrama 2D)
      item {
        if (use3DModel) {
          Humanoid3DViewer(
            guide = currentGuide,
            isDark = isDark
          )
        } else {
          LoopableVisualGuidePlaceholder(
            guide = currentGuide,
            isDark = isDark
          )
        }
      }

      // 3. Gráfica de Progreso Histórico (Integración D3.js / Recharts)
      item {
        ExerciseHistoryProgressSection(
          exerciseName = currentGuide.exerciseName,
          history = historyPoints,
          selectedMetric = selectedMetric,
          onSelectMetric = { selectedMetric = it },
          chartRenderType = chartRenderType,
          onSelectRenderType = { chartRenderType = it },
          isDark = isDark
        )
      }

      // 4. Instrucciones Biomecánicas y Desglose Muscular
      item {
        ExerciseInstructionsCard(
          guide = currentGuide,
          isDark = isDark
        )
      }

      // 5. Calculadora 1RM (Fórmula de Brzycki) para este ejercicio específico
      item {
        com.example.ui.components.BrzyckiCalculatorCard(
          initialExerciseName = currentGuide.exerciseName,
          isDark = isDark,
          onSaveRecord = { exName, best1Rm, w, r ->
            gymViewModel.record1RmCalculation(exName, best1Rm, w, r)
          }
        )
      }

      // 6. Historial Detallado de Sesiones Previas
      item {
        ExerciseSessionHistoryList(
          history = historyPoints,
          isDark = isDark
        )
      }

      item { Spacer(modifier = Modifier.height(60.dp)) }
    }
  }
}

/**
 * Componente interactivo que muestra la trayectoria biomecánica en bucle infinito
 * adaptando completamente sus contrastes al tema activo.
 */
@Composable
fun LoopableVisualGuidePlaceholder(
  guide: ExerciseVisualGuide,
  isDark: Boolean
) {
  var isPlaying by remember { mutableStateOf(true) }
  var loopSpeedFactor by remember { mutableFloatStateOf(1.0f) }

  val transition = rememberInfiniteTransition(label = "biomechanical_loop")
  val progress by transition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(
        durationMillis = (4500 / loopSpeedFactor).roundToInt(),
        easing = LinearEasing
      ),
      repeatMode = RepeatMode.Restart
    ),
    label = "loop_progress"
  )

  val activeProgress = if (isPlaying) progress else 0.5f

  // Definición de las 3 fases biomecánicas según el tempo 3-1-1
  val (phaseTitle, phaseBadgeColor, phaseDescription, phaseProgressRatio) = when {
    activeProgress < 0.60f -> {
      val ratio = activeProgress / 0.60f
      Quadruple(
        "Fase Excéntrica (3s)",
        if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7),
        "Frenado gravitatorio controlado. Elongación miofibrilar activa sin rebote.",
        ratio
      )
    }
    activeProgress < 0.80f -> {
      val ratio = (activeProgress - 0.60f) / 0.20f
      Quadruple(
        "Pausa Isométrica (1s)",
        if (isDark) Color(0xFFFBBF24) else Color(0xFFD97706),
        "Punto de máxima tensión mecánica y disipación de reflejo miotático.",
        ratio
      )
    }
    else -> {
      val ratio = (activeProgress - 0.80f) / 0.20f
      Quadruple(
        "Fase Concéntrica (1s)",
        if (isDark) Color(0xFF34D399) else Color(0xFF059669),
        "Aceleración explosiva. Máximo reclutamiento de unidades motoras rápidas.",
        ratio
      )
    }
  }

  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = if (isDark) 0.35f else 0.5f)),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("loopable_visual_guide_card")
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Cabecera del reproductor
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(10.dp)
              .clip(CircleShape)
              .background(if (isPlaying) Color(0xFF10B981) else Color(0xFFEF4444))
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "GUÍA VISUAL EN BUCLE",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
          )
        }

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = phaseBadgeColor.copy(alpha = 0.15f),
          border = BorderStroke(1.dp, phaseBadgeColor.copy(alpha = 0.3f))
        ) {
          Text(
            text = phaseTitle,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = phaseBadgeColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Canvas animado del bucle biomecánico con fondo adaptativo
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(180.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (isDark) 0.6f else 0.8f))
          .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
      ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(20.dp)) {
          val canvasWidth = size.width
          val canvasHeight = size.height
          val centerX = canvasWidth / 2
          val topY = 25f
          val bottomY = canvasHeight - 25f
          val travelDistance = bottomY - topY

          // 1. Líneas de referencia y guías biomecánicas
          drawLine(
            color = if (isDark) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.12f),
            start = Offset(centerX, topY),
            end = Offset(centerX, bottomY),
            strokeWidth = 3f,
            cap = StrokeCap.Round
          )

          // Marcas de posición superior, media e inferior
          val markColor = if (isDark) Color.White.copy(alpha = 0.25f) else Color.Black.copy(alpha = 0.2f)
          drawCircle(color = markColor, radius = 5f, center = Offset(centerX, topY))
          drawCircle(color = markColor, radius = 5f, center = Offset(centerX, topY + travelDistance / 2))
          drawCircle(color = markColor, radius = 5f, center = Offset(centerX, bottomY))

          // 2. Calcular posición actual de la carga según la fase
          val currentY = when {
            activeProgress < 0.60f -> topY + travelDistance * (activeProgress / 0.60f)
            activeProgress < 0.80f -> bottomY
            else -> bottomY - travelDistance * ((activeProgress - 0.80f) / 0.20f)
          }

          // 3. Estela de movimiento con gradiente
          val trailColor = phaseBadgeColor

          drawLine(
            brush = Brush.verticalGradient(
              listOf(trailColor.copy(alpha = 0.1f), trailColor)
            ),
            start = Offset(centerX, topY),
            end = Offset(centerX, currentY),
            strokeWidth = 6f,
            cap = StrokeCap.Round
          )

          // 4. Barra o mancuerna representada biomecánicamente
          val barWidth = 140f
          drawLine(
            color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF334155),
            start = Offset(centerX - barWidth / 2, currentY),
            end = Offset(centerX + barWidth / 2, currentY),
            strokeWidth = 8f,
            cap = StrokeCap.Round
          )

          // Discos a los extremos
          drawRoundRect(
            color = trailColor,
            topLeft = Offset(centerX - barWidth / 2 - 14f, currentY - 22f),
            size = Size(14f, 44f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
          )
          drawRoundRect(
            color = trailColor,
            topLeft = Offset(centerX + barWidth / 2, currentY - 22f),
            size = Size(14f, 44f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
          )

          // Núcleo central con pulso
          drawCircle(
            color = trailColor,
            radius = 12f,
            center = Offset(centerX, currentY)
          )
          drawCircle(
            color = Color.White,
            radius = 5f,
            center = Offset(centerX, currentY)
          )
        }

        // Overlay con cadencia de respiración y tempo
        Column(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(12.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.90f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
          ) {
            Text(
              text = "Tempo: ${guide.tempo}",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Barra de progreso de la fase actual
      LinearProgressIndicator(
        progress = { phaseProgressRatio.coerceIn(0f, 1f) },
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = phaseBadgeColor,
        trackColor = phaseBadgeColor.copy(alpha = 0.2f)
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = phaseDescription,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Controles interactivos del bucle (Pausa/Play y Selector de Velocidad)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedButton(
            onClick = { isPlaying = !isPlaying },
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("play_pause_button")
          ) {
            Icon(
              imageVector = if (isPlaying) Icons.Default.Refresh else Icons.Default.PlayArrow,
              contentDescription = if (isPlaying) "Pausar" else "Reanudar",
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(if (isPlaying) "Pausar" else "Reanudar")
          }
        }

        // Selector de velocidad
        Row(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          listOf(0.5f, 1.0f, 1.5f).forEach { speed ->
            val isSelected = loopSpeedFactor == speed
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
              border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
              modifier = Modifier
                .clickable { loopSpeedFactor = speed }
                .padding(2.dp)
            ) {
              Text(
                text = "${speed}x",
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }
      }
    }
  }
}

/**
 * Sección de Gráfica Histórica con integración de D3.js (mediante WebView HTML5/SVG interactivo)
 * y alternativa nativa en Jetpack Compose, con colores vinculados al tema global.
 */
@Composable
fun ExerciseHistoryProgressSection(
  exerciseName: String,
  history: List<ExerciseHistoryPoint>,
  selectedMetric: ChartMetric,
  onSelectMetric: (ChartMetric) -> Unit,
  chartRenderType: ChartRenderType,
  onSelectRenderType: (ChartRenderType) -> Unit,
  isDark: Boolean
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = if (isDark) 0.35f else 0.5f)),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("exercise_chart_section")
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "📈 Progreso Histórico de Cargas",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Evolución de $exerciseName a lo largo del tiempo",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Selector de Métrica (1RM, Carga Máxima, Volumen)
      SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        ChartMetric.values().forEachIndexed { index, metric ->
          SegmentedButton(
            selected = selectedMetric == metric,
            onClick = { onSelectMetric(metric) },
            shape = SegmentedButtonDefaults.itemShape(index = index, count = ChartMetric.values().size)
          ) {
            Text(metric.label, style = MaterialTheme.typography.labelSmall)
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Toggle entre D3.js y Nativo Compose
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        ) {
          Row(modifier = Modifier.padding(4.dp)) {
            ChartRenderType.values().forEach { type ->
              val isSelected = chartRenderType == type
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                modifier = Modifier
                  .clickable { onSelectRenderType(type) }
                  .padding(2.dp)
              ) {
                Text(
                  text = type.label,
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Renderizado de Gráfico
      if (chartRenderType == ChartRenderType.D3_RECHARTS) {
        D3InteractiveProgressChart(
          history = history,
          metric = selectedMetric,
          isDark = isDark
        )
      } else {
        NativeComposeProgressChart(
          history = history,
          metric = selectedMetric,
          isDark = isDark
        )
      }
    }
  }
}

/**
 * Gráfico interactivo alimentado con D3.js renderizado dentro de un WebView local
 * perfectamente acoplado a la paleta de colores del tema.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun D3InteractiveProgressChart(
  history: List<ExerciseHistoryPoint>,
  metric: ChartMetric,
  isDark: Boolean
) {
  val htmlContent = remember(history, metric, isDark) {
    generateD3ChartHtml(history, metric, isDark)
  }

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(230.dp)
      .clip(RoundedCornerShape(14.dp))
      .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (isDark) 0.6f else 0.8f))
      .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
  ) {
    AndroidView(
      modifier = Modifier.fillMaxSize(),
      factory = { context ->
        WebView(context).apply {
          layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
          )
          settings.javaScriptEnabled = true
          settings.domStorageEnabled = true
          settings.cacheMode = WebSettings.LOAD_NO_CACHE
          // Security hardening: Disable local file and content schema access
          settings.allowFileAccess = false
          settings.allowContentAccess = false
          setBackgroundColor(0x00000000) // Fondo transparente
          loadDataWithBaseURL("about:blank", htmlContent, "text/html", "UTF-8", null)
        }
      },
      update = { webView ->
        webView.loadDataWithBaseURL("about:blank", htmlContent, "text/html", "UTF-8", null)
      }
    )
  }
}

/**
 * Generador del HTML con visualización SVG inspirada en D3 / Recharts
 * vinculado a los colores exactos del tema Material 3 de SYVRA.
 */
private fun generateD3ChartHtml(
  history: List<ExerciseHistoryPoint>,
  metric: ChartMetric,
  isDark: Boolean
): String {
  // Colores sincronizados con DarkBackground/DarkSurface vs LightBackground/LightSurface
  val bgColor = if (isDark) "#060E18" else "#F3F7FA"
  val textColor = if (isDark) "#8BA2BA" else "#4E6376"
  val gridColor = if (isDark) "rgba(255,255,255,0.08)" else "rgba(0,0,0,0.08)"
  val strokeColor = if (isDark) "#38BDF8" else "#0284C7"
  val gradStart = if (isDark) "rgba(56, 189, 248, 0.45)" else "rgba(2, 132, 199, 0.35)"
  val gradEnd = "rgba(99, 102, 241, 0.0)"
  val cardBg = if (isDark) "#0B1726" else "#FFFFFF"

  val dataJson = history.joinToString(separator = ",") { point ->
    val value = when (metric) {
      ChartMetric.ONE_RM -> point.estimated1Rm
      ChartMetric.WEIGHT -> point.weightKg
      ChartMetric.VOLUME -> point.volumeKg
    }
    """{"date":"${point.dateIso}","val":$value,"reps":${point.reps},"weight":${point.weightKg},"isPr":${point.isPr}}"""
  }

  return """
    <!DOCTYPE html>
    <html>
    <head>
      <meta charset="utf-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
      <style>
        * { margin:0; padding:0; box-sizing:border-box; font-family:-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; }
        body { background:${bgColor}; color:${textColor}; overflow:hidden; user-select:none; }
        #chart { width:100%; height:100%; position:relative; }
        svg { width:100%; height:100%; display:block; }
        .grid line { stroke:${gridColor}; stroke-width:1; stroke-dasharray:3 3; }
        .axis-text { font-size:10px; fill:${textColor}; }
        .area-path { fill:url(#chartGrad); }
        .line-path { fill:none; stroke:${strokeColor}; stroke-width:3; stroke-linecap:round; }
        .dot { fill:${strokeColor}; stroke:${bgColor}; stroke-width:2.5; cursor:pointer; }
        .dot-pr { fill:#F59E0B; stroke:#FFF; stroke-width:2; }
        .tooltip {
          position:absolute; display:none; background:${cardBg}; color:${if (isDark) "#F0F6FC" else "#0A1926"};
          padding:6px 10px; border-radius:8px; font-size:11px; font-weight:600;
          box-shadow:0 8px 16px rgba(0,0,0,0.35); pointer-events:none; border:1px solid ${strokeColor};
          transform:translate(-50%, -120%); z-index:10; white-space:nowrap;
        }
        .tooltip .title { font-size:9px; color:${textColor}; margin-bottom:2px; }
      </style>
    </head>
    <body>
      <div id="chart">
        <div id="tooltip" class="tooltip"></div>
        <svg id="svgChart">
          <defs>
            <linearGradient id="chartGrad" x1="0%" y1="0%" x2="0%" y2="100%">
              <stop offset="0%" stop-color="${gradStart}"/>
              <stop offset="100%" stop-color="${gradEnd}"/>
            </linearGradient>
          </defs>
          <g id="grid"></g>
          <path id="area" class="area-path" />
          <path id="line" class="line-path" />
          <g id="dots"></g>
          <g id="labels"></g>
        </svg>
      </div>
      <script>
        var rawData = [""" + dataJson + """];
        var svg = document.getElementById('svgChart');
        var tooltip = document.getElementById('tooltip');
        var metricUnit = '""" + metric.unit + """';

        function render() {
          var w = svg.clientWidth || window.innerWidth;
          var h = svg.clientHeight || window.innerHeight;
          var padding = { top: 20, right: 24, bottom: 28, left: 38 };
          var plotW = w - padding.left - padding.right;
          var plotH = h - padding.top - padding.bottom;

          if (rawData.length === 0) return;

          var values = rawData.map(function(d) { return d.val; });
          var minVal = Math.min.apply(null, values) * 0.90;
          var maxVal = Math.max.apply(null, values) * 1.05;

          function getX(i) {
            return padding.left + (i / Math.max(1, rawData.length - 1)) * plotW;
          }
          function getY(val) {
            return padding.top + plotH - ((val - minVal) / (maxVal - minVal || 1)) * plotH;
          }

          var gridHtml = '';
          var yTicks = 4;
          for (var i = 0; i <= yTicks; i++) {
            var v = minVal + (i / yTicks) * (maxVal - minVal);
            var y = getY(v);
            gridHtml += '<line x1="' + padding.left + '" y1="' + y + '" x2="' + (w - padding.right) + '" y2="' + y + '" />';
            gridHtml += '<text class="axis-text" x="' + (padding.left - 6) + '" y="' + (y + 3) + '" text-anchor="end">' + Math.round(v) + '</text>';
          }
          document.getElementById('grid').innerHTML = gridHtml;

          var lineD = 'M ' + getX(0) + ' ' + getY(rawData[0].val);
          for (var i = 0; i < rawData.length - 1; i++) {
            var x0 = getX(i);
            var y0 = getY(rawData[i].val);
            var x1 = getX(i + 1);
            var y1 = getY(rawData[i + 1].val);
            var mx = (x0 + x1) / 2;
            lineD += ' C ' + mx + ' ' + y0 + ', ' + mx + ' ' + y1 + ', ' + x1 + ' ' + y1;
          }

          document.getElementById('line').setAttribute('d', lineD);
          var areaD = lineD + ' L ' + getX(rawData.length - 1) + ' ' + (padding.top + plotH) + ' L ' + getX(0) + ' ' + (padding.top + plotH) + ' Z';
          document.getElementById('area').setAttribute('d', areaD);

          var dotsHtml = '';
          var labelsHtml = '';
          rawData.forEach(function(d, i) {
            var cx = getX(i);
            var cy = getY(d.val);
            var isPrClass = d.isPr ? 'dot-pr' : '';
            dotsHtml += '<circle class="dot ' + isPrClass + '" cx="' + cx + '" cy="' + cy + '" r="' + (d.isPr ? 6 : 4.5) + '" data-idx="' + i + '"/>';

            var shortDate = d.date.split('-').slice(1).join('/');
            labelsHtml += '<text class="axis-text" x="' + cx + '" y="' + (h - 8) + '" text-anchor="middle">' + shortDate + '</text>';
          });
          document.getElementById('dots').innerHTML = dotsHtml;
          document.getElementById('labels').innerHTML = labelsHtml;

          document.querySelectorAll('.dot').forEach(function(dot) {
            dot.addEventListener('click', function(e) { showTip(e); });
            dot.addEventListener('touchstart', function(e) { showTip(e); });
          });
        }

        function showTip(e) {
          var idx = e.target.getAttribute('data-idx');
          var d = rawData[idx];
          var cx = e.target.getAttribute('cx');
          var cy = e.target.getAttribute('cy');

          tooltip.style.display = 'block';
          tooltip.style.left = cx + 'px';
          tooltip.style.top = cy + 'px';
          tooltip.innerHTML = '<div class="title">' + d.date + (d.isPr ? ' 🏆 PR' : '') + '</div>' +
            '<div>' + (Math.round(d.val * 10) / 10) + ' ' + metricUnit + ' (' + d.weight + 'kg x ' + d.reps + ')</div>';
        }

        window.addEventListener('resize', render);
        window.addEventListener('load', render);
        render();
      </script>
    </body>
    </html>
  """.trimIndent()
}

/**
 * Gráfico nativo alternativo en Jetpack Compose Canvas con scrubber táctil.
 */
@Composable
fun NativeComposeProgressChart(
  history: List<ExerciseHistoryPoint>,
  metric: ChartMetric,
  isDark: Boolean
) {
  var activeIndex by remember { mutableIntStateOf(-1) }

  val values = history.map {
    when (metric) {
      ChartMetric.ONE_RM -> it.estimated1Rm
      ChartMetric.WEIGHT -> it.weightKg
      ChartMetric.VOLUME -> it.volumeKg
    }
  }

  val minVal = (values.minOrNull() ?: 0f) * 0.90f
  val maxVal = (values.maxOrNull() ?: 100f) * 1.05f

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(230.dp)
      .clip(RoundedCornerShape(14.dp))
      .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (isDark) 0.6f else 0.8f))
      .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
      .pointerInput(history) {
        detectDragGestures(
          onDragEnd = { activeIndex = -1 },
          onDragCancel = { activeIndex = -1 },
          onDrag = { change, _ ->
            val touchX = change.position.x
            val segmentWidth = size.width / history.size.coerceAtLeast(1)
            val index = (touchX / segmentWidth).toInt().coerceIn(0, history.lastIndex)
            activeIndex = index
          }
        )
      }
      .padding(16.dp)
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val width = size.width
      val height = size.height
      val paddingBottom = 30f
      val plotHeight = height - paddingBottom

      if (values.isNotEmpty()) {
        val stepX = width / (values.size - 1).coerceAtLeast(1)
        val range = (maxVal - minVal).coerceAtLeast(1f)

        // Línea de base y grilla
        for (i in 0..3) {
          val y = plotHeight * (i / 3f)
          drawLine(
            color = if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.08f),
            start = Offset(0f, y),
            end = Offset(width, y),
            strokeWidth = 2f
          )
        }

        // Trazado cúbico suave
        val path = Path()
        val fillPath = Path()

        val points = values.mapIndexed { i, v ->
          val x = i * stepX
          val y = plotHeight - ((v - minVal) / range) * plotHeight
          Offset(x, y)
        }

        points.forEachIndexed { i, pt ->
          if (i == 0) {
            path.moveTo(pt.x, pt.y)
            fillPath.moveTo(pt.x, pt.y)
          } else {
            val prev = points[i - 1]
            val midX = (prev.x + pt.x) / 2
            path.cubicTo(midX, prev.y, midX, pt.y, pt.x, pt.y)
            fillPath.cubicTo(midX, prev.y, midX, pt.y, pt.x, pt.y)
          }
        }

        fillPath.lineTo(points.last().x, plotHeight)
        fillPath.lineTo(points.first().x, plotHeight)
        fillPath.close()

        val chartPrimary = if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7)

        // Dibujar área con gradiente
        drawPath(
          path = fillPath,
          brush = Brush.verticalGradient(
            listOf(chartPrimary.copy(alpha = 0.35f), chartPrimary.copy(alpha = 0.02f))
          )
        )

        // Dibujar línea principal
        drawPath(
          path = path,
          color = chartPrimary,
          style = Stroke(width = 5f, cap = StrokeCap.Round)
        )

        // Dibujar puntos
        points.forEachIndexed { i, pt ->
          val isPr = history.getOrNull(i)?.isPr == true
          val isSelected = activeIndex == i

          drawCircle(
            color = if (isPr) Color(0xFFF59E0B) else chartPrimary,
            radius = if (isSelected) 9f else if (isPr) 7f else 5f,
            center = pt
          )
          drawCircle(
            color = Color.White,
            radius = if (isSelected) 4f else 2.5f,
            center = pt
          )
        }
      }
    }

    // Tooltip flotante si se hace scrub con el dedo
    if (activeIndex in history.indices) {
      val item = history[activeIndex]
      val value = when (metric) {
        ChartMetric.ONE_RM -> item.estimated1Rm
        ChartMetric.WEIGHT -> item.weightKg
        ChartMetric.VOLUME -> item.volumeKg
      }

      Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        modifier = Modifier.align(Alignment.TopCenter)
      ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
          Text(
            text = "${item.dateIso} ${if (item.isPr) "🏆 PR" else ""}",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
          )
          Text(
            text = "${(value * 10).roundToInt() / 10f} ${metric.unit} (${item.weightKg}kg x ${item.reps} reps)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
          )
        }
      }
    }
  }
}

/**
 * Tarjeta detallada con instrucciones biomecánicas, músculos involucrados y errores comunes.
 */
@Composable
fun ExerciseInstructionsCard(
  guide: ExerciseVisualGuide,
  isDark: Boolean
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = if (isDark) 0.35f else 0.5f)),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("exercise_instructions_card")
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Text(
        text = "📖 Protocolo de Ejecución Biomecánica",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(14.dp))

      // 1. Desglose de Músculos Principales y Secundarios
      Text(
        text = "MÚSCULOS DIANA (TARGET)",
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        letterSpacing = 1.sp
      )
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        guide.primaryMuscles.forEach { muscle ->
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
          ) {
            Text(
              text = "🎯 $muscle",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "ESTABILIZADORES SECUNDARIOS",
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        letterSpacing = 1.sp
      )
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        guide.secondaryMuscles.forEach { muscle ->
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
          ) {
            Text(
              text = "⚡ $muscle",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 2. Cadencia de Respiración e Instrucción Clave
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = "💨", fontSize = 24.sp)
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "Patrón Respiratorio Intra-Abdominal",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Text(
              text = guide.breathingCue,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onTertiaryContainer
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 3. Puntos Clave de Ejecución (Checklist)
      Text(
        text = "PUNTOS CLAVE DE SEGURIDAD Y RENDIMIENTO",
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        letterSpacing = 1.sp
      )
      Spacer(modifier = Modifier.height(8.dp))

      guide.keyPoints.forEach { point ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          verticalAlignment = Alignment.Top
        ) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF10B981),
            modifier = Modifier
              .size(18.dp)
              .padding(top = 2.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = point,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }
    }
  }
}

/**
 * Lista cronológica con el historial de series y récords personales registrados para este ejercicio.
 */
@Composable
fun ExerciseSessionHistoryList(
  history: List<ExerciseHistoryPoint>,
  isDark: Boolean
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = if (isDark) 0.35f else 0.5f)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Text(
        text = "📅 Registros Históricos de Sesiones",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(12.dp))

      history.reversed().take(5).forEach { session ->
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (isDark) 0.6f else 0.8f),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = session.dateIso,
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
                if (session.isPr) {
                  Spacer(modifier = Modifier.width(6.dp))
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFFEF3C7),
                    border = BorderStroke(1.dp, Color(0xFFF59E0B))
                  ) {
                    Text(
                      text = "🏆 RÉCORD PR",
                      style = MaterialTheme.typography.labelSmall,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFFB45309),
                      fontSize = 9.sp,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "Serie tope: ${session.weightKg} kg x ${session.reps} reps",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "${(session.estimated1Rm * 10).roundToInt() / 10f} kg",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
              Text(
                text = "1RM Brzycki",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 9.sp
              )
            }
          }
        }
      }
    }
  }
}

private data class Quadruple<A, B, C, D>(
  val first: A,
  val second: B,
  val third: C,
  val fourth: D
)
