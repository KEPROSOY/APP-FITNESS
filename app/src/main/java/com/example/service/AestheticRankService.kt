package com.example.service

import com.example.data.model.AestheticMuscleCategory
import com.example.data.model.AestheticRank
import com.example.data.model.DailyWorkoutRecommendation
import com.example.data.model.MuscleCategoryRank
import com.example.data.model.PersonalRecord
import com.example.data.model.RecommendedExercise
import com.example.data.model.WorkoutSession
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * AestheticRankService
 *
 * Responsabilidades:
 * 1. Calcula el nivel de progreso estético del usuario (desde Madera hasta Diamante Olímpico)
 *    evaluando la carga relativa (1RM / peso corporal) y el volumen acumulado de entrenamiento.
 * 2. Proporciona sugerencias inteligentes de rutinas y grupos musculares diarios para optimizar
 *    la proporción áurea y silueta V-Taper, priorizando grupos musculares rezagados.
 */
class AestheticRankService {

  data class ProgressLevelResult(
    val currentRank: AestheticRank,
    val nextRank: AestheticRank?,
    val progressPercentage: Float, // 0.0f a 1.0f
    val overallScore: Float,
    val relativeLoadRatio: Float,
    val totalVolumeKg: Float,
    val categoryRanks: Map<AestheticMuscleCategory, AestheticRank>,
    val laggingCategories: List<AestheticMuscleCategory>,
    val dominantCategories: List<AestheticMuscleCategory>,
    val feedbackMessage: String
  )

  /**
   * Calcula el 1RM estimado usando la fórmula biomecánica de Brzycki:
   * 1RM = Peso / (1.0278 - (0.0278 * Reps))
   */
  fun calculateEstimated1Rm(weightKg: Float, reps: Int): Float {
    if (weightKg <= 0f || reps <= 0) return 0f
    if (reps == 1) return weightKg
    val cappedReps = min(reps, 15) // Brzycki es más preciso entre 1 y 15 reps
    val denominator = 1.0278f - (0.0278f * cappedReps)
    return if (denominator > 0f) weightKg / denominator else weightKg
  }

  /**
   * Determina el rango estético (de Madera a Diamante Olímpico) según el ratio de carga relativa:
   * Madera (< 0.6x) -> Hierro (0.6x) -> Oro (0.9x) -> Platino (1.2x) -> Esmeralda (1.5x) -> Diamante (1.8x) -> Diamante Olímpico (>= 2.1x)
   */
  fun getRankForRelativeLoad(ratio: Float): AestheticRank {
    return when {
      ratio >= AestheticRank.DIAMANTE_OLIMPICO.minRatio -> AestheticRank.DIAMANTE_OLIMPICO
      ratio >= AestheticRank.DIAMANTE.minRatio -> AestheticRank.DIAMANTE
      ratio >= AestheticRank.ESMERALDA.minRatio -> AestheticRank.ESMERALDA
      ratio >= AestheticRank.PLATINO.minRatio -> AestheticRank.PLATINO
      ratio >= AestheticRank.ORO.minRatio -> AestheticRank.ORO
      ratio >= AestheticRank.HIERRO.minRatio -> AestheticRank.HIERRO
      else -> AestheticRank.MADERA
    }
  }

  /**
   * Calcula el nivel global de progreso estético considerando:
   * - Carga relativa promedio (1RM ponderado / peso corporal del usuario)
   * - Volumen total acumulado (tonelaje de entrenamiento)
   */
  fun calculateProgressLevel(
    userWeightKg: Float,
    personalRecords: List<PersonalRecord>,
    totalVolumeKg: Float = 0f
  ): ProgressLevelResult {
    val effectiveBodyWeight = if (userWeightKg > 30f) userWeightKg else 75.0f

    // Si no hay récords personales, asignar nivel inicial de Madera
    if (personalRecords.isEmpty()) {
      return ProgressLevelResult(
        currentRank = AestheticRank.MADERA,
        nextRank = AestheticRank.HIERRO,
        progressPercentage = 0.15f,
        overallScore = 15f,
        relativeLoadRatio = 0.2f,
        totalVolumeKg = totalVolumeKg,
        categoryRanks = emptyMap(),
        laggingCategories = listOf(AestheticMuscleCategory.V_TAPER_DELTS, AestheticMuscleCategory.V_TAPER_LATS),
        dominantCategories = emptyList(),
        feedbackMessage = "Comienza registrando tus primeras series para desbloquear tu rango en Hierro."
      )
    }

    // Calcular ratio de carga por categoría muscular
    val categoryRatios = mutableMapOf<AestheticMuscleCategory, Float>()
    val categoryRanks = mutableMapOf<AestheticMuscleCategory, AestheticRank>()

    AestheticMuscleCategory.values().forEach { category ->
      val recordsForCategory = personalRecords.filter { it.category == category }
      if (recordsForCategory.isNotEmpty()) {
        val best1Rm = recordsForCategory.maxOfOrNull { it.estimated1Rm } ?: 0f
        val ratio = best1Rm / effectiveBodyWeight
        categoryRatios[category] = ratio
        categoryRanks[category] = getRankForRelativeLoad(ratio)
      } else {
        categoryRatios[category] = 0.3f
        categoryRanks[category] = AestheticRank.MADERA
      }
    }

    // Ponderación de categorías: Deltoides y Dorsales tienen un 40% de peso por V-Taper estético
    val vTaperRatio = ((categoryRatios[AestheticMuscleCategory.V_TAPER_DELTS] ?: 0.4f) +
        (categoryRatios[AestheticMuscleCategory.V_TAPER_LATS] ?: 0.4f)) / 2f

    val averageRatio = if (categoryRatios.isNotEmpty()) categoryRatios.values.average().toFloat() else 0.5f

    // Factor de volumen: bonificación hasta +0.2 al ratio si el volumen acumulado supera umbrales clave
    val volumeBonus = when {
      totalVolumeKg >= 50000f -> 0.20f
      totalVolumeKg >= 25000f -> 0.15f
      totalVolumeKg >= 10000f -> 0.10f
      totalVolumeKg >= 3000f -> 0.05f
      else -> 0.0f
    }

    // Ratio efectivo ponderado (60% carga relativa, 25% V-Taper, 15% bonificación de volumen)
    val combinedRatio = (averageRatio * 0.60f) + (vTaperRatio * 0.25f) + volumeBonus
    val currentRank = getRankForRelativeLoad(combinedRatio)

    // Determinar siguiente rango y porcentaje de avance
    val nextRank = when (currentRank) {
      AestheticRank.MADERA -> AestheticRank.HIERRO
      AestheticRank.HIERRO -> AestheticRank.ORO
      AestheticRank.ORO -> AestheticRank.PLATINO
      AestheticRank.PLATINO -> AestheticRank.ESMERALDA
      AestheticRank.ESMERALDA -> AestheticRank.DIAMANTE
      AestheticRank.DIAMANTE -> AestheticRank.DIAMANTE_OLIMPICO
      AestheticRank.DIAMANTE_OLIMPICO -> null
    }

    val progressPercentage = if (nextRank != null) {
      val minCurrent = currentRank.minRatio
      val minNext = nextRank.minRatio
      val span = minNext - minCurrent
      if (span > 0f) ((combinedRatio - minCurrent) / span).coerceIn(0f, 1f) else 1f
    } else {
      1.0f
    }

    // Identificar grupos musculares rezagados y dominantes
    val laggingCategories = categoryRatios.filter { it.value < averageRatio * 0.85f }.keys.toList()
    val dominantCategories = categoryRatios.filter { it.value >= averageRatio * 1.15f }.keys.toList()

    val overallScore = ((combinedRatio / 2.1f) * 100f).coerceIn(10f, 100f)

    val feedbackMessage = when (currentRank) {
      AestheticRank.MADERA -> "Fase de adaptación y acondicionamiento biomecánico base."
      AestheticRank.HIERRO -> "Cimientos de fuerza establecidos. Enfoca sobrecarga progresiva en hombros y espalda."
      AestheticRank.ORO -> "Hipertrofia visible. Excelente balance de fuerza y desarrollo muscular."
      AestheticRank.PLATINO -> "Silueta V-Taper pronunciada. Estructura estética de alto nivel competitivo."
      AestheticRank.ESMERALDA -> "Fuerza relativa élite y densidad miofibrilar avanzada."
      AestheticRank.DIAMANTE -> "Físico clásico altamente simétrico, esculpido y equilibrado."
      AestheticRank.DIAMANTE_OLIMPICO -> "Nivel máximo de excelencia estética y proporción áurea natural."
    }

    return ProgressLevelResult(
      currentRank = currentRank,
      nextRank = nextRank,
      progressPercentage = progressPercentage,
      overallScore = overallScore,
      relativeLoadRatio = combinedRatio,
      totalVolumeKg = totalVolumeKg,
      categoryRanks = categoryRanks,
      laggingCategories = laggingCategories,
      dominantCategories = dominantCategories,
      feedbackMessage = feedbackMessage
    )
  }

  /**
   * Sugiere el entrenamiento y grupos musculares para el día actual o especificado,
   * adaptando el volumen y ejercicio para priorizar grupos rezagados.
   */
  fun suggestDailyMuscleGroupWorkouts(
    dayOfWeek: String? = null,
    laggingCategories: List<AestheticMuscleCategory> = emptyList()
  ): DailyWorkoutRecommendation {
    val targetDay = dayOfWeek ?: getTodayDayName()
    val normalizedDay = targetDay.lowercase(Locale.ROOT)

    val isDeltsLagging = laggingCategories.contains(AestheticMuscleCategory.V_TAPER_DELTS)
    val isLatsLagging = laggingCategories.contains(AestheticMuscleCategory.V_TAPER_LATS)
    val isChestLagging = laggingCategories.contains(AestheticMuscleCategory.CHEST_UPPER_LOWER)

    return when {
      normalizedDay.contains("lunes") || normalizedDay.contains("monday") -> {
        DailyWorkoutRecommendation(
          dayOfWeek = "Lunes",
          title = "V-Taper Primario: Deltoides & Pectoral Superior",
          primaryFocus = AestheticMuscleCategory.V_TAPER_DELTS,
          secondaryFocus = AestheticMuscleCategory.CHEST_UPPER_LOWER,
          rationale = if (isDeltsLagging) {
            "Prioridad alta: deltoides laterales detectados en rezago. Iniciamos la semana estimulando el ancho clavicular."
          } else {
            "Inicio de semana enfocado en la anchura de hombros y el haz clavicular para una silueta en V prominente."
          },
          suggestedExercises = listOf(
            RecommendedExercise(
              name = "Press Militar con Barra de Pie",
              sets = if (isDeltsLagging) 5 else 4,
              repsRange = "6-8",
              targetRpe = 8.5f,
              focusNote = "Máxima aceleración concéntrica y bloqueo vertical controlado."
            ),
            RecommendedExercise(
              name = "Elevaciones Laterales en Polea a 45°",
              sets = if (isDeltsLagging) 5 else 4,
              repsRange = "12-15",
              targetRpe = 9.0f,
              focusNote = "Alineación estricta con el plano escapular; tensión continua."
            ),
            RecommendedExercise(
              name = "Press Inclinado con Mancuernas (30°)",
              sets = 4,
              repsRange = "8-10",
              targetRpe = 8.0f,
              focusNote = "Énfasis en haz clavicular sin comprometer la cápsula anterior del hombro."
            ),
            RecommendedExercise(
              name = "Cruces en Polea Baja",
              sets = 3,
              repsRange = "12-15",
              targetRpe = 8.5f,
              focusNote = "Convergencia superior manteniendo protracción escapular al final."
            )
          )
        )
      }

      normalizedDay.contains("martes") || normalizedDay.contains("tuesday") -> {
        DailyWorkoutRecommendation(
          dayOfWeek = "Martes",
          title = "V-Taper Anchura: Dorsales & Espalda Alta",
          primaryFocus = AestheticMuscleCategory.V_TAPER_LATS,
          secondaryFocus = AestheticMuscleCategory.V_TAPER_DELTS,
          rationale = if (isLatsLagging) {
            "Dorsales en foco prioritario: mayor volumen en jalones con agarre neutro para elongación máxima."
          } else {
            "Desarrollo de amplitud dorsal para maximizar el contraste con la cintura estrecha."
          },
          suggestedExercises = listOf(
            RecommendedExercise(
              name = "Dominadas Lastradas Pronas",
              sets = 4,
              repsRange = "6-8",
              targetRpe = 8.5f,
              focusNote = "Inicio con depresión escapular pura; codos hacia las costillas."
            ),
            RecommendedExercise(
              name = "Jalón al Pecho Agarre Neutro Unilateral",
              sets = 4,
              repsRange = "10-12",
              targetRpe = 8.0f,
              focusNote = "Foco en fibras ilíacas del dorsal ancho con recorrido completo."
            ),
            RecommendedExercise(
              name = "Remo con Barra en T (Pecho Apoyado)",
              sets = 4,
              repsRange = "8-10",
              targetRpe = 8.5f,
              focusNote = "Aducción escapular firme para densidad de romboides y trapecio medio."
            ),
            RecommendedExercise(
              name = "Face Pulls con Doble Cuerda",
              sets = 3,
              repsRange = "15-20",
              targetRpe = 8.0f,
              focusNote = "Rotación externa al final; salud articular y deltoides posterior."
            )
          )
        )
      }

      normalizedDay.contains("miércoles") || normalizedDay.contains("wednesday") -> {
        DailyWorkoutRecommendation(
          dayOfWeek = "Miércoles",
          title = "Pilar Inferior: Pierna & Cintura (Cuádriceps/Isquios)",
          primaryFocus = AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS,
          secondaryFocus = AestheticMuscleCategory.CORE_WAIST,
          rationale = "Construcción de la base atlética sin ensanchar la cintura para mantener el ratio V-Taper.",
          suggestedExercises = listOf(
            RecommendedExercise(
              name = "Sentadilla Hack Profunda",
              sets = 4,
              repsRange = "8-10",
              targetRpe = 8.5f,
              focusNote = "Descenso en 3 segundos; máximo reclutamiento del vasto externo."
            ),
            RecommendedExercise(
              name = "Peso Muerto Rumano con Mancuernas",
              sets = 4,
              repsRange = "8-10",
              targetRpe = 8.0f,
              focusNote = "Empuje de cadera hacia atrás; estiramiento isquiosural activo."
            ),
            RecommendedExercise(
              name = "Extensiones de Cuádriceps (Pausa 2s)",
              sets = 3,
              repsRange = "12-15",
              targetRpe = 9.0f,
              focusNote = "Tensión máxima en acortamiento en la cúspide del movimiento."
            ),
            RecommendedExercise(
              name = "Elevaciones de Piernas Colgado (Vacío Abdominal)",
              sets = 3,
              repsRange = "12-15",
              targetRpe = 8.0f,
              focusNote = "Control pélvico; contracción del transverso para cintura estrecha."
            )
          )
        )
      }

      normalizedDay.contains("jueves") || normalizedDay.contains("thursday") -> {
        DailyWorkoutRecommendation(
          dayOfWeek = "Jueves",
          title = "Escultura de Brazos & Hombro Posterior",
          primaryFocus = AestheticMuscleCategory.ARMS_BICEPS_TRICEPS,
          secondaryFocus = AestheticMuscleCategory.V_TAPER_DELTS,
          rationale = "Desarrollo del pico de bíceps y cabeza lateral del tríceps para densidad en perfil y frontal.",
          suggestedExercises = listOf(
            RecommendedExercise(
              name = "Press Francés Inclinado con Barra Z",
              sets = 4,
              repsRange = "10-12",
              targetRpe = 8.0f,
              focusNote = "Énfasis en la cabeza larga del tríceps con codos cerrados."
            ),
            RecommendedExercise(
              name = "Curl Inclinado con Mancuernas (60°)",
              sets = 4,
              repsRange = "10-12",
              targetRpe = 8.5f,
              focusNote = "Estiramiento máximo del bíceps braquial sin compensación de hombros."
            ),
            RecommendedExercise(
              name = "Extensiones en Polea con Cuerda",
              sets = 3,
              repsRange = "12-15",
              targetRpe = 9.0f,
              focusNote = "Apertura en la parte inferior para pico de cabeza lateral."
            ),
            RecommendedExercise(
              name = "Pájaros en Máquina Contractora Invertida",
              sets = 4,
              repsRange = "15-20",
              targetRpe = 8.5f,
              focusNote = "Redondez 3D del hombro posterior vista desde atrás."
            )
          )
        )
      }

      normalizedDay.contains("viernes") || normalizedDay.contains("friday") -> {
        DailyWorkoutRecommendation(
          dayOfWeek = "Viernes",
          title = "Potenciación V-Taper: Espalda Amplitud & Deltoides",
          primaryFocus = AestheticMuscleCategory.V_TAPER_LATS,
          secondaryFocus = AestheticMuscleCategory.V_TAPER_DELTS,
          rationale = "Segunda sesión semanal de frecuencia 2 en los grupos clave de la silueta en V.",
          suggestedExercises = listOf(
            RecommendedExercise(
              name = "Remo Unilateral en Polea Baja",
              sets = 4,
              repsRange = "8-10",
              targetRpe = 8.5f,
              focusNote = "Recorrido largo con rotación moderada de torso para dorsal ilíaco."
            ),
            RecommendedExercise(
              name = "Elevaciones Laterales Inclinadas en Banco",
              sets = 4,
              repsRange = "12-15",
              targetRpe = 9.0f,
              focusNote = "Aisla la cabeza media eliminando inercia del cuerpo."
            ),
            RecommendedExercise(
              name = "Pull-over con Mancuerna en Banco",
              sets = 3,
              repsRange = "10-12",
              targetRpe = 8.0f,
              focusNote = "Expansión torácica y elongación dorsal sin sobreextender codos."
            ),
            RecommendedExercise(
              name = "Elevaciones Lu Raises",
              sets = 3,
              repsRange = "12-15",
              targetRpe = 7.5f,
              focusNote = "Recorrido completo hasta arriba para activación completa del deltoides."
            )
          )
        )
      }

      normalizedDay.contains("sábado") || normalizedDay.contains("saturday") -> {
        DailyWorkoutRecommendation(
          dayOfWeek = "Sábado",
          title = "Detalle Estético & Cadena Posterior",
          primaryFocus = AestheticMuscleCategory.V_TAPER_LATS,
          secondaryFocus = AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS,
          rationale = "Densidad en trapecios, romboides y femorales para un físico equilibrado en 360 grados.",
          suggestedExercises = listOf(
            RecommendedExercise(
              name = "Remo Gironda con Agarre Estrecho",
              sets = 4,
              repsRange = "10-12",
              targetRpe = 8.0f,
              focusNote = "Pausa de 1 segundo en máxima contracción escapular."
            ),
            RecommendedExercise(
              name = "Curl Femoral Tumbado",
              sets = 4,
              repsRange = "10-12",
              targetRpe = 8.5f,
              focusNote = "Punta de pies en flexión plantar; excéntrica de 3 segundos."
            ),
            RecommendedExercise(
              name = "Encogimientos Haney con Barra por Detrás",
              sets = 3,
              repsRange = "12-15",
              targetRpe = 8.0f,
              focusNote = "Aislamiento del trapecio sin empujar la cabeza hacia adelante."
            ),
            RecommendedExercise(
              name = "Gemelos de Pie en Máquina Smith",
              sets = 4,
              repsRange = "12-15",
              targetRpe = 9.0f,
              focusNote = "Pausa de 2 segundos en el fondo para disipar reflejo miotático."
            )
          )
        )
      }

      else -> {
        DailyWorkoutRecommendation(
          dayOfWeek = "Domingo",
          title = "Recuperación Activa, Descompresión & Vacío",
          primaryFocus = AestheticMuscleCategory.CORE_WAIST,
          secondaryFocus = null,
          rationale = "Descompresión vertebral, movilidad articular y entrenamiento del transverso del abdomen.",
          suggestedExercises = listOf(
            RecommendedExercise(
              name = "Vacío Abdominal en Cuadrupedia",
              sets = 4,
              repsRange = "20s mantención",
              targetRpe = 6.0f,
              focusNote = "Exhalación total y succión del ombligo hacia la columna vertebral."
            ),
            RecommendedExercise(
              name = "Descompresión Colgado de Barra",
              sets = 3,
              repsRange = "45-60 segundos",
              targetRpe = 5.0f,
              focusNote = "Relajación total de la fascia lumbar y elongación de lats."
            ),
            RecommendedExercise(
              name = "Caminata Ligera al Aire Libre (Zona 2)",
              sets = 1,
              repsRange = "30-40 minutos",
              targetRpe = 4.0f,
              focusNote = "Oxigenación muscular y aceleración del aclaramiento de metabolitos."
            )
          )
        )
      }
    }
  }

  /**
   * Sugiere la lista de grupos musculares a trabajar en el día indicado.
   */
  fun suggestDailyMuscleGroups(dayOfWeek: String? = null): List<AestheticMuscleCategory> {
    val recommendation = suggestDailyMuscleGroupWorkouts(dayOfWeek)
    return listOfNotNull(recommendation.primaryFocus, recommendation.secondaryFocus)
  }

  /**
   * Calcula el rango estético combinando la carga relativa (1RM / peso corporal)
   * y el volumen acumulado en kg.
   */
  fun calculateAestheticRank(relativeLoad: Float, totalVolumeKg: Float = 0f): AestheticRank {
    val volumeBonus = when {
      totalVolumeKg >= 50000f -> 0.20f
      totalVolumeKg >= 25000f -> 0.15f
      totalVolumeKg >= 10000f -> 0.10f
      totalVolumeKg >= 3000f -> 0.05f
      else -> 0.0f
    }
    val effectiveRatio = relativeLoad + volumeBonus
    return getRankForRelativeLoad(effectiveRatio)
  }

  private fun getTodayDayName(): String {
    return try {
      val calendar = Calendar.getInstance()
      val dayFormat = SimpleDateFormat("EEEE", Locale.forLanguageTag("es-ES"))
      dayFormat.format(calendar.time)
    } catch (_: Exception) {
      "Lunes"
    }
  }
}
