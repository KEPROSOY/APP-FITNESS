package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSession(session: WorkoutSessionEntity): Long

  @Update
  suspend fun updateSession(session: WorkoutSessionEntity)

  @Query("DELETE FROM workout_sessions WHERE id = :sessionId")
  suspend fun deleteSessionById(sessionId: Long)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertExercises(exercises: List<WorkoutExerciseEntity>): List<Long>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertExercise(exercise: WorkoutExerciseEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSets(sets: List<ExerciseSetEntity>): List<Long>

  @Transaction
  @Query("SELECT * FROM workout_sessions WHERE userId = :userId ORDER BY startTimeMillis DESC")
  fun getAllSessionsWithDetails(userId: Int = 1): Flow<List<WorkoutSessionWithExercises>>

  @Transaction
  @Query("SELECT * FROM workout_sessions WHERE userId = :userId AND dateIso = :dateIso ORDER BY startTimeMillis ASC")
  fun getSessionsByDate(dateIso: String, userId: Int = 1): Flow<List<WorkoutSessionWithExercises>>

  @Transaction
  @Query("SELECT * FROM workout_sessions WHERE userId = :userId AND dateIso BETWEEN :startDateIso AND :endDateIso ORDER BY dateIso DESC, startTimeMillis DESC")
  fun getSessionsBetweenDates(startDateIso: String, endDateIso: String, userId: Int = 1): Flow<List<WorkoutSessionWithExercises>>


  @Transaction
  @Query("SELECT * FROM workout_sessions WHERE id = :sessionId LIMIT 1")
  suspend fun getSessionById(sessionId: Long): WorkoutSessionWithExercises?

  @Query("SELECT COALESCE(SUM(estimatedCaloriesBurned), 0) FROM workout_sessions WHERE userId = :userId AND dateIso = :dateIso")
  fun getCaloriesBurnedByDate(dateIso: String, userId: Int = 1): Flow<Int>

  @Query("DELETE FROM workout_sessions WHERE userId = :userId")
  suspend fun clearAllSessions(userId: Int = 1)

  @Query("DELETE FROM workout_exercises WHERE sessionId = :sessionId")
  suspend fun deleteExercisesBySessionId(sessionId: Long)
}

@Dao
interface PersonalRecordDao {

  @Query("SELECT * FROM personal_records WHERE userId = :userId ORDER BY category ASC, exerciseName ASC")
  fun getAllPersonalRecords(userId: Int = 1): Flow<List<PersonalRecordEntity>>

  @Query("SELECT * FROM personal_records WHERE userId = :userId AND exerciseName = :exerciseName LIMIT 1")
  suspend fun getRecordForExercise(exerciseName: String, userId: Int = 1): PersonalRecordEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateRecord(record: PersonalRecordEntity)

  @Query("DELETE FROM personal_records WHERE id = :id")
  suspend fun deleteRecord(id: Long)

  @Query("DELETE FROM personal_records WHERE userId = :userId")
  suspend fun clearAllRecords(userId: Int = 1)
}
