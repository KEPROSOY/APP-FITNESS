package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
  @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
  fun getUserProfileFlow(): Flow<UserProfileEntity?>

  @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
  suspend fun getUserProfile(): UserProfileEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(profile: UserProfileEntity)

  @Query("DELETE FROM user_profile")
  suspend fun clearProfile()
}

@Dao
interface MealDao {
  @Query("SELECT * FROM meals WHERE dateIso = :dateIso ORDER BY timestamp ASC")
  fun getMealsByDate(dateIso: String): Flow<List<MealEntity>>

  @Query("SELECT * FROM meals ORDER BY timestamp DESC LIMIT 50")
  fun getAllRecentMeals(): Flow<List<MealEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMeal(meal: MealEntity): Long

  @Update
  suspend fun updateMeal(meal: MealEntity)

  @Query("DELETE FROM meals WHERE id = :mealId")
  suspend fun deleteMealById(mealId: Long)

  @Query("DELETE FROM meals WHERE timestamp < :cutoffTimestamp")
  suspend fun deleteMealsOlderThan(cutoffTimestamp: Long): Int

  @Query("DELETE FROM meals")
  suspend fun clearAllMeals()
}

@Dao
interface WeightDao {
  @Query("SELECT * FROM weight_entries ORDER BY timestamp ASC")
  fun getAllWeights(): Flow<List<WeightEntryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertWeight(entry: WeightEntryEntity): Long

  @Query("DELETE FROM weight_entries WHERE id = :id")
  suspend fun deleteWeightById(id: Long)

  @Query("DELETE FROM weight_entries")
  suspend fun clearAllWeights()
}
