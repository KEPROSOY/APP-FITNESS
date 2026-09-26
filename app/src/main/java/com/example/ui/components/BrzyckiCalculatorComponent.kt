package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.ExerciseDatabase
import com.example.data.model.ExerciseVisualGuide
import com.example.fitness.FitnessEngine
import kotlin.math.roundToInt

/**
 * Modelo de entrada de serie para la calculadora Brzycki.
 */
data class BrzyckiSetInput(
  val setNumber: Int,
  val weightKgText: String,
  val repsText: String
) {
  val weightKg: Float get() = weightKgText.toFloatOrNull() ?: 0f
  val reps: Int get() = repsText.toIntOrNull() ?: 0

  /**
   * Cálculo de 1RM según la fórmula de Matt Brzycki:
   * 1RM = Peso * (36 / (37 - Reps))
   * Para 1 rep, 1RM = Peso.
   * Rango válido: 1 a 30 reps (por encima de 30 la fórmula pierde validez fisiológica).
   */
  val calculated1Rm: Float
    get() {
      if (weightKg <= 0f || reps <= 0) return 0f
      if (reps == 1) return weightKg
      val safeReps = reps.coerceIn(1, 30)
      val brzycki = weightKg * (36f / (37f - safeReps))
      return (brzycki * 10f).roundToInt() / 10f
    }
}

/**
 * Zona de entrenamiento porcentual basada en 1RM.
 */
data class BrzyckiTrainingZone(
  val percentage: Int,
  val targetReps: String,
  val focusName: String,
  val targetWeightKg: Float
)

/**
 * Componente interactivo de Calculadora 1RM mediante fórmula de Brzycki
 * para un ejercicio específico con soporte para múltiples series y repeticiones.
 */
@Composable
fun BrzyckiCalculatorCard(
  initialExerciseName: String = "Press Militar con Barra",
  isDark: Boolean = true,
  onSaveRecord: ((exerciseName: String, best1Rm: Float, bestWeight: Float, bestReps: Int) -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  var exerciseName by remember(initialExerciseName) { mutableStateOf(initialExerciseName) }
  var showExerciseSelector by remember { mutableStateOf(false) }
  var exerciseSearchQuery by remember { mutableStateOf("") }

  val allGuides = remember { ExerciseDatabase.getAllExercises() }
  val matchedGuide = remember(exerciseName) { ExerciseDatabase.findExercise(exerciseName) }

  // Series introducidas por el usuario
  var setsList by remember {
    mutableStateOf(
      listOf(
        BrzyckiSetInput(setNumber = 1, weightKgText = "80", repsText = "8"),
        BrzyckiSetInput(setNumber = 2, weightKgText = "85", repsText = "6")
      )
    )
  }

  // Mejor 1RM y mejor serie según Brzycki
  val bestSetWith1Rm = remember(setsList) {
    setsList.filter { it.calculated1Rm > 0f }.maxByOrNull { it.calculated1Rm }
  }
  val globalBest1Rm = bestSetWith1Rm?.calculated1Rm ?: 0f

  // Proyecciones de carga de la fórmula de Brzycki
  val trainingZones = remember(globalBest1Rm) {
    if (globalBest1Rm <= 0f) emptyList()
    else {
      listOf(
        BrzyckiTrainingZone(100, "1 rep", "Fuerza Máxima (1RM)", globalBest1Rm),
        BrzyckiTrainingZone(95, "2 reps", "Fuerza Pura", ((globalBest1Rm * 0.95f) * 10).roundToInt() / 10f),
        BrzyckiTrainingZone(90, "3-4 reps", "Fuerza / Potencia", ((globalBest1Rm * 0.90f) * 10).roundToInt() / 10f),
        BrzyckiTrainingZone(85, "5-6 reps", "Hipertrofia Pesada", ((globalBest1Rm * 0.85f) * 10).roundToInt() / 10f),
        BrzyckiTrainingZone(80, "7-8 reps", "Hipertrofia Clásica", ((globalBest1Rm * 0.80f) * 10).roundToInt() / 10f),
        BrzyckiTrainingZone(75, "9-10 reps", "Volumen / Tensión", ((globalBest1Rm * 0.75f) * 10).roundToInt() / 10f),
        BrzyckiTrainingZone(70, "11-12 reps", "Resistencia a la Fuerza", ((globalBest1Rm * 0.70f) * 10).roundToInt() / 10f),
        BrzyckiTrainingZone(65, "15 reps", "Bombeo Sarcoplasmático", ((globalBest1Rm * 0.65f) * 10).roundToInt() / 10f)
      )
    }
  }

  Card(
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isDark) Color(0xFF131D31) else Color(0xFFFFFFFF)
    ),
    border = BorderStroke(
      1.dp,
      if (isDark) MaterialTheme.colorScheme.primary.copy(alpha = 0.45f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    ),
    modifier = modifier
      .fillMaxWidth()
      .animateContentSize()
      .testTag("brzycki_calculator_card")
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      // Cabecera con selector del ejercicio específico
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            modifier = Modifier.size(40.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = matchedGuide?.category?.iconEmoji ?: "🏋️",
                fontSize = 20.sp
              )
            }
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Calculadora 1RM (Brzycki)",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Fórmula: Peso × [36 / (37 - Reps)]",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.primary,
              fontSize = 11.sp
            )
          }
        }

        OutlinedButton(
          onClick = { showExerciseSelector = !showExerciseSelector },
          shape = RoundedCornerShape(10.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
        ) {
          Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Cambiar", fontSize = 11.sp)
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Ejercicio Seleccionado Badge
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isDark) Color(0xFF0F172A) else Color(0xFFF1F5F9),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { showExerciseSelector = !showExerciseSelector }
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              Icons.Default.FitnessCenter,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = exerciseName,
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
          if (matchedGuide != null) {
            Text(
              text = matchedGuide.targetMuscleGroup,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      // Desplegable de búsqueda de ejercicio
      AnimatedVisibility(visible = showExerciseSelector) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
        ) {
          OutlinedTextField(
            value = exerciseSearchQuery,
            onValueChange = { exerciseSearchQuery = it },
            placeholder = { Text("Buscar ejercicio del catálogo...", fontSize = 12.sp) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) }
          )

          val filtered = remember(exerciseSearchQuery, allGuides) {
            if (exerciseSearchQuery.isBlank()) allGuides.take(6)
            else allGuides.filter {
              it.exerciseName.contains(exerciseSearchQuery, ignoreCase = true) ||
                it.targetMuscleGroup.contains(exerciseSearchQuery, ignoreCase = true)
            }.take(8)
          }

          Spacer(modifier = Modifier.height(6.dp))

          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            filtered.forEach { guide ->
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (guide.exerciseName == exerciseName) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                else Color.Transparent,
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable {
                    exerciseName = guide.exerciseName
                    showExerciseSelector = false
                  }
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(guide.category.iconEmoji, fontSize = 14.sp)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = guide.exerciseName,
                    fontSize = 12.sp,
                    fontWeight = if (guide.exerciseName == exerciseName) FontWeight.Bold else FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))
      HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
      Spacer(modifier = Modifier.height(14.dp))

      // Input de Series y Repeticiones
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "SERIES & REPETICIONES DEL EJERCICIO",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          letterSpacing = 0.8.sp
        )

        TextButton(
          onClick = {
            val nextNum = setsList.size + 1
            val lastSet = setsList.lastOrNull()
            setsList = setsList + BrzyckiSetInput(
              setNumber = nextNum,
              weightKgText = lastSet?.weightKgText ?: "80",
              repsText = lastSet?.repsText ?: "8"
            )
          },
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Añadir Serie", fontSize = 12.sp)
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Lista de inputs por serie
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        setsList.forEachIndexed { index, setItem ->
          val isBest = setItem == bestSetWith1Rm && globalBest1Rm > 0f

          Surface(
            shape = RoundedCornerShape(14.dp),
            color = if (isBest) {
              MaterialTheme.colorScheme.primaryContainer.copy(alpha = if (isDark) 0.3f else 0.45f)
            } else {
              if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
            },
            border = BorderStroke(
              1.dp,
              if (isBest) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            ),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Indicador de serie
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = CircleShape,
                  color = if (isBest) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                  modifier = Modifier.size(24.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Text(
                      text = "${setItem.setNumber}",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (isBest) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }
                Spacer(modifier = Modifier.width(8.dp))
                if (isBest) {
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primary
                  ) {
                    Text(
                      text = "TOP 1RM",
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color.White,
                      modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                  }
                }
              }

              // Inputs de Peso y Reps
              Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                // Peso
                OutlinedTextField(
                  value = setItem.weightKgText,
                  onValueChange = { newVal ->
                    setsList = setsList.toMutableList().also {
                      it[index] = setItem.copy(weightKgText = newVal)
                    }
                  },
                  label = { Text("kg", fontSize = 10.sp) },
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                  singleLine = true,
                  modifier = Modifier
                    .width(72.dp)
                    .height(52.dp),
                  shape = RoundedCornerShape(8.dp)
                )

                Text("×", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)

                // Reps
                OutlinedTextField(
                  value = setItem.repsText,
                  onValueChange = { newVal ->
                    setsList = setsList.toMutableList().also {
                      it[index] = setItem.copy(repsText = newVal)
                    }
                  },
                  label = { Text("reps", fontSize = 10.sp) },
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                  singleLine = true,
                  modifier = Modifier
                    .width(68.dp)
                    .height(52.dp),
                  shape = RoundedCornerShape(8.dp)
                )

                // 1RM Calculado para esta serie
                Column(horizontalAlignment = Alignment.End, modifier = Modifier.width(65.dp)) {
                  Text(
                    text = "${setItem.calculated1Rm} kg",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (isBest) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "Brzycki",
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }

                // Botón eliminar serie si hay más de 1
                if (setsList.size > 1) {
                  IconButton(
                    onClick = {
                      setsList = setsList.filterIndexed { i, _ -> i != index }
                        .mapIndexed { newIdx, s -> s.copy(setNumber = newIdx + 1) }
                    },
                    modifier = Modifier.size(24.dp)
                  ) {
                    Icon(
                      Icons.Default.Clear,
                      contentDescription = "Eliminar serie",
                      tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                      modifier = Modifier.size(16.dp)
                    )
                  }
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Banner Principal de Resultado 1RM Brzycki
      Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              Icons.Default.Calculate,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "1RM Brzycki Máximo Estimado",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "$globalBest1Rm kg",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
          )

          if (bestSetWith1Rm != null) {
            Text(
              text = "Logrado con Serie ${bestSetWith1Rm.setNumber}: ${bestSetWith1Rm.weightKg}kg × ${bestSetWith1Rm.reps} reps",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
            )
          }
        }
      }

      // Proyecciones de carga Brzycki por repeticiones objetivo
      if (trainingZones.isNotEmpty()) {
        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "PROYECCIÓN DE CARGAS Y RANGOS (BRZYCKI)",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          letterSpacing = 0.8.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          trainingZones.forEach { zone ->
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (isDark) Color(0xFF0F172A) else Color(0xFFF1F5F9),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 12.dp, vertical = 7.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                  ) {
                    Text(
                      text = "${zone.percentage}%",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "${zone.targetReps} • ${zone.focusName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                }

                Text(
                  text = "${zone.targetWeightKg} kg",
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }
          }
        }
      }

      // Botón opcional para guardar como récord personal
      if (onSaveRecord != null && globalBest1Rm > 0f && bestSetWith1Rm != null) {
        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = {
            onSaveRecord(exerciseName, globalBest1Rm, bestSetWith1Rm.weightKg, bestSetWith1Rm.reps)
          },
          modifier = Modifier.fillMaxWidth().height(46.dp),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
          )
        ) {
          Icon(Icons.Default.EmojiEvents, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Guardar como Récord Personal (PR)", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

/**
 * Diálogo flotante modal para abrir la Calculadora 1RM Brzycki desde cualquier pantalla.
 */
@Composable
fun Brzycki1RmCalculatorDialog(
  initialExerciseName: String = "Press Militar con Barra",
  isDark: Boolean = true,
  onDismiss: () -> Unit,
  onSaveRecord: ((exerciseName: String, best1Rm: Float, bestWeight: Float, bestReps: Int) -> Unit)? = null
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = 0.6f))
        .padding(16.dp),
      contentAlignment = Alignment.Center
    ) {
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        item {
          BrzyckiCalculatorCard(
            initialExerciseName = initialExerciseName,
            isDark = isDark,
            onSaveRecord = onSaveRecord
          )
        }
        item {
          Spacer(modifier = Modifier.height(12.dp))
          Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth().height(44.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.surfaceVariant,
              contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
          ) {
            Text("Cerrar Calculadora", fontWeight = FontWeight.SemiBold)
          }
        }
      }
    }
  }
}
