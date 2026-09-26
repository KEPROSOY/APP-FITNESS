package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
  entities = [
    UserProfileEntity::class,
    MealEntity::class,
    WeightEntryEntity::class,
    WorkoutSessionEntity::class,
    WorkoutExerciseEntity::class,
    ExerciseSetEntity::class,
    PersonalRecordEntity::class,
    User::class,
    WorkoutSession::class,
    Exercise::class,
    SetRecord::class
  ],
  version = 7,
  exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun userProfileDao(): UserProfileDao
  abstract fun mealDao(): MealDao
  abstract fun weightDao(): WeightDao
  abstract fun workoutDao(): WorkoutDao
  abstract fun personalRecordDao(): PersonalRecordDao
  abstract fun userDao(): UserDao
  abstract fun workoutSessionRecordDao(): WorkoutSessionRecordDao
  abstract fun exerciseDao(): ExerciseDao
  abstract fun setRecordDao(): SetRecordDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "nutri_ai_database"
        )
          .fallbackToDestructiveMigration(dropAllTables = true)
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
