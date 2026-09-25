package com.renz.planner.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.renz.planner.data.calendar.CalendarDatabase
import com.renz.planner.repository.CalendarRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CalendarBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == "android.intent.action.QUICKBOOT_POWERON" ||
            action == "com.htc.intent.action.QUICKBOOT_POWERON" ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED
        ) {

            val pendingResult = goAsync()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val database = CalendarDatabase.getDatabase(context)
                    val repository = CalendarRepository(database.eventDao())
                    val notificationManager = CalendarNotificationManager(context)

                    val events = repository.getAllEventsOnce()
                    events.forEach { event ->
                        try {
                            notificationManager.scheduleEventAlarm(event)
                        } catch (e: Exception) {
                            Log.e(
                                "CalendarBootReceiver",
                                "Gagal me-reschedule event: ${event.id}",
                                e
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.e("CalendarBootReceiver", "Gagal memproses boot receiver", e)
                } finally {

                    pendingResult.finish()
                }
            }
        }
    }
}