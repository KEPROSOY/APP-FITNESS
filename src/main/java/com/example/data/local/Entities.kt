package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.MealIngredient
import com.example.data.model.MealType

@Entity(tableName = "user_profile")
data class UserProfileEntity(
  @PrimaryKey val id: Int = 1,
  val name: String,
  val age: Int,
  val gender: String,
  val heightCm: Float,
  val weightKg: Float,
  val targetWeightKg: Float,
  val goal: String,
  val activityLevel: String,
  val dailyCalories: Int,
  val proteinGoalGrams: Int,
  val carbsGoalGrams: Int,
  val fatGoalGrams: Int,
  val isMetric: Boolean,
  val isDarkTheme: Boolean,
  val followSystemTheme: Boolean = true,
  val notificationsEnabled: Boolean,
  val isOnboardingCompleted: Boolean,
  val avatarUri: String? = null,
  val email: String? = null,
  val isLoggedIn: Boolean = false,
  val authProvider: String = "EMAIL",
  val lastActiveDateIso: String? = null,
  val streakCount: Int = 0
)

@Entity(tableName = "meals")
data class MealEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val dateIso: String,
  val mealType: MealType,
  val timeFormatted: String,
  val totalCalories: Int,
  val totalProtein: Float,
  val totalCarbs: Float,
  val totalFat: Float,
  val totalSaturatedFat: Float = 0f,
  val totalTransFat: Float = 0f,
  val imageUrl: String? = null,
  val ingredients: List<MealIngredient>,
  val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "weight_entries")
data class WeightEntryEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val dateIso: String,
  val weightKg: Float,
  val timestamp: Long = System.currentTimeMillis()
)
