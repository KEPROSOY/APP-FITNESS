package com.example

import com.example.data.model.AestheticMuscleCategory
import com.example.data.model.ExerciseSet
import com.example.data.model.WorkoutExercise
import com.example.data.model.WorkoutSession
import com.example.fitness.FitnessEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class GymViewModelTest {

  @Test
  fun `test Epley 1RM calculation`() {
    // 100 kg x 10 reps -> Epley = 100 * (1 + 10 / 30) = 133.33 kg
    val epley = FitnessEngine.calculateEpley1Rm(100f, 10)
    assertTrue("Epley should be approximately 133.3 kg", abs(epley - 133.3f) < 0.2f)

    // 1 rep should equal original weight
    val singleRep = FitnessEngine.calculateEpley1Rm(120f, 1)
    assertEquals(120f, singleRep)
  }

  @Test
  fun `test Brzycki 1RM calculation`() {
    // 100 kg x 10 reps -> Brzycki = 100 * 36 / (37 - 10) = 100 * 36 / 27 = 133.33 kg
    val brzycki = FitnessEngine.calculateBrzycki1Rm(100f, 10)
    assertTrue("Brzycki should be approximately 133.3 kg", abs(brzycki - 133.3f) < 0.2f)

    val singleRep = FitnessEngine.calculateBrzycki1Rm(120f, 1)
    assertEquals(120f, singleRep)
  }

  @Test
  fun `test compound 1RM calculation and load projections`() {
    val weight = 80f
    val reps = 6
    val estimated1Rm = FitnessEngine.estimate1Rm(weight, reps)

    // 80kg x 6 reps should be around 93-96kg 1RM
    assertTrue("Compound 1RM should be in realistic range", estimated1Rm in 92f..97f)

    // Load projection for 3 reps (93%), 5 reps (87%), 10 reps (75%)
    val load3Reps = FitnessEngine.estimateTargetLoad(estimated1Rm, 3)
    val load5Reps = FitnessEngine.estimateTargetLoad(estimated1Rm, 5)
    val load10Reps = FitnessEngine.estimateTargetLoad(estimated1Rm, 10)

    assertTrue("Heavy reps require higher load than light reps", load3Reps > load5Reps)
    assertTrue("5 reps require higher load than 10 reps", load5Reps > load10Reps)
    assertTrue("10 reps load should be lower than original 6 reps weight", load10Reps < weight + 5f)
  }

  @Test
  fun `test workout training volume calculation`() {
    val sets = listOf(
      ExerciseSet(setNumber = 1, weightKg = 50f, reps = 10, completed = true), // 500 kg
      ExerciseSet(setNumber = 2, weightKg = 60f, reps = 8, completed = true),  // 480 kg
      ExerciseSet(setNumber = 3, weightKg = 70f, reps = 6, completed = true)   // 420 kg
    )
    val exercise = WorkoutExercise(
      exerciseName = "Press Militar",
      category = AestheticMuscleCategory.V_TAPER_DELTS,
      sets = sets
    )

    assertEquals(1400f, exercise.totalVolumeKg)
    assertEquals(3, exercise.completedSetsCount)

    val session = WorkoutSession(
      dateIso = "2026-09-21",
      title = "Día de Hombro",
      durationMinutes = 45,
      exercises = listOf(exercise)
    )

    assertEquals(1400f, session.totalVolumeKg)
    assertEquals(3, session.totalCompletedSets)
  }

  @Test
  fun `test symmetry distribution and aesthetic balance`() {
    val deltsExercise = WorkoutExercise(
      exerciseName = "Press Militar",
      category = AestheticMuscleCategory.V_TAPER_DELTS,
      sets = listOf(ExerciseSet(setNumber = 1, weightKg = 50f, reps = 10, completed = true)) // 500 kg
    )
    val latsExercise = WorkoutExercise(
      exerciseName = "Dominadas Lastradas",
      category = AestheticMuscleCategory.V_TAPER_LATS,
      sets = listOf(ExerciseSet(setNumber = 1, weightKg = 70f, reps = 10, completed = true)) // 700 kg
    )

    val distribution = FitnessEngine.calculateSymmetryDistribution(listOf(deltsExercise, latsExercise))
    val totalVol = distribution.sumOf { it.actualVolumeKg.toDouble() }.toFloat()

    assertEquals(1200f, totalVol)
    assertTrue("All aesthetic muscle categories should be represented", distribution.size == AestheticMuscleCategory.values().size)
  }
}
