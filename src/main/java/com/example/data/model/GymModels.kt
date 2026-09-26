package com.example.data.model

/**
 * Categorías musculares y estéticas (inspiradas en balance de simetría y V-Taper).
 */
enum class AestheticMuscleCategory(
  val displayName: String,
  val targetRatioPercent: Float, // Porcentaje de volumen recomendado para proporción estética
  val iconEmoji: String,
  val description: String
) {
  V_TAPER_DELTS("Hombros (Deltoides)", 0.20f, "🛡️", "Clave para la amplitud clavicular y proporción V-Taper"),
  V_TAPER_LATS("Espalda (Dorsales)", 0.22f, "🦅", "Amplitud y densidad posterior para la silueta estética"),
  CHEST_UPPER_LOWER("Pectoral", 0.18f, "🛡️", "Pectoral superior y grosor para plenitud torácica"),
  ARMS_BICEPS_TRICEPS("Brazos (Bíceps/Tríceps)", 0.16f, "💪", "Pico de bíceps y cabeza lateral del tríceps"),
  CORE_WAIST("Cintura y Abdomen", 0.08f, "⚡", "Core compacto para acentuar el radio cintura-hombros"),
  LEGS_QUADS_HAMSTRINGS("Piernas y Glúteos", 0.16f, "🦵", "Base sólida, vasto externo y definición femorales")
}

enum class SetType(val label: String) {
  NORMAL("Normal"),
  WARMUP("Calentamiento"),
  DROPSET("Drop Set"),
  FAILURE("Al fallo")
}

data class ExerciseSet(
  val id: Long = 0,
  val setNumber: Int,
  val weightKg: Float,
  val reps: Int,
  val rpe: Float? = null, // Rate of Perceived Exertion (1 a 10)
  val setType: SetType = SetType.NORMAL,
  val completed: Boolean = true,
  val estimated1Rm: Float = 0f
)

data class WorkoutExercise(
  val id: Long = 0,
  val exerciseName: String,
  val category: AestheticMuscleCategory,
  val sets: List<ExerciseSet> = emptyList(),
  val notes: String = ""
) {
  val totalVolumeKg: Float
    get() = sets.filter { it.completed }.sumOf { (it.weightKg * it.reps).toDouble() }.toFloat()

  val completedSetsCount: Int
    get() = sets.count { it.completed }

  val bestEstimated1Rm: Float
    get() = sets.maxOfOrNull { it.estimated1Rm } ?: 0f
}

data class WorkoutSession(
  val id: Long = 0,
  val userId: Int = 1,
  val dateIso: String, // YYYY-MM-DD
  val title: String,
  val startTimeMillis: Long = System.currentTimeMillis(),
  val durationMinutes: Int = 60,
  val exercises: List<WorkoutExercise> = emptyList(),
  val estimatedCaloriesBurned: Int = 0,
  val notes: String = ""
) {
  val totalVolumeKg: Float
    get() = exercises.sumOf { it.totalVolumeKg.toDouble() }.toFloat()

  val totalCompletedSets: Int
    get() = exercises.sumOf { ex -> ex.sets.count { it.completed } }
}

data class PersonalRecord(
  val id: Long = 0,
  val userId: Int = 1,
  val exerciseName: String,
  val category: AestheticMuscleCategory,
  val bestWeightKg: Float,
  val bestReps: Int,
  val estimated1Rm: Float,
  val achievedDateIso: String
)

data class AestheticVolumeDistribution(
  val category: AestheticMuscleCategory,
  val actualVolumeKg: Float,
  val percentageOfTotal: Float,
  val isLagging: Boolean,
  val isDominant: Boolean
)

/**
 * Representa el nivel de progreso y gamificación del usuario en el gimnasio.
 * El cómputo inicia desde 0 (Nivel 1, 0 XP, 0 series) y se incrementa en tiempo real
 * conforme el usuario registra y completa ejercicios y series.
 */
data class GymUserLevel(
  val level: Int = 1,
  val title: String = "Novato del Hierro",
  val badgeEmoji: String = "🌱",
  val currentXp: Int = 0,
  val xpInCurrentLevel: Int = 0,
  val xpRequiredForNextLevel: Int = 300,
  val progressFraction: Float = 0f,
  val totalCompletedExercises: Int = 0,
  val totalCompletedSets: Int = 0,
  val totalSessions: Int = 0,
  val totalVolumeKg: Float = 0f,
  val nextUnlockTitle: String = "Iniciado del Hierro"
)

data class DailyGymSummary(
  val dateIso: String,
  val sessionsCount: Int,
  val totalMinutes: Int,
  val totalVolumeKg: Float,
  val totalCaloriesBurned: Int,
  val sessions: List<WorkoutSession>
)

/**
 * Jerarquía de rangos estéticos:
 * Madera -> Hierro -> Oro -> Platino -> Esmeralda -> Diamante -> Diamante Olímpico
 */
enum class AestheticRank(
  val displayName: String,
  val symbol: String,
  val minRatio: Float,
  val colorHex: Long,
  val description: String
) {
  MADERA("Madera", "🪵", 0.0f, 0xFF8D6E63, "Iniciando adaptación anatómica"),
  HIERRO("Hierro", "⛓️", 0.6f, 0xFF78909C, "Cimientos de fuerza y técnica básica"),
  ORO("Oro", "🥇", 0.9f, 0xFFFFD700, "Desarrollo hipertrófico y simetría visible"),
  PLATINO("Platino", "🛡️", 1.2f, 0xFF00E5FF, "V-Taper pronunciado y densidad muscular"),
  ESMERALDA("Esmeralda", "❇️", 1.5f, 0xFF00E676, "Fuerza relativa élite y corte estético"),
  DIAMANTE("Diamante", "💎", 1.8f, 0xFF38BDF8, "Físico clásico esculpido y máxima definición"),
  DIAMANTE_OLIMPICO("Diamante Olímpico", "👑", 2.1f, 0xFFFFB300, "Proporción áurea y desarrollo de tarima")
}

data class MuscleCategoryRank(
  val category: AestheticMuscleCategory,
  val currentRank: AestheticRank,
  val relativeRatio: Float,
  val nextRank: AestheticRank?,
  val progressToNext: Float, // 0f a 1f
  val primaryExercise: String,
  val best1Rm: Float
)

data class AestheticSymmetryProfile(
  val globalRank: AestheticRank,
  val globalScore: Float,
  val vTaperRatio: Float,
  val categoryRanks: List<MuscleCategoryRank>,
  val laggingCategories: List<AestheticMuscleCategory>,
  val dominantCategories: List<AestheticMuscleCategory>
)

data class RecommendedExercise(
  val name: String,
  val sets: Int,
  val repsRange: String,
  val targetRpe: Float,
  val focusNote: String
)

data class DailyWorkoutRecommendation(
  val dayOfWeek: String,
  val title: String,
  val primaryFocus: AestheticMuscleCategory,
  val secondaryFocus: AestheticMuscleCategory?,
  val rationale: String,
  val suggestedExercises: List<RecommendedExercise>
)

data class ExerciseVisualGuide(
  val id: String,
  val exerciseName: String,
  val category: AestheticMuscleCategory,
  val equipment: String,
  val tempo: String,
  val primaryMuscles: List<String>,
  val secondaryMuscles: List<String>,
  val keyPoints: List<String>,
  val breathingCue: String,
  val visualCues: List<String>,
  val targetMuscleGroup: String = "Pecho",
  val animationType: String = "BENCH_PRESS"
)

data class ExerciseHistoryPoint(
  val dateIso: String,
  val weightKg: Float,
  val reps: Int,
  val estimated1Rm: Float,
  val volumeKg: Float,
  val isPr: Boolean = false
)

