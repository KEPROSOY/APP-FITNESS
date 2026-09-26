package com.example.data.model

enum class GoalType(val label: String, val description: String) {
  PERDER_PESO("Perder peso", "Déficit calórico moderado para quemar grasa de forma sostenible"),
  MANTENER_PESO("Mantener peso", "Balance energético para mantener tu composición actual"),
  GANAR_PESO("Ganar masa", "Superávit calórico optimizado para ganar masa muscular")
}

enum class ActivityLevel(val label: String, val multiplier: Float) {
  SEDENTARIO("Sedentario (poco o ningún ejercicio)", 1.2f),
  LIGERO("Ligero (ejercicio 1-3 días/semana)", 1.375f),
  MODERADO("Moderado (ejercicio 3-5 días/semana)", 1.55f),
  ACTIVO("Muy activo (ejercicio intenso 6-7 días)", 1.725f)
}

enum class MealType(val displayName: String, val defaultTime: String, val emoji: String) {
  DESAYUNO("Desayuno", "08:30 AM", "🍳"),
  COMIDA("Comida", "02:00 PM", "🥗"),
  CENA("Cena", "08:30 PM", "🍲"),
  SNACK("Snack", "05:00 PM", "🍎")
}

data class UserProfile(
  val id: Int = 1,
  val name: String = "Alex",
  val age: Int = 26,
  val gender: String = "Masculino", // "Masculino", "Femenino", "Otro"
  val heightCm: Float = 175f,
  val weightKg: Float = 68.5f,
  val targetWeightKg: Float = 65.0f,
  val goal: GoalType = GoalType.PERDER_PESO,
  val activityLevel: ActivityLevel = ActivityLevel.MODERADO,
  val dailyCalories: Int = 2200,
  val proteinGoalGrams: Int = 140,
  val carbsGoalGrams: Int = 240,
  val fatGoalGrams: Int = 65,
  val isMetric: Boolean = true,
  val isDarkTheme: Boolean = false,
  val followSystemTheme: Boolean = true,
  val notificationsEnabled: Boolean = true,
  val isOnboardingCompleted: Boolean = false,
  val avatarUri: String? = null,
  val email: String? = null,
  val isLoggedIn: Boolean = false,
  val authProvider: String = "EMAIL", // "GOOGLE", "EMAIL", "INVITADO"
  val lastActiveDateIso: String? = null,
  val streakCount: Int = 0
)

data class BmiResult(
  val bmi: Float,
  val category: String, // "Bajo peso", "Peso normal / Saludable", "Sobrepeso", "Obesidad"
  val detail: String,
  val colorHex: Long,
  val healthAdvice: String
)

fun calculateBmi(weightKg: Float, heightCm: Float): BmiResult {
  if (heightCm <= 0f || weightKg <= 0f) {
    return BmiResult(
      bmi = 0f,
      category = "Sin datos",
      detail = "Ingresa tu estatura y peso",
      colorHex = 0xFF94A3B8,
      healthAdvice = "Ingresa tu información corporal para calcular tu IMC."
    )
  }
  val heightM = heightCm / 100f
  val bmi = weightKg / (heightM * heightM)
  val rounded = (Math.round(bmi * 10f)) / 10f

  return when {
    rounded < 18.5f -> BmiResult(
      bmi = rounded,
      category = "Bajo peso (Delgado/a)",
      detail = "Tu IMC está por debajo del rango recomendado por la OMS.",
      colorHex = 0xFF38BDF8, // Azul suave
      healthAdvice = "Te conviene un ligero superávit calórico con proteínas limpias y carbohidratos complejos para ganar tono y masa muscular saludable."
    )
    rounded < 25.0f -> BmiResult(
      bmi = rounded,
      category = "Peso saludable (Normal)",
      detail = "¡Excelente! Te encuentras en tu peso óptimo según la OMS.",
      colorHex = 0xFF10B981, // Verde esmeralda
      healthAdvice = "¡Gran trabajo! Mantén tus niveles con una dieta balanceada rica en grasas saludables (aguacate, aceite de oliva virgen) y ejercicio regular."
    )
    rounded < 30.0f -> BmiResult(
      bmi = rounded,
      category = "Sobrepeso",
      detail = "Tu IMC supera ligeramente el rango ideal de masa corporal.",
      colorHex = 0xFFF59E0B, // Ámbar
      healthAdvice = "Un déficit calórico moderado de 300-400 kcal al día y limitar ultraprocesados con grasas saturadas te ayudará a perder grasa de forma progresiva."
    )
    else -> BmiResult(
      bmi = rounded,
      category = "Obesidad",
      detail = "Tu peso se sitúa en un nivel de riesgo metabólico y cardiovascular.",
      colorHex = 0xFFEF4444, // Rojo alerta
      healthAdvice = "Prioriza alimentos naturales no procesados, cero grasas trans, abundante agua y caminatas diarias. Considera supervisión médica especializada."
    )
  }
}

data class Recipe(
  val id: String,
  val title: String,
  val description: String,
  val category: String, // "Fitness", "Desayunos", "Almuerzos", "Cenas Rápidas", "Snacks"
  val mealType: MealType,
  val ingredients: List<MealIngredient>,
  val emoji: String = "🍲"
) {
  val totalCalories: Int get() = ingredients.sumOf { it.calories }
  val totalProtein: Float get() = ingredients.sumOf { it.protein.toDouble() }.toFloat()
  val totalCarbs: Float get() = ingredients.sumOf { it.carbs.toDouble() }.toFloat()
  val totalFat: Float get() = ingredients.sumOf { it.fat.toDouble() }.toFloat()
  val totalSaturatedFat: Float get() = ingredients.sumOf { it.saturatedFat.toDouble() }.toFloat()
  val totalTransFat: Float get() = ingredients.sumOf { it.transFat.toDouble() }.toFloat()
}

data class FoodItem(
  val id: String,
  val name: String,
  val category: String, // "Proteínas", "Carbohidratos", "Grasas", "Grasas Malas / Trans"
  val caloriesPer100g: Int,
  val proteinPer100g: Float,
  val carbsPer100g: Float,
  val fatPer100g: Float,
  val saturatedFatPer100g: Float = 0f, // Grasas Saturadas (Malas)
  val transFatPer100g: Float = 0f,     // Grasas Trans (Peligro Cardiovascular)
  val defaultServingGrams: Float = 100f,
  val servingUnit: String = "g",
  val emoji: String = "🍽️"
) {
  val isTransFat: Boolean get() = transFatPer100g > 0f
  val isBadFat: Boolean get() = saturatedFatPer100g >= 5f || isTransFat
}

data class MealIngredient(
  val id: String = java.util.UUID.randomUUID().toString(),
  val foodName: String,
  val grams: Float,
  val calories: Int,
  val protein: Float,
  val carbs: Float,
  val fat: Float,
  val saturatedFat: Float = 0f,
  val transFat: Float = 0f,
  val emoji: String = "🥗"
)

data class MealRecord(
  val id: Long = 0,
  val dateIso: String, // YYYY-MM-DD
  val mealType: MealType,
  val timeFormatted: String,
  val totalCalories: Int,
  val totalProtein: Float,
  val totalCarbs: Float,
  val totalFat: Float,
  val totalSaturatedFat: Float = 0f,
  val totalTransFat: Float = 0f,
  val imageUrl: String? = null,
  val ingredients: List<MealIngredient> = emptyList(),
  val timestamp: Long = System.currentTimeMillis()
)

data class WeightEntry(
  val id: Long = 0,
  val dateIso: String,
  val weightKg: Float,
  val timestamp: Long = System.currentTimeMillis()
)

data class ChatMessage(
  val id: String = java.util.UUID.randomUUID().toString(),
  val text: String,
  val isUser: Boolean,
  val timestamp: Long = System.currentTimeMillis()
)

data class DailySummary(
  val dateIso: String = "",
  val totalCaloriesConsumed: Int = 0,
  val targetCalories: Int = 2000,
  val totalProtein: Float = 0f,
  val targetProtein: Int = 150,
  val totalCarbs: Float = 0f,
  val targetCarbs: Int = 200,
  val totalFat: Float = 0f,
  val targetFat: Int = 65,
  val totalSaturatedFat: Float = 0f,
  val totalTransFat: Float = 0f,
  val meals: List<MealRecord> = emptyList(),
  val workoutCaloriesBurned: Int = 0
) {
  val caloriesRemaining: Int
    get() = (targetCalories - totalCaloriesConsumed).coerceAtLeast(0)

  val netCalories: Int
    get() = (totalCaloriesConsumed - workoutCaloriesBurned).coerceAtLeast(0)

  val effectiveCaloriesRemaining: Int
    get() = (targetCalories + workoutCaloriesBurned - totalCaloriesConsumed).coerceAtLeast(0)

  val calorieProgress: Float
    get() = if (targetCalories > 0) (totalCaloriesConsumed.toFloat() / targetCalories).coerceIn(0f, 1f) else 0f

  val targetSaturatedFatMax: Int = 20 // Máximo recomendado OMS (<20g/día)
  val targetTransFatMax: Float = 0.5f  // Máximo OMS (Ideal 0.0g)

  val healthyFat: Float
    get() = (totalFat - totalSaturatedFat - totalTransFat).coerceAtLeast(0f)
}

data class SymmetryExercise(
  val name: String,
  val sets: Int,
  val reps: String
)

data class SymmetryRoutineDay(
  val day: String,
  val exercises: List<SymmetryExercise>
)

data class SymmetryResult(
  val strengths: List<String>,
  val weaknesses: List<String>,
  val routine: List<SymmetryRoutineDay>,
  val advice: String
)

