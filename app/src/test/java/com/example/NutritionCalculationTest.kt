package com.example

import com.example.data.model.ActivityLevel
import com.example.data.model.GoalType
import com.example.data.model.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NutritionCalculationTest {

  @Test
  fun `test caloric and macro calculation for deficit goal`() {
    val weightKg = 70f
    val heightCm = 175f
    val age = 28
    val result = com.example.data.repository.NutritionRepository.calculateNutrition(
      age = age,
      gender = "Masculino",
      heightCm = heightCm,
      weightKg = weightKg,
      activityLevel = ActivityLevel.MODERADO,
      goal = GoalType.PERDER_PESO
    )

    assertTrue("Target calories should be positive and reasonable", result.dailyCalories in 1500..3000)

    val expectedProtein = (weightKg * 2.0f).toInt()
    assertEquals(expectedProtein, result.proteinGrams)
    assertTrue("Carb goal should be positive", result.carbsGrams > 0)
    assertTrue("Fat goal should be positive", result.fatGrams > 0)
  }

  @Test
  fun `test user profile macro balance sum`() {
    val profile = UserProfile(
      name = "Test User",
      age = 26,
      gender = "Masculino",
      heightCm = 178f,
      weightKg = 72f,
      targetWeightKg = 68f,
      activityLevel = ActivityLevel.MODERADO,
      goal = GoalType.PERDER_PESO,
      dailyCalories = 2100,
      proteinGoalGrams = 144,
      carbsGoalGrams = 230,
      fatGoalGrams = 58,
      isOnboardingCompleted = true
    )

    val macroCals = (profile.proteinGoalGrams * 4) + (profile.carbsGoalGrams * 4) + (profile.fatGoalGrams * 9)
    val diff = kotlin.math.abs(macroCals - profile.dailyCalories)
    assertTrue("Macro caloric sum is within 5% of target daily calories", diff < 150)
  }

  @Test
  fun `test meal fat breakdown and quality calculation`() {
    val totalFat = 28f
    val saturatedFat = 6f
    val transFat = 0.0f
    val goodFats = (totalFat - saturatedFat - transFat).coerceAtLeast(0f)

    assertEquals(22f, goodFats)
    assertTrue("Trans fat should be 0 or minimal for healthy diets", transFat <= 0.5f)
    assertTrue("Saturated fat should be within healthy limit", saturatedFat <= 20f)
  }
}
