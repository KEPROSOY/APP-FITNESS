package com.example.fitness

import com.example.data.model.*
import kotlin.math.roundToInt


/**
 * Motor matemático y de lógica de entrenamiento y estética muscular.
 */
object FitnessEngine {

  /**
   * Fórmula de Epley para estimar 1 Repetición Máxima (1RM):
   * 1RM = Peso * (1 + Reps / 30)
   */
  fun calculateEpley1Rm(weightKg: Float, reps: Int): Float {
    if (reps <= 0 || weightKg <= 0f) return 0f
    if (reps == 1) return weightKg
    return (weightKg * (1f + reps / 30f) * 10f).roundToInt() / 10f
  }

  /**
   * Fórmula de Brzycki:
   * 1RM = Peso * (36 / (37 - Reps))
   */
  fun calculateBrzycki1Rm(weightKg: Float, reps: Int): Float {
    if (reps <= 0 || weightKg <= 0f) return 0f
    if (reps >= 37) return weightKg * 2f
    if (reps == 1) return weightKg
    val oneRm = weightKg * (36f / (37f - reps.toFloat()))
    return (oneRm * 10f).roundToInt() / 10f
  }

  /**
   * Promedio compuesto ponderado entre Epley y Brzycki para mayor precisión en rangos de 2 a 12 reps.
   */
  fun estimate1Rm(weightKg: Float, reps: Int): Float {
    if (reps <= 0 || weightKg <= 0f) return 0f
    if (reps == 1) return weightKg
    val epley = calculateEpley1Rm(weightKg, reps)
    val brzycki = calculateBrzycki1Rm(weightKg, reps)
    return ((epley + brzycki) / 2f * 10f).roundToInt() / 10f
  }

  /**
   * Proyección de carga estimada para un número dado de repeticiones objetivo a partir del 1RM.
   * Por ejemplo: 5 reps (~87%), 8 reps (~80%), 10 reps (~75%), 12 reps (~70%).
   */
  fun estimateTargetLoad(oneRm: Float, targetReps: Int): Float {
    if (oneRm <= 0f || targetReps <= 0) return 0f
    if (targetReps == 1) return oneRm
    val percentage = when (targetReps) {
      2 -> 0.95f
      3 -> 0.93f
      4 -> 0.90f
      5 -> 0.87f
      6 -> 0.85f
      7 -> 0.83f
      8 -> 0.80f
      9 -> 0.77f
      10 -> 0.75f
      12 -> 0.70f
      15 -> 0.65f
      else -> (1f / (1f + targetReps / 30f)).coerceIn(0.5f, 1f)
    }
    return (oneRm * percentage * 2f).roundToInt() / 2f // Redondeado a los 0.5 kg más cercanos
  }

  /**
   * Estimación de gasto calórico metabólico para entrenamiento de hipertrofia/fuerza.
   * Fórmula basada en METs (Metabolic Equivalent of Task).
   * MET promedio para entrenamiento con pesas vigoroso = 5.5 - 6.0 METs.
   * Calorías quemadas = (MET * 3.5 * pesoKg / 200) * duraciónMinutos
   */
  fun calculateWorkoutCaloriesBurned(
    userWeightKg: Float,
    durationMinutes: Int,
    volumeKg: Float = 0f
  ): Int {
    val weight = if (userWeightKg > 30f) userWeightKg else 70f
    val minutes = durationMinutes.coerceIn(10, 180)
    val baseMet = 5.5f
    // Factor de intensidad por volumen: cada 5000 kg de volumen añade intensidad metabólica
    val volumeFactor = 1f + (volumeKg / 15000f).coerceIn(0f, 0.4f)
    val calories = (baseMet * 3.5f * weight / 200f) * minutes * volumeFactor
    return calories.roundToInt().coerceAtLeast(40)
  }

  /**
   * Análisis de distribución de volumen estético por grupo muscular para balance y simetría.
   */
  fun calculateSymmetryDistribution(exercises: List<WorkoutExercise>): List<AestheticVolumeDistribution> {
    val totalVolume = exercises.sumOf { it.totalVolumeKg.toDouble() }.toFloat()
    
    return AestheticMuscleCategory.values().map { category ->
      val categoryVolume = exercises
        .filter { it.category == category }
        .sumOf { it.totalVolumeKg.toDouble() }
        .toFloat()

      val actualRatio = if (totalVolume > 0f) categoryVolume / totalVolume else 0f
      val expectedRatio = category.targetRatioPercent

      // Se considera rezagado si está más del 30% por debajo del ratio objetivo
      val isLagging = totalVolume > 2000f && actualRatio < (expectedRatio * 0.70f)
      val isDominant = totalVolume > 2000f && actualRatio > (expectedRatio * 1.35f)

      AestheticVolumeDistribution(
        category = category,
        actualVolumeKg = categoryVolume,
        percentageOfTotal = actualRatio * 100f,
        isLagging = isLagging,
        isDominant = isDominant
      )
    }
  }

  /**
   * Determina el rango estético en base al ratio relativo (cargas relativas / peso corporal o densidad de volumen).
   */
  fun determineRankForRatio(ratio: Float): AestheticRank {
    return when {
      ratio >= com.example.data.model.AestheticRank.DIAMANTE_OLIMPICO.minRatio -> com.example.data.model.AestheticRank.DIAMANTE_OLIMPICO
      ratio >= com.example.data.model.AestheticRank.DIAMANTE.minRatio -> com.example.data.model.AestheticRank.DIAMANTE
      ratio >= com.example.data.model.AestheticRank.ESMERALDA.minRatio -> com.example.data.model.AestheticRank.ESMERALDA
      ratio >= com.example.data.model.AestheticRank.PLATINO.minRatio -> com.example.data.model.AestheticRank.PLATINO
      ratio >= com.example.data.model.AestheticRank.ORO.minRatio -> com.example.data.model.AestheticRank.ORO
      ratio >= com.example.data.model.AestheticRank.HIERRO.minRatio -> com.example.data.model.AestheticRank.HIERRO
      else -> com.example.data.model.AestheticRank.MADERA
    }
  }

  /**
   * Calcula el perfil simétrico y los rangos de cada grupo muscular (Hombros, Espalda, Pectoral, Brazos, Cintura, Piernas).
   * La jerarquía sigue: Madera -> Hierro -> Oro -> Platino -> Esmeralda -> Diamante -> Diamante Olímpico.
   */
  fun calculateAestheticProfile(
    sessions: List<WorkoutSession>,
    personalRecords: List<com.example.data.model.PersonalRecord>,
    userWeightKg: Float
  ): com.example.data.model.AestheticSymmetryProfile {
    val weight = if (userWeightKg > 35f) userWeightKg else 70f
    val allExercises = sessions.flatMap { it.exercises }

    val categoryRanks = AestheticMuscleCategory.values().map { category ->
      val prsForCategory = personalRecords.filter { it.category == category }
      val best1Rm = prsForCategory.maxOfOrNull { it.estimated1Rm }
        ?: allExercises.filter { it.category == category }.maxOfOrNull { it.bestEstimated1Rm }
        ?: 0f

      val primaryExName = prsForCategory.maxByOrNull { it.estimated1Rm }?.exerciseName
        ?: when (category) {
          AestheticMuscleCategory.V_TAPER_DELTS -> "Press Militar"
          AestheticMuscleCategory.V_TAPER_LATS -> "Dominadas / Jalón"
          AestheticMuscleCategory.CHEST_UPPER_LOWER -> "Press Inclinado"
          AestheticMuscleCategory.ARMS_BICEPS_TRICEPS -> "Curl / Fondos"
          AestheticMuscleCategory.CORE_WAIST -> "Elevación Piernas"
          AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS -> "Sentadilla Trasera"
        }

      // Factor de normalización por grupo muscular frente al peso corporal:
      // P.ej. Press Militar 0.8x peso es Oro; Sentadilla 1.5x peso es Platino/Esmeralda
      val normFactor = when (category) {
        AestheticMuscleCategory.V_TAPER_DELTS -> 1.35f // Hombros
        AestheticMuscleCategory.V_TAPER_LATS -> 1.05f  // Espalda/Dominadas
        AestheticMuscleCategory.CHEST_UPPER_LOWER -> 1.0f // Pectoral
        AestheticMuscleCategory.ARMS_BICEPS_TRICEPS -> 1.55f // Brazos
        AestheticMuscleCategory.CORE_WAIST -> 1.8f // Core
        AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS -> 0.75f // Piernas (cargas más altas naturalmente)
      }

      // Añadir contribución de volumen de entrenamiento acumulado
      val categoryVol = allExercises.filter { it.category == category }.sumOf { it.totalVolumeKg.toDouble() }.toFloat()
      val volumeBonus = (categoryVol / 6000f).coerceIn(0f, 0.45f)

      val rawRatio = if (best1Rm > 0f) (best1Rm / weight) * normFactor + volumeBonus else volumeBonus
      val currentRank = determineRankForRatio(rawRatio)

      val rankValues = com.example.data.model.AestheticRank.values()
      val nextRankIndex = currentRank.ordinal + 1
      val nextRank = if (nextRankIndex < rankValues.size) rankValues[nextRankIndex] else null

      val progress = if (nextRank != null) {
        val currentMin = currentRank.minRatio
        val nextMin = nextRank.minRatio
        ((rawRatio - currentMin) / (nextMin - currentMin)).coerceIn(0f, 1f)
      } else {
        1.0f
      }

      com.example.data.model.MuscleCategoryRank(
        category = category,
        currentRank = currentRank,
        relativeRatio = ((rawRatio * 10f).roundToInt() / 10f),
        nextRank = nextRank,
        progressToNext = progress,
        primaryExercise = primaryExName,
        best1Rm = best1Rm
      )
    }

    // Ratio V-Taper: Fuerza y volumen de Hombros + Espalda frente a Core/Cintura
    val deltsRatio = categoryRanks.firstOrNull { it.category == AestheticMuscleCategory.V_TAPER_DELTS }?.relativeRatio ?: 0.5f
    val latsRatio = categoryRanks.firstOrNull { it.category == AestheticMuscleCategory.V_TAPER_LATS }?.relativeRatio ?: 0.5f
    val vTaperScore = ((deltsRatio + latsRatio) / 2f * 10f).roundToInt() / 10f

    val averageScore = if (categoryRanks.isNotEmpty()) {
      categoryRanks.map { it.relativeRatio }.average().toFloat()
    } else 0f

    val globalRank = determineRankForRatio(averageScore)

    val dist = calculateSymmetryDistribution(allExercises)
    val lagging = dist.filter { it.isLagging }.map { it.category }
    val dominant = dist.filter { it.isDominant }.map { it.category }

    return com.example.data.model.AestheticSymmetryProfile(
      globalRank = globalRank,
      globalScore = ((averageScore * 10f).roundToInt() / 10f),
      vTaperRatio = vTaperScore,
      categoryRanks = categoryRanks,
      laggingCategories = lagging,
      dominantCategories = dominant
    )
  }

  /**
   * Genera recomendaciones de entrenamiento inteligente según el día de la semana y balance de simetría.
   */
  fun generateDailyRecommendation(
    dayName: String,
    profile: com.example.data.model.AestheticSymmetryProfile
  ): com.example.data.model.DailyWorkoutRecommendation {
    // Si hay un grupo muscular rezagado crítico para el V-Taper (Hombros o Espalda), sugerir compensación
    val isDeltsLagging = profile.laggingCategories.contains(AestheticMuscleCategory.V_TAPER_DELTS)
    val isLatsLagging = profile.laggingCategories.contains(AestheticMuscleCategory.V_TAPER_LATS)

    return when (dayName.lowercase()) {
      "lunes" -> {
        if (isDeltsLagging) {
          com.example.data.model.DailyWorkoutRecommendation(
            dayOfWeek = "Lunes",
            title = "Especialización V-Taper: Deltoides & Clavicular",
            primaryFocus = AestheticMuscleCategory.V_TAPER_DELTS,
            secondaryFocus = AestheticMuscleCategory.CHEST_UPPER_LOWER,
            rationale = "Tus hombros presentan un ratio rezagado frente al torso. Priorizar amplitud clavicular al inicio de semana maximiza la proporción V-Taper.",
            suggestedExercises = listOf(
              com.example.data.model.RecommendedExercise("Press Militar con Barra de Pie", 4, "6-8", 8.5f, "Base de fuerza y sobrecarga progresiva"),
              com.example.data.model.RecommendedExercise("Elevaciones Laterales con Mancuerna", 4, "12-15", 9.0f, "Pausa de 1s en la contracción de la cabeza lateral"),
              com.example.data.model.RecommendedExercise("Press Inclinado Mancuernas a 30°", 3, "8-10", 8.0f, "Enfoque en porción clavicular del pectoral"),
              com.example.data.model.RecommendedExercise("Pájaros en Polea Posterior", 3, "15", 8.5f, "Salud glenohumeral y deltoides posterior 3D")
            )
          )
        } else {
          com.example.data.model.DailyWorkoutRecommendation(
            dayOfWeek = "Lunes",
            title = "Torso Superior: Amplitud V-Taper",
            primaryFocus = AestheticMuscleCategory.V_TAPER_DELTS,
            secondaryFocus = AestheticMuscleCategory.CHEST_UPPER_LOWER,
            rationale = "Inicio de ciclo con enfoque en hombros 3D y pectoral superior para expandir la línea de los hombros.",
            suggestedExercises = listOf(
              com.example.data.model.RecommendedExercise("Press Militar con Mancuernas", 4, "8-10", 8.0f, "Control excéntrico de 3 segundos"),
              com.example.data.model.RecommendedExercise("Press de Banca Inclinado", 4, "6-8", 8.5f, "Hipertrofia clavicular pura"),
              com.example.data.model.RecommendedExercise("Elevaciones Laterales en Polea", 4, "12-15", 9.0f, "Tensión constante en el deltoides lateral"),
              com.example.data.model.RecommendedExercise("Fondos en Paralelas lastrados", 3, "8-10", 8.5f, "Grosor de pectoral y tríceps")
            )
          )
        }
      }
      "martes" -> {
        com.example.data.model.DailyWorkoutRecommendation(
          dayOfWeek = "Martes",
          title = "Espalda y Dorsales: Densidad y V-Taper",
          primaryFocus = AestheticMuscleCategory.V_TAPER_LATS,
          secondaryFocus = AestheticMuscleCategory.ARMS_BICEPS_TRICEPS,
          rationale = "Construye la silueta en 'V' ensanchando los dorsales superiores y agregando grosor al redondo mayor.",
          suggestedExercises = listOf(
            com.example.data.model.RecommendedExercise("Dominadas Pronadas / Lastradas", 4, "6-8", 8.5f, "Amplitud dorsal y expansión escapular"),
            com.example.data.model.RecommendedExercise("Remo con Barra Agarre Supino", 4, "8-10", 8.0f, "Densidad dorsal media y bíceps"),
            com.example.data.model.RecommendedExercise("Jalón al Pecho Agarre Neutro", 3, "10-12", 8.5f, "Aislamiento de la inserción baja del dorsal"),
            com.example.data.model.RecommendedExercise("Curl con Barra Z de Pie", 3, "10-12", 8.5f, "Pico de bíceps y braquial anterior")
          )
        )
      }
      "miércoles" -> {
        com.example.data.model.DailyWorkoutRecommendation(
          dayOfWeek = "Miércoles",
          title = "Pierna Clásica & Core Compacto",
          primaryFocus = AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS,
          secondaryFocus = AestheticMuscleCategory.CORE_WAIST,
          rationale = "Desarrolla el vasto lateral de los cuádriceps y mantiene la cintura compacta para maximizar la proporción cintura-hombros.",
          suggestedExercises = listOf(
            com.example.data.model.RecommendedExercise("Sentadilla Trasera en Barra", 4, "6-8", 8.5f, "Base estructural de fuerza y cuádriceps"),
            com.example.data.model.RecommendedExercise("Peso Muerto Rumano con Mancuernas", 4, "8-10", 8.0f, "Hipertrofia de isquiosurales y glúteos"),
            com.example.data.model.RecommendedExercise("Prensa de Piernas Inclinada", 3, "10-12", 8.5f, "Énfasis en barrido externo del cuádriceps"),
            com.example.data.model.RecommendedExercise("Elevación de Piernas Colgado + Vacío Abdominal", 3, "15", 8.0f, "Control del transverso abdominal")
          )
        )
      }
      "jueves" -> {
        com.example.data.model.DailyWorkoutRecommendation(
          dayOfWeek = "Jueves",
          title = "Torso Esculpido: Pectoral & Brazos 3D",
          primaryFocus = AestheticMuscleCategory.CHEST_UPPER_LOWER,
          secondaryFocus = AestheticMuscleCategory.ARMS_BICEPS_TRICEPS,
          rationale = "Frecuencia 2 de torso para optimizar la síntesis proteica en pectorales y volumen de brazos.",
          suggestedExercises = listOf(
            com.example.data.model.RecommendedExercise("Press de Banca Plano con Barra", 4, "6-8", 8.5f, "Fuerza básica y reclutamiento miofibrilar"),
            com.example.data.model.RecommendedExercise("Aperturas en Polea Inclinada", 3, "12-15", 9.0f, "Estiramiento bajo carga con máxima congestión"),
            com.example.data.model.RecommendedExercise("Press Francés con Barra Z", 4, "10-12", 8.5f, "Cabeza larga del tríceps"),
            com.example.data.model.RecommendedExercise("Curl Martillo con Mancuernas", 3, "10-12", 8.5f, "Braquial anterior para ensanchar el brazo")
          )
        )
      }
      "viernes" -> {
        com.example.data.model.DailyWorkoutRecommendation(
          dayOfWeek = "Viernes",
          title = "Simetría Estética Integral (V-Taper Booster)",
          primaryFocus = if (isDeltsLagging) AestheticMuscleCategory.V_TAPER_DELTS else AestheticMuscleCategory.V_TAPER_LATS,
          secondaryFocus = AestheticMuscleCategory.CORE_WAIST,
          rationale = "Sesión de afinación estética para ecualizar grupos rezagados antes del descanso de fin de semana.",
          suggestedExercises = listOf(
            com.example.data.model.RecommendedExercise("Elevaciones Laterales Pesadas + Drop Set", 4, "10+10", 9.5f, "Densidad en la cabeza medial del deltoides"),
            com.example.data.model.RecommendedExercise("Remo Gironda al Pecho", 4, "10-12", 8.5f, "Apertura escapular y retracción"),
            com.example.data.model.RecommendedExercise("Press Militar Sentado Mancuernas", 3, "8-10", 8.0f, "Sobrecarga estricta"),
            com.example.data.model.RecommendedExercise("Planchas Dinámicas & Rueda Abdominal", 3, "12-15", 8.0f, "Cintura dura y no ensanchada")
          )
        )
      }
      "sábado" -> {
        com.example.data.model.DailyWorkoutRecommendation(
          dayOfWeek = "Sábado",
          title = "Pierna Posterior, Gemelos y Movilidad",
          primaryFocus = AestheticMuscleCategory.LEGS_QUADS_HAMSTRINGS,
          secondaryFocus = null,
          rationale = "Énfasis en la cadena posterior y estética de las pantorrillas para una base simétrica sólida.",
          suggestedExercises = listOf(
            com.example.data.model.RecommendedExercise("Curl Femoral Tumbado / Sentado", 4, "10-12", 8.5f, "Aislamiento de isquiotibiales"),
            com.example.data.model.RecommendedExercise("Zancadas Búlgaras con Mancuernas", 3, "10/pierna", 9.0f, "Estabilidad pélvica y vasto medio"),
            com.example.data.model.RecommendedExercise("Elevación de Talones de Pie", 4, "15-20", 9.0f, "Desarrollo del gastrocnemio")
          )
        )
      }
      else -> {
        com.example.data.model.DailyWorkoutRecommendation(
          dayOfWeek = "Domingo",
          title = "Recuperación Activa y Vacío Abdominal",
          primaryFocus = AestheticMuscleCategory.CORE_WAIST,
          secondaryFocus = null,
          rationale = "Día de restauración neuromuscular, estiramientos de cadena posterior y control del diafragma.",
          suggestedExercises = listOf(
            com.example.data.model.RecommendedExercise("Vacío Abdominal (Stomach Vacuum)", 4, "20 segs", 7.0f, "Reducción de cintura y tonicidad del transverso"),
            com.example.data.model.RecommendedExercise("Estiramiento Pectoral en Marco de Puerta", 3, "45 segs", 5.0f, "Apertura postural y corrección de hombros caídos"),
            com.example.data.model.RecommendedExercise("Caminata Ligera al Aire Libre", 1, "40 min", 5.0f, "Circulación y desinflamación sistémica")
          )
        )
      }
    }
  }

  /**
   * Catálogo de guías visuales biomecánicas para la demostración gráfica de ejercicios.
   */
  fun getExerciseVisualGuides(): List<com.example.data.model.ExerciseVisualGuide> {
    return com.example.data.ExerciseDatabase.getAllExercises()
  }

  /**
   * Calcula el nivel y progreso de gamificación del usuario en el gimnasio.
   * El conteo inicia estrictamente desde 0 (Nivel 1, 0 XP) y otorga puntos
   * conforme el usuario completa y registra series y ejercicios reales.
   */
  fun calculateUserGymLevel(
    sessions: List<com.example.data.model.WorkoutSession>,
    prs: List<com.example.data.model.PersonalRecord>
  ): com.example.data.model.GymUserLevel {
    val totalSessions = sessions.size
    val allExercises = sessions.flatMap { it.exercises }
    val totalCompletedExercises = allExercises.size
    val allSets = allExercises.flatMap { it.sets }
    val completedSets = allSets.filter { it.completed }
    val totalCompletedSets = completedSets.size
    val totalVolume = sessions.sumOf { it.totalVolumeKg.toDouble() }.toFloat()

    // Sistema de XP:
    // +25 XP por serie efectiva completada
    // +50 XP por ejercicio registrado
    // +150 XP por sesión de entrenamiento completada
    // +100 XP por cada récord personal (PR) conseguido
    val xpFromSets = totalCompletedSets * 25
    val xpFromExercises = totalCompletedExercises * 50
    val xpFromSessions = totalSessions * 150
    val xpFromPrs = prs.size * 100
    val totalXp = xpFromSets + xpFromExercises + xpFromSessions + xpFromPrs

    // Escalafón de Niveles:
    // Nivel 1: 0 - 300 XP
    // Nivel 2: 300 - 750 XP (450 XP necesarios)
    // Nivel 3: 750 - 1,400 XP (650 XP necesarios)
    // Nivel 4: 1,400 - 2,300 XP (900 XP necesarios)
    // Nivel 5: 2,300 - 3,500 XP (1,200 XP necesarios)
    // Nivel 6: 3,500 - 5,000 XP (1,500 XP necesarios)
    // Nivel 7: 5,000 - 7,000 XP (2,000 XP necesarios)
    // Nivel 8+: 7,000+ XP
    val (level, title, badgeEmoji, baseLevelXp, nextLevelXp, nextTitle) = when {
      totalXp < 300 -> Tuple6(1, "Novato del Hierro", "🌱", 0, 300, "Iniciado del Hierro")
      totalXp < 750 -> Tuple6(2, "Iniciado del Hierro", "⚔️", 300, 750, "Guerrero Bronce")
      totalXp < 1400 -> Tuple6(3, "Guerrero Bronce", "🥉", 750, 1400, "Atleta Plata")
      totalXp < 2300 -> Tuple6(4, "Atleta Plata", "🥈", 1400, 2300, "Titán Dorado")
      totalXp < 3500 -> Tuple6(5, "Titán Dorado", "🥇", 2300, 3500, "Escultor de Platino")
      totalXp < 5000 -> Tuple6(6, "Escultor de Platino", "💎", 3500, 5000, "Semidiós Diamante")
      totalXp < 7000 -> Tuple6(7, "Semidiós Diamante", "⚡", 5000, 7000, "Leyenda del Olimpo")
      else -> Tuple6(8 + (totalXp - 7000) / 2500, "Leyenda del Olimpo", "👑", 7000, 9500, "Dios de la Fuerza")
    }

    val xpInLevel = totalXp - baseLevelXp
    val xpSpan = (nextLevelXp - baseLevelXp).coerceAtLeast(1)
    val fraction = (xpInLevel.toFloat() / xpSpan.toFloat()).coerceIn(0f, 1f)

    return com.example.data.model.GymUserLevel(
      level = level,
      title = title,
      badgeEmoji = badgeEmoji,
      currentXp = totalXp,
      xpInCurrentLevel = xpInLevel,
      xpRequiredForNextLevel = xpSpan,
      progressFraction = fraction,
      totalCompletedExercises = totalCompletedExercises,
      totalCompletedSets = totalCompletedSets,
      totalSessions = totalSessions,
      totalVolumeKg = totalVolume,
      nextUnlockTitle = nextTitle
    )
  }
}

private data class Tuple6<A, B, C, D, E, F>(
  val a: A, val b: B, val c: C, val d: D, val e: E, val f: F
)


