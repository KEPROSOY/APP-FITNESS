package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.MealEntity
import com.example.data.local.UserProfileEntity
import com.example.data.local.WeightEntryEntity
import com.example.data.model.ActivityLevel
import com.example.data.model.GoalType
import com.example.data.model.MealIngredient
import com.example.data.model.MealRecord
import com.example.data.model.MealType
import com.example.data.model.UserProfile
import com.example.data.model.WeightEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NutritionRepository(private val db: AppDatabase) {

  private val userProfileDao = db.userProfileDao()
  private val mealDao = db.mealDao()
  private val weightDao = db.weightDao()

  val userProfileFlow: Flow<UserProfile> = userProfileDao.getUserProfileFlow().map { entity ->
    if (entity != null) {
      UserProfile(
        id = entity.id,
        name = entity.name,
        age = entity.age,
        gender = entity.gender,
        heightCm = entity.heightCm,
        weightKg = entity.weightKg,
        targetWeightKg = entity.targetWeightKg,
        goal = GoalType.entries.find { it.name == entity.goal } ?: GoalType.PERDER_PESO,
        activityLevel = ActivityLevel.entries.find { it.name == entity.activityLevel } ?: ActivityLevel.MODERADO,
        dailyCalories = entity.dailyCalories,
        proteinGoalGrams = entity.proteinGoalGrams,
        carbsGoalGrams = entity.carbsGoalGrams,
        fatGoalGrams = entity.fatGoalGrams,
        isMetric = entity.isMetric,
        isDarkTheme = entity.isDarkTheme,
        followSystemTheme = entity.followSystemTheme,
        notificationsEnabled = entity.notificationsEnabled,
        isOnboardingCompleted = entity.isOnboardingCompleted,
        avatarUri = entity.avatarUri,
        email = entity.email,
        isLoggedIn = entity.isLoggedIn,
        authProvider = entity.authProvider,
        lastActiveDateIso = entity.lastActiveDateIso,
        streakCount = entity.streakCount
      )
    } else {
      // Default initial profile
      UserProfile()
    }
  }

  suspend fun saveUserProfile(profile: UserProfile) = withContext(Dispatchers.IO) {
    userProfileDao.insertOrUpdate(
      UserProfileEntity(
        id = 1,
        name = profile.name,
        age = profile.age,
        gender = profile.gender,
        heightCm = profile.heightCm,
        weightKg = profile.weightKg,
        targetWeightKg = profile.targetWeightKg,
        goal = profile.goal.name,
        activityLevel = profile.activityLevel.name,
        dailyCalories = profile.dailyCalories,
        proteinGoalGrams = profile.proteinGoalGrams,
        carbsGoalGrams = profile.carbsGoalGrams,
        fatGoalGrams = profile.fatGoalGrams,
        isMetric = profile.isMetric,
        isDarkTheme = profile.isDarkTheme,
        followSystemTheme = profile.followSystemTheme,
        notificationsEnabled = profile.notificationsEnabled,
        isOnboardingCompleted = profile.isOnboardingCompleted,
        avatarUri = profile.avatarUri,
        email = profile.email,
        isLoggedIn = profile.isLoggedIn,
        authProvider = profile.authProvider,
        lastActiveDateIso = profile.lastActiveDateIso,
        streakCount = profile.streakCount
      )
    )
  }

  fun getMealsForDate(dateIso: String): Flow<List<MealRecord>> {
    return mealDao.getMealsByDate(dateIso).map { entities ->
      entities.map { entity ->
        MealRecord(
          id = entity.id,
          dateIso = entity.dateIso,
          mealType = entity.mealType,
          timeFormatted = entity.timeFormatted,
          totalCalories = entity.totalCalories,
          totalProtein = entity.totalProtein,
          totalCarbs = entity.totalCarbs,
          totalFat = entity.totalFat,
          totalSaturatedFat = entity.totalSaturatedFat,
          totalTransFat = entity.totalTransFat,
          imageUrl = entity.imageUrl,
          ingredients = entity.ingredients,
          timestamp = entity.timestamp
        )
      }
    }
  }

  suspend fun insertMeal(meal: MealRecord): Long = withContext(Dispatchers.IO) {
    mealDao.insertMeal(
      MealEntity(
        id = meal.id,
        dateIso = meal.dateIso,
        mealType = meal.mealType,
        timeFormatted = meal.timeFormatted,
        totalCalories = meal.totalCalories,
        totalProtein = meal.totalProtein,
        totalCarbs = meal.totalCarbs,
        totalFat = meal.totalFat,
        totalSaturatedFat = meal.totalSaturatedFat,
        totalTransFat = meal.totalTransFat,
        imageUrl = meal.imageUrl,
        ingredients = meal.ingredients,
        timestamp = meal.timestamp
      )
    )
  }

  suspend fun deleteMeal(mealId: Long) = withContext(Dispatchers.IO) {
    mealDao.deleteMealById(mealId)
  }

  /**
   * Automatically purge meals older than 24 hours (1 day) to minimize device resource consumption
   */
  suspend fun cleanupOldMeals(): Int = withContext(Dispatchers.IO) {
    val oneDayAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000L)
    mealDao.deleteMealsOlderThan(oneDayAgo)
  }

  val weightsFlow: Flow<List<WeightEntry>> = weightDao.getAllWeights().map { entities ->
    entities.map { entity ->
      WeightEntry(
        id = entity.id,
        dateIso = entity.dateIso,
        weightKg = entity.weightKg,
        timestamp = entity.timestamp
      )
    }
  }

  suspend fun insertWeight(weightKg: Float, dateIso: String): Long = withContext(Dispatchers.IO) {
    weightDao.insertWeight(
      WeightEntryEntity(
        dateIso = dateIso,
        weightKg = weightKg
      )
    )
  }

  suspend fun resetAllData() = withContext(Dispatchers.IO) {
    userProfileDao.clearProfile()
    mealDao.clearAllMeals()
    weightDao.clearAllWeights()
  }

  // Populate starter data if database is brand new
  fun checkAndSeedInitialData(scope: CoroutineScope) {
    scope.launch(Dispatchers.IO) {
      val existingProfile = userProfileDao.getUserProfile()
      val todayIso = getTodayIso()
      if (existingProfile == null) {
        // Default initial profile
        val defaultProfile = UserProfile(
          id = 1,
          name = "Alex",
          age = 26,
          gender = "Masculino",
          heightCm = 175f,
          weightKg = 68.5f,
          targetWeightKg = 65.0f,
          goal = GoalType.PERDER_PESO,
          activityLevel = ActivityLevel.MODERADO,
          dailyCalories = 2200,
          proteinGoalGrams = 140,
          carbsGoalGrams = 240,
          fatGoalGrams = 65,
          isMetric = true,
          isDarkTheme = false,
          followSystemTheme = true,
          notificationsEnabled = true,
          isOnboardingCompleted = true, // Ready for immediate exploration
          isLoggedIn = true // Keeps session active after closing app
        )
        saveUserProfile(defaultProfile)

        // Starting weight entry from day 0 (no phantom previous history)
        val baseTime = System.currentTimeMillis()
        weightDao.insertWeight(WeightEntryEntity(dateIso = todayIso, weightKg = defaultProfile.weightKg, timestamp = baseTime))

        // Seed today's sample meals: Desayuno, Comida, Snack (exactly matching prompt values or close!)
        // Desayuno: 420 kcal
        val breakfastIngredients = listOf(
          MealIngredient(foodName = "Avena con leche desnatada", grams = 60f, calories = 230, protein = 10f, carbs = 40f, fat = 3f, saturatedFat = 0.8f, transFat = 0f, emoji = "🥣"),
          MealIngredient(foodName = "Plátano maduro", grams = 120f, calories = 105, protein = 1.3f, carbs = 27f, fat = 0.3f, saturatedFat = 0.1f, transFat = 0f, emoji = "🍌"),
          MealIngredient(foodName = "Almendras tostadas", grams = 15f, calories = 85, protein = 3.2f, carbs = 3.2f, fat = 7.5f, saturatedFat = 0.6f, transFat = 0f, emoji = "🥜")
        )
        mealDao.insertMeal(
          MealEntity(
            dateIso = todayIso,
            mealType = MealType.DESAYUNO,
            timeFormatted = "08:30 AM",
            totalCalories = 420,
            totalProtein = 14.5f,
            totalCarbs = 70.2f,
            totalFat = 10.8f,
            totalSaturatedFat = 1.5f,
            totalTransFat = 0.0f,
            ingredients = breakfastIngredients
          )
        )

        // Comida: 650 kcal
        val lunchIngredients = listOf(
          MealIngredient(foodName = "Pechuga de pollo a la plancha", grams = 180f, calories = 297, protein = 55.8f, carbs = 0f, fat = 6.5f, saturatedFat = 1.8f, transFat = 0f, emoji = "🍗"),
          MealIngredient(foodName = "Arroz integral cocido", grams = 180f, calories = 200, protein = 4.7f, carbs = 41.4f, fat = 1.6f, saturatedFat = 0.3f, transFat = 0f, emoji = "🍚"),
          MealIngredient(foodName = "Aguacate fresco", grams = 50f, calories = 80, protein = 1f, carbs = 4.2f, fat = 7.4f, saturatedFat = 1.0f, transFat = 0f, emoji = "🥑"),
          MealIngredient(foodName = "Ensalada verde con aceite", grams = 120f, calories = 73, protein = 1.8f, carbs = 3.5f, fat = 6.0f, saturatedFat = 0.9f, transFat = 0f, emoji = "🥗")
        )
        mealDao.insertMeal(
          MealEntity(
            dateIso = todayIso,
            mealType = MealType.COMIDA,
            timeFormatted = "02:15 PM",
            totalCalories = 650,
            totalProtein = 63.3f,
            totalCarbs = 49.1f,
            totalFat = 21.5f,
            totalSaturatedFat = 4.0f,
            totalTransFat = 0.0f,
            ingredients = lunchIngredients
          )
        )

        // Snack: 88 kcal
        val snackIngredients = listOf(
          MealIngredient(foodName = "Manzana fuji y canela", grams = 170f, calories = 88, protein = 0.5f, carbs = 23.5f, fat = 0.3f, saturatedFat = 0f, transFat = 0f, emoji = "🍎")
        )
        mealDao.insertMeal(
          MealEntity(
            dateIso = todayIso,
            mealType = MealType.SNACK,
            timeFormatted = "05:30 PM",
            totalCalories = 88,
            totalProtein = 0.5f,
            totalCarbs = 23.5f,
            totalFat = 0.3f,
            totalSaturatedFat = 0.0f,
            totalTransFat = 0.0f,
            ingredients = snackIngredients
          )
        )
      }
    }
  }

  companion object {
    fun getTodayIso(): String {
      return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    /**
     * Calculates baseline nutritional requirements using Mifflin-St Jeor formula:
     * Men: BMR = (10 * weight in kg) + (6.25 * height in cm) - (5 * age) + 5
     * Women: BMR = (10 * weight in kg) + (6.25 * height in cm) - (5 * age) - 161
     */
    fun calculateNutrition(
      age: Int,
      gender: String,
      heightCm: Float,
      weightKg: Float,
      activityLevel: ActivityLevel,
      goal: GoalType
    ): NutritionCalculationResult {
      val base = (10 * weightKg) + (6.25f * heightCm) - (5 * age)
      val bmr = if (gender.equals("Femenino", ignoreCase = true)) {
        base - 161
      } else {
        base + 5
      }

      val tdee = bmr * activityLevel.multiplier

      val targetCalories = when (goal) {
        GoalType.PERDER_PESO -> (tdee - 450f).toInt().coerceAtLeast(1200)
        GoalType.MANTENER_PESO -> tdee.toInt()
        GoalType.GANAR_PESO -> (tdee + 350f).toInt()
      }

      // Macro splits based on fitness standard:
      // Protein: ~2.0g per kg of weight (or 25-30% of total cals)
      val proteinGrams = (weightKg * 2.0f).toInt().coerceIn(70, 220)
      // Fat: ~25-30% of total calories (9 cals per g)
      val fatGrams = ((targetCalories * 0.25f) / 9f).toInt().coerceIn(40, 110)
      // Carbs: remainder of calories (4 cals per g)
      val proteinCals = proteinGrams * 4
      val fatCals = fatGrams * 9
      val carbCals = (targetCalories - proteinCals - fatCals).coerceAtLeast(400)
      val carbsGrams = (carbCals / 4)

      return NutritionCalculationResult(
        dailyCalories = targetCalories,
        proteinGrams = proteinGrams,
        carbsGrams = carbsGrams,
        fatGrams = fatGrams
      )
    }
  }
}

data class NutritionCalculationResult(
  val dailyCalories: Int,
  val proteinGrams: Int,
  val carbsGrams: Int,
  val fatGrams: Int
)
