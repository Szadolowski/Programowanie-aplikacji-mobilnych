package com.example.steptrack.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DailySummary::class, Workout::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun workoutDao(): WorkoutDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "step_track_database"
                )
                .fallbackToDestructiveMigration() // Dla celów deweloperskich przy zmianie schematu
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
