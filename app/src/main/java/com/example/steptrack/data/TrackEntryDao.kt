package com.example.steptrack.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TrackEntryDao {
    @Insert
    suspend fun insert(trackEntry: TrackEntry)

    @Query("SELECT * FROM track_entries ORDER BY timestamp DESC")
    fun getAllEntries(): LiveData<List<TrackEntry>>

    @Query("SELECT * FROM track_entries WHERE timestamp >= :startOfDay AND timestamp < :endOfDay")
    fun getEntriesForDay(startOfDay: Long, endOfDay: Long): LiveData<List<TrackEntry>>
}
