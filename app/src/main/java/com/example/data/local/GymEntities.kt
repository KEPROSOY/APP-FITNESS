package com.example.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.data.model.AestheticMuscleCategory
import com.example.data.model.SetType

@Entity(
  tableName = "workout_sessions",
  foreignKeys = [
    ForeignKey(
      entity = UserProfileEntity::class,
      parentColumns = ["id"],
      childColumns = ["userId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index("userId"), Index("dateIso")]
)
data class WorkoutSessionEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val userId: Int = 1,
  val dateIso: String, // YYYY-MM-DD
  val title: String,
  val startTimeMillis: Long = System.currentTimeMillis(),
  val durationMinutes: Int = 60,
  val estimatedCaloriesBurned: Int = 0,
  val notes: String = ""
)

@Entity(
  tableName = "workout_exercises",
  foreignKeys = [
    ForeignKey(
      entity = WorkoutSessionEntity::class,
      parentColumns = ["id"],
      childColumns = ["sessionId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index("sessionId")]
)
data class WorkoutExerciseEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val sessionId: Long,
  val exerciseName: String,
  val category: AestheticMuscleCategory,
  val notes: String = "",
  val orderIndex: Int = 0
)

@Entity(
  tableName = "exercise_sets",
  foreignKeys = [
    ForeignKey(
      entity = WorkoutExerciseEntity::class,
      parentColumns = ["id"],
      childColumns = ["exerciseId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index("exerciseId")]
)
data class ExerciseSetEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val exerciseId: Long,
  val setNumber: Int,
  val weightKg: Float,
  val reps: Int,
  val rpe: Float? = null,
  val setType: SetType = SetType.NORMAL,
  val completed: Boolean = true,
  val estimated1Rm: Float = 0f
)

@Entity(
  tableName = "personal_records",
  indices = [Index("userId"), Index(value = ["userId", "exerciseName"], unique = true)]
)
data class PersonalRecordEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val userId: Int = 1,
  val exerciseName: String,
  val category: AestheticMuscleCategory,
  val bestWeightKg: Float,
  val bestReps: Int,
  val estimated1Rm: Float,
  val achievedDateIso: String,
  val timestamp: Long = System.currentTimeMillis()
)

data class WorkoutExerciseWithSets(
  @Embedded val exercise: WorkoutExerciseEntity,
  @Relation(
    parentColumn = "id",
    entityColumn = "exerciseId"
  )
  val sets: List<ExerciseSetEntity>
)

data class WorkoutSessionWithExercises(
  @Embedded val session: WorkoutSessionEntity,
  @Relation(
    entity = WorkoutExerciseEntity::class,
    parentColumn = "id",
    entityColumn = "sessionId"
  )
  val exercisesWithSets: List<WorkoutExerciseWithSets>
)
