package com.renz.planner.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.renz.planner.data.calendar.CalendarDatabase
import com.renz.planner.repository.CalendarRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CalendarBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val database = CalendarDatabase.getDatabase(context)
            val repository = CalendarRepository(database.eventDao())
            val notificationManager = CalendarNotificationManager(context)

            GlobalScope.launch(Dispatchers.IO) {
                val events = repository.getAllEventsOnce()
                events.forEach { event ->
                    notificationManager.scheduleEventAlarm(event)
                }
            }
        }
    }
}