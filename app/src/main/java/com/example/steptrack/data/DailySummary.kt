package com.example.steptrack.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_summaries")
data class DailySummary(
    @PrimaryKey
    val date: String, // Format "yyyy-MM-dd"
    val totalSteps: Int
)
