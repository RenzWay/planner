package com.renz.planner.data.calendar

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarEventDao {
    @Query("SELECT * FROM events ORDER BY date ASC, startTime ASC")
    fun getAllEvents(): Flow<List<CalendarEvent>>

    @Query("SELECT * FROM events WHERE date = :dateString ORDER BY startTime ASC")
    fun getEventByDate(dateString: String): Flow<List<CalendarEvent>>

    @Query("SELECT * FROM events")
    suspend fun getAllEventsOnce(): List<CalendarEvent>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: CalendarEvent)

    @Delete
    suspend fun deleteEvent(event: CalendarEvent)

    @Update
    suspend fun updateEvent(event: CalendarEvent)
}