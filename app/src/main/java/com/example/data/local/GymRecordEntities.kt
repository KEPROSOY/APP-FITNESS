package com.example.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.data.model.AestheticMuscleCategory

/**
 * Entidad User para Room:
 * Representa al usuario propietario de los registros permanentes de entrenamiento,
 * volumen y récords personales (PRs).
 */
@Entity(tableName = "users")
data class User(
  @PrimaryKey(autoGenerate = true) val id: Long = 1,
  val name: String = "Usuario",
  val email: String = "user@example.com",
  val weightKg: Float = 75.0f,
  val heightCm: Float = 175.0f,
  val bodyFatPercentage: Float? = null,
  val createdAt: Long = System.currentTimeMillis()
)

/**
 * Entidad WorkoutSession para Room:
 * Almacena permanentemente el historial de cada sesión de entrenamiento,
 * vinculada mediante clave foránea (ForeignKey) a la entidad User.
 */
@Entity(
  tableName = "workout_session_history",
  foreignKeys = [
    ForeignKey(
      entity = User::class,
      parentColumns = ["id"],
      childColumns = ["userId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index("userId"), Index("dateIso")]
)
data class WorkoutSession(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val userId: Long = 1,
  val dateIso: String, // Formato YYYY-MM-DD
  val title: String = "Entrenamiento",
  val durationMinutes: Int = 60,
  val totalVolumeKg: Float = 0f,
  val estimatedCaloriesBurned: Int = 0,
  val notes: String = "",
  val createdAt: Long = System.currentTimeMillis()
)

/**
 * Entidad Exercise para Room:
 * Representa cada ejercicio realizado dentro de una sesión,
 * con claves foráneas vinculadas a WorkoutSession y al User.
 */
@Entity(
  tableName = "exercises",
  foreignKeys = [
    ForeignKey(
      entity = WorkoutSession::class,
      parentColumns = ["id"],
      childColumns = ["sessionId"],
      onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
      entity = User::class,
      parentColumns = ["id"],
      childColumns = ["userId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index("sessionId"), Index("userId")]
)
data class Exercise(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val sessionId: Long,
  val userId: Long = 1,
  val name: String,
  val category: AestheticMuscleCategory = AestheticMuscleCategory.V_TAPER_DELTS,
  val targetRpe: Float = 8.0f,
  val notes: String = "",
  val orderIndex: Int = 0
)

/**
 * Entidad SetRecord para Room:
 * Registra cada serie individual (peso, repeticiones, RPE, volumen y 1RM estimado),
 * con claves foráneas a Exercise y a User para garantizar persistencia y trazabilidad de PRs.
 */
@Entity(
  tableName = "set_records",
  foreignKeys = [
    ForeignKey(
      entity = Exercise::class,
      parentColumns = ["id"],
      childColumns = ["exerciseId"],
      onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
      entity = User::class,
      parentColumns = ["id"],
      childColumns = ["userId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index("exerciseId"), Index("userId")]
)
data class SetRecord(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val exerciseId: Long,
  val userId: Long = 1,
  val setNumber: Int,
  val weightKg: Float,
  val reps: Int,
  val rpe: Float? = null,
  val volumeKg: Float = weightKg * reps,
  val estimated1Rm: Float = 0f,
  val isPersonalRecord: Boolean = false,
  val completed: Boolean = true,
  val timestamp: Long = System.currentTimeMillis()
)

/**
 * Relaciones para consultas agregadas (Exercise con sus SetRecords)
 */
data class ExerciseWithSetRecords(
  @Embedded val exercise: Exercise,
  @Relation(
    parentColumn = "id",
    entityColumn = "exerciseId"
  )
  val sets: List<SetRecord>
)

/**
 * Relaciones para sesión completa con todos sus ejercicios y series
 */
data class WorkoutSessionWithDetails(
  @Embedded val session: WorkoutSession,
  @Relation(
    entity = Exercise::class,
    parentColumn = "id",
    entityColumn = "sessionId"
  )
  val exercises: List<ExerciseWithSetRecords>
)
