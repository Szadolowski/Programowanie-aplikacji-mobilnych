package com.example.steptrack.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_entries")
data class TrackEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timestamp: Long,
    val steps: Int,
    val latitude: Double,
    val longitude: Double
)
