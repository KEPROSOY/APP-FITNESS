package com.example

import com.example.data.local.Exercise
import com.example.data.local.SetRecord
import com.example.data.local.User
import com.example.data.local.WorkoutSession
import com.example.data.model.AestheticMuscleCategory
import com.example.data.model.AestheticRank
import com.example.data.model.PersonalRecord
import com.example.service.AestheticRankService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AestheticRankServiceTest {

  private lateinit var service: AestheticRankService

  @Before
  fun setUp() {
    service = AestheticRankService()
  }

  @Test
  fun testBrzyckiEstimated1RmCalculation() {
    // 80 kg para 1 rep debe ser 80 kg
    val oneRep = service.calculateEstimated1Rm(80f, 1)
    assertEquals(80f, oneRep, 0.01f)

    // 80 kg para 6 reps con Brzycki: 80 / (1.0278 - (0.0278 * 6)) = 80 / 0.861 = ~92.9 kg
    val sixReps = service.calculateEstimated1Rm(80f, 6)
    assertTrue("1RM estimado debe ser mayor que el peso levantado", sixReps > 80f)
    assertEquals(92.9f, sixReps, 1.0f)
  }

  @Test
  fun testAestheticRankProgressionHierarchy() {
    // Madera (< 0.6x)
    assertEquals(AestheticRank.MADERA, service.getRankForRelativeLoad(0.4f))

    // Hierro (0.6x - 0.89x)
    assertEquals(AestheticRank.HIERRO, service.getRankForRelativeLoad(0.7f))

    // Oro (0.9x - 1.19x)
    assertEquals(AestheticRank.ORO, service.getRankForRelativeLoad(1.0f))

    // Platino (1.2x - 1.49x)
    assertEquals(AestheticRank.PLATINO, service.getRankForRelativeLoad(1.3f))

    // Esmeralda (1.5x - 1.79x)
    assertEquals(AestheticRank.ESMERALDA, service.getRankForRelativeLoad(1.6f))

    // Diamante (1.8x - 2.09x)
    assertEquals(AestheticRank.DIAMANTE, service.getRankForRelativeLoad(1.9f))

    // Diamante Olímpico (>= 2.1x)
    assertEquals(AestheticRank.DIAMANTE_OLIMPICO, service.getRankForRelativeLoad(2.2f))
  }

  @Test
  fun testCalculateProgressLevelWithLoadAndVolume() {
    val userWeight = 75f
    val prs = listOf(
      PersonalRecord(
        id = 1,
        exerciseName = "Press Militar",
        category = AestheticMuscleCategory.V_TAPER_DELTS,
        bestWeightKg = 70f,
        bestReps = 5,
        estimated1Rm = 80f,
        achievedDateIso = "2026-09-20"
      ),
      PersonalRecord(
        id = 2,
        exerciseName = "Dominadas Lastradas",
        category = AestheticMuscleCategory.V_TAPER_LATS,
        bestWeightKg = 30f,
        bestReps = 6,
        estimated1Rm = 110f, // 75kg peso corporal + 35kg
        achievedDateIso = "2026-09-20"
      ),
      PersonalRecord(
        id = 3,
        exerciseName = "Press Banca Inclinado",
        category = AestheticMuscleCategory.CHEST_UPPER_LOWER,
        bestWeightKg = 85f,
        bestReps = 6,
        estimated1Rm = 98f,
        achievedDateIso = "2026-09-20"
      )
    )

    val result = service.calculateProgressLevel(
      userWeightKg = userWeight,
      personalRecords = prs,
      totalVolumeKg = 30000f
    )

    assertNotNull(result.currentRank)
    assertTrue("El usuario debe estar al menos en nivel Hierro u Oro", result.currentRank >= AestheticRank.HIERRO)
    assertTrue("Progreso debe estar entre 0 y 1", result.progressPercentage in 0f..1f)
    assertNotNull(result.feedbackMessage)
  }

  @Test
  fun testSuggestDailyMuscleGroupWorkouts() {
    val recommendationMonday = service.suggestDailyMuscleGroupWorkouts(
      dayOfWeek = "Lunes",
      laggingCategories = listOf(AestheticMuscleCategory.V_TAPER_DELTS)
    )

    assertEquals("Lunes", recommendationMonday.dayOfWeek)
    assertEquals(AestheticMuscleCategory.V_TAPER_DELTS, recommendationMonday.primaryFocus)
    assertTrue(recommendationMonday.suggestedExercises.isNotEmpty())

    // Verificar que un día en inglés también funcione correctamente
    val recommendationTuesday = service.suggestDailyMuscleGroupWorkouts(
      dayOfWeek = "Tuesday"
    )
    assertEquals(AestheticMuscleCategory.V_TAPER_LATS, recommendationTuesday.primaryFocus)

    val muscleGroupsMonday = service.suggestDailyMuscleGroups("Lunes")
    assertTrue(muscleGroupsMonday.contains(AestheticMuscleCategory.V_TAPER_DELTS))
  }

  @Test
  fun testCalculateAestheticRankWithVolumeBonus() {
    // 0.85x sin volumen -> HIERRO
    val rankNoVolume = service.calculateAestheticRank(0.85f, 0f)
    assertEquals(AestheticRank.HIERRO, rankNoVolume)

    // 0.85x + 30000kg de volumen (+0.15f) = 1.00x -> ORO
    val rankWithVolume = service.calculateAestheticRank(0.85f, 30000f)
    assertEquals(AestheticRank.ORO, rankWithVolume)
  }

  @Test
  fun testRoomEntitiesInstantiation() {
    val user = User(id = 1, name = "Aesthetic Lifter", weightKg = 78.5f)
    val session = WorkoutSession(id = 10, userId = user.id, dateIso = "2026-09-21", title = "Upper V-Taper")
    val exercise = Exercise(id = 100, sessionId = session.id, userId = user.id, name = "Press Militar", category = AestheticMuscleCategory.V_TAPER_DELTS)
    val setRecord = SetRecord(
      id = 1000,
      exerciseId = exercise.id,
      userId = user.id,
      setNumber = 1,
      weightKg = 60f,
      reps = 8,
      volumeKg = 480f,
      estimated1Rm = 74.5f,
      isPersonalRecord = true
    )

    assertEquals(1L, user.id)
    assertEquals(10L, session.id)
    assertEquals(100L, exercise.id)
    assertEquals(10L, exercise.sessionId)
    assertEquals(1L, exercise.userId)
    assertEquals(100L, setRecord.exerciseId)
    assertEquals(1L, setRecord.userId)
    assertEquals(480f, setRecord.volumeKg, 0.01f)
    assertTrue(setRecord.isPersonalRecord)
  }
}
