package com.example.data.repository

import com.example.data.local.ExerciseSetEntity
import com.example.data.local.PersonalRecordDao
import com.example.data.local.PersonalRecordEntity
import com.example.data.local.UserProfileDao
import com.example.data.local.WorkoutDao
import com.example.data.local.WorkoutExerciseEntity
import com.example.data.local.WorkoutSessionEntity
import com.example.data.local.WorkoutSessionWithExercises
import com.example.data.model.AestheticMuscleCategory
import com.example.data.model.DailyGymSummary
import com.example.data.model.ExerciseSet
import com.example.data.model.PersonalRecord
import com.example.data.model.WorkoutExercise
import com.example.data.model.WorkoutSession
import com.example.fitness.FitnessEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class WorkoutRepository(
  private val workoutDao: WorkoutDao,
  private val personalRecordDao: PersonalRecordDao,
  private val userProfileDao: UserProfileDao
) {

  val allSessionsFlow: Flow<List<WorkoutSession>> = workoutDao.getAllSessionsWithDetails(userId = 1)
    .map { list -> list.map { it.toDomainModel() } }

  val personalRecordsFlow: Flow<List<PersonalRecord>> = personalRecordDao.getAllPersonalRecords(userId = 1)
    .map { list -> list.map { it.toDomainModel() } }

  fun getSessionsForDate(dateIso: String): Flow<List<WorkoutSession>> {
    return workoutDao.getSessionsByDate(dateIso, userId = 1)
      .map { list -> list.map { it.toDomainModel() } }
  }

  fun getSessionsBetweenDates(startDateIso: String, endDateIso: String): Flow<List<WorkoutSession>> {
    return workoutDao.getSessionsBetweenDates(startDateIso, endDateIso, userId = 1)
      .map { list -> list.map { it.toDomainModel() } }
  }


  fun getDailyGymSummary(dateIso: String): Flow<DailyGymSummary> {
    return workoutDao.getSessionsByDate(dateIso, userId = 1).map { sessionList ->
      val domainSessions = sessionList.map { it.toDomainModel() }
      val totalVolume = domainSessions.sumOf { it.totalVolumeKg.toDouble() }.toFloat()
      val totalCalories = domainSessions.sumOf { it.estimatedCaloriesBurned }
      val totalMinutes = domainSessions.sumOf { it.durationMinutes }

      DailyGymSummary(
        dateIso = dateIso,
        sessionsCount = domainSessions.size,
        totalMinutes = totalMinutes,
        totalVolumeKg = totalVolume,
        totalCaloriesBurned = totalCalories,
        sessions = domainSessions
      )
    }
  }

  fun getCaloriesBurnedFlow(dateIso: String): Flow<Int> {
    return workoutDao.getCaloriesBurnedByDate(dateIso, userId = 1)
  }

  suspend fun saveWorkoutSession(session: WorkoutSession): Long = withContext(Dispatchers.IO) {
    val profile = userProfileDao.getUserProfile()
    val userWeight = profile?.weightKg ?: 70f

    // 1. Calcular 1RM para cada set y detectar récords personales
    val processedExercises = session.exercises.map { exercise ->
      val processedSets = exercise.sets.map { set ->
        val oneRm = FitnessEngine.estimate1Rm(set.weightKg, set.reps)
        set.copy(estimated1Rm = oneRm)
      }
      exercise.copy(sets = processedSets)
    }

    val totalVolume = processedExercises.sumOf { it.totalVolumeKg.toDouble() }.toFloat()
    val caloriesBurned = FitnessEngine.calculateWorkoutCaloriesBurned(
      userWeightKg = userWeight,
      durationMinutes = session.durationMinutes,
      volumeKg = totalVolume
    )

    // 2. Insertar o actualizar sesión
    val sessionEntity = WorkoutSessionEntity(
      id = session.id,
      userId = 1,
      dateIso = session.dateIso,
      title = session.title,
      startTimeMillis = session.startTimeMillis,
      durationMinutes = session.durationMinutes,
      estimatedCaloriesBurned = caloriesBurned,
      notes = session.notes
    )
    val sessionId = workoutDao.insertSession(sessionEntity)

    // Si ya existía la sesión, limpiamos los ejercicios anteriores para persistir la nueva edición limpia
    if (session.id > 0) {
      workoutDao.deleteExercisesBySessionId(session.id)
    }

    // 3. Insertar ejercicios y sets
    for ((index, exercise) in processedExercises.withIndex()) {
      val exerciseEntity = WorkoutExerciseEntity(
        id = 0, // Nuevo ID para inserción limpia
        sessionId = sessionId,
        exerciseName = exercise.exerciseName,
        category = exercise.category,
        notes = exercise.notes,
        orderIndex = index
      )
      val exerciseId = workoutDao.insertExercise(exerciseEntity)

      val setEntities = exercise.sets.mapIndexed { sIdx, set ->
        ExerciseSetEntity(
          id = 0,
          exerciseId = exerciseId,
          setNumber = sIdx + 1,
          weightKg = set.weightKg,
          reps = set.reps,
          rpe = set.rpe,
          setType = set.setType,
          completed = set.completed,
          estimated1Rm = set.estimated1Rm
        )
      }
      workoutDao.insertSets(setEntities)

      // 4. Evaluar PRs (Personal Records)
      val bestSet = exercise.sets.filter { it.completed }.maxByOrNull { it.estimated1Rm }
      if (bestSet != null && bestSet.estimated1Rm > 0f) {
        val existingPr = personalRecordDao.getRecordForExercise(exercise.exerciseName, userId = 1)
        if (existingPr == null || bestSet.estimated1Rm > existingPr.estimated1Rm) {
          personalRecordDao.insertOrUpdateRecord(
            PersonalRecordEntity(
              id = existingPr?.id ?: 0,
              userId = 1,
              exerciseName = exercise.exerciseName,
              category = exercise.category,
              bestWeightKg = bestSet.weightKg,
              bestReps = bestSet.reps,
              estimated1Rm = bestSet.estimated1Rm,
              achievedDateIso = session.dateIso
            )
          )
        }
      }
    }

    sessionId
  }

  suspend fun deleteSession(sessionId: Long) = withContext(Dispatchers.IO) {
    workoutDao.deleteSessionById(sessionId)
  }

  /**
   * Borra todo el historial de entrenamientos y récords para reiniciar el progreso desde 0.
   */
  suspend fun clearAllWorkoutHistory() = withContext(Dispatchers.IO) {
    workoutDao.clearAllSessions(userId = 1)
    personalRecordDao.clearAllRecords(userId = 1)
  }

  // Conversiones
  private fun WorkoutSessionWithExercises.toDomainModel(): WorkoutSession {
    val exercises = exercisesWithSets.map { exWithSets ->
      WorkoutExercise(
        id = exWithSets.exercise.id,
        exerciseName = exWithSets.exercise.exerciseName,
        category = exWithSets.exercise.category,
        notes = exWithSets.exercise.notes,
        sets = exWithSets.sets.map { s ->
          ExerciseSet(
            id = s.id,
            setNumber = s.setNumber,
            weightKg = s.weightKg,
            reps = s.reps,
            rpe = s.rpe,
            setType = s.setType,
            completed = s.completed,
            estimated1Rm = s.estimated1Rm
          )
        }
      )
    }

    return WorkoutSession(
      id = session.id,
      userId = session.userId,
      dateIso = session.dateIso,
      title = session.title,
      startTimeMillis = session.startTimeMillis,
      durationMinutes = session.durationMinutes,
      exercises = exercises,
      estimatedCaloriesBurned = session.estimatedCaloriesBurned,
      notes = session.notes
    )
  }

  private fun PersonalRecordEntity.toDomainModel(): PersonalRecord {
    return PersonalRecord(
      id = id,
      userId = userId,
      exerciseName = exerciseName,
      category = category,
      bestWeightKg = bestWeightKg,
      bestReps = bestReps,
      estimated1Rm = estimated1Rm,
      achievedDateIso = achievedDateIso
    )
  }
}
