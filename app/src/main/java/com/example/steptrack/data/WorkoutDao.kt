package com.example.steptrack.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WorkoutDao {
    // ZMIANA: Używamy IGNORE, aby nie nadpisywać (i nie usuwać kaskadowo) istniejącego dnia
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDailySummary(summary: DailySummary)

    @Insert
    suspend fun insertWorkout(workout: Workout)

    @Query("SELECT * FROM daily_summaries ORDER BY date DESC")
    fun getAllDailySummaries(): LiveData<List<DailySummary>>

    @Transaction
    @Query("SELECT * FROM daily_summaries ORDER BY date DESC")
    fun getAllDaysWithWorkouts(): LiveData<List<DayWithWorkouts>>

    @Query("SELECT * FROM workouts WHERE parentDate = :date ORDER BY startTime ASC")
    fun getWorkoutsForDay(date: String): LiveData<List<Workout>>

    @Transaction
    @Query("SELECT * FROM daily_summaries WHERE date = :date")
    fun getDayWithWorkouts(date: String): LiveData<DayWithWorkouts>
}

data class DayWithWorkouts(
    @Embedded val day: DailySummary,
    @Relation(
        parentColumn = "date",
        entityColumn = "parentDate"
    )
    val workouts: List<Workout>
)
