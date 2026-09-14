package com.renz.planner.repository

import com.renz.planner.data.calendar.CalendarEvent
import com.renz.planner.data.calendar.CalendarEventDao
import kotlinx.coroutines.flow.Flow

class CalendarRepository(private val eventDao: CalendarEventDao) {
    val allEvents: Flow<List<CalendarEvent>> = eventDao.getAllEvents()

    fun getEventByDate(dateString: String): Flow<List<CalendarEvent>> {
        return eventDao.getEventByDate(dateString)
    }

    suspend fun insertEvent(event: CalendarEvent) {
        eventDao.insertEvent(event)
    }

    suspend fun deleteEvent(event: CalendarEvent) {
        eventDao.deleteEvent(event)
    }

    suspend fun updateEvent(event: CalendarEvent) {
        eventDao.updateEvent(event)
    }

}