package com.example.steptrack.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workouts",
    foreignKeys = [
        ForeignKey(
            entity = DailySummary::class,
            parentColumns = ["date"],
            childColumns = ["parentDate"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("parentDate")]
)
data class Workout(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val parentDate: String, // Klucz obcy do DailySummary (yyyy-MM-dd)
    val startTime: Long,
    val endTime: Long,
    val steps: Int,
    val distanceMetres: Double,
    val routePointsJson: String // Wybrany format: JSON String
)
