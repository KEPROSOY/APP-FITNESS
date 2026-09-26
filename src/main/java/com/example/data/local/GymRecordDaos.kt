package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUser(user: User): Long

  @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
  suspend fun getUserById(userId: Long = 1): User?

  @Query("SELECT * FROM users LIMIT 1")
  fun getCurrentUserFlow(): Flow<User?>
}

@Dao
interface WorkoutSessionRecordDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSession(session: WorkoutSession): Long

  @Update
  suspend fun updateSession(session: WorkoutSession)

  @Query("DELETE FROM workout_session_history WHERE id = :sessionId")
  suspend fun deleteSession(sessionId: Long)

  @Query("SELECT * FROM workout_session_history WHERE userId = :userId ORDER BY dateIso DESC, createdAt DESC")
  fun getSessionsForUser(userId: Long = 1): Flow<List<WorkoutSession>>

  @Query("SELECT * FROM workout_session_history WHERE id = :sessionId LIMIT 1")
  suspend fun getSessionById(sessionId: Long): WorkoutSession?

  @Transaction
  @Query("SELECT * FROM workout_session_history WHERE userId = :userId ORDER BY dateIso DESC, createdAt DESC")
  fun getFullSessionsWithDetails(userId: Long = 1): Flow<List<WorkoutSessionWithDetails>>
}

@Dao
interface ExerciseDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertExercise(exercise: Exercise): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertExercises(exercises: List<Exercise>): List<Long>

  @Query("SELECT * FROM exercises WHERE sessionId = :sessionId ORDER BY orderIndex ASC")
  fun getExercisesForSession(sessionId: Long): Flow<List<Exercise>>

  @Transaction
  @Query("SELECT * FROM exercises WHERE sessionId = :sessionId ORDER BY orderIndex ASC")
  fun getExercisesWithSets(sessionId: Long): Flow<List<ExerciseWithSetRecords>>
}

@Dao
interface SetRecordDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSetRecord(setRecord: SetRecord): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSetRecords(setRecords: List<SetRecord>): List<Long>

  @Query("SELECT * FROM set_records WHERE exerciseId = :exerciseId ORDER BY setNumber ASC")
  fun getSetRecordsForExercise(exerciseId: Long): Flow<List<SetRecord>>

  @Query("SELECT * FROM set_records WHERE userId = :userId AND isPersonalRecord = 1 ORDER BY estimated1Rm DESC")
  fun getPersonalRecords(userId: Long = 1): Flow<List<SetRecord>>

  @Query("SELECT COALESCE(SUM(volumeKg), 0.0) FROM set_records WHERE userId = :userId")
  fun getTotalVolumeForUser(userId: Long = 1): Flow<Float>
}
