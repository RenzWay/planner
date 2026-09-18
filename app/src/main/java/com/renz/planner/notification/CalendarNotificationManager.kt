package com.renz.planner.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.renz.planner.MainActivity
import com.renz.planner.data.calendar.CalendarEvent
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class CalendarNotificationManager(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        const val CHANNEL_ID = "calendar_event_channel"
        const val CHANNEL_NAME = "Pengingat Kalender"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi pengingat agenda kalender"
                enableVibration(true)
                enableLights(true)
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleEventAlarm(event: CalendarEvent) {
        cancelEventAlarm(event)
        val intent = Intent(context, CalendarAlarmReceiver::class.java).apply {
            putExtra("EVENT_ID", event.id)
            putExtra("EVENT_TITLE", event.title)
            putExtra("EVENT_TIME", "${event.startTime} - ${event.endTime}")
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            event.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val eventDateTimeString = "${event.date} ${event.startTime}"
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val eventLocalDateTime = LocalDateTime.parse(eventDateTimeString, formatter)

        val triggerLocalDateTime =
            eventLocalDateTime.minusMinutes(event.reminderMinutesBefore.toLong())
        val triggerMillis =
            triggerLocalDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        if (triggerMillis <= System.currentTimeMillis()) return

        var finalTriggerMillis = triggerMillis
        if (finalTriggerMillis <= System.currentTimeMillis()) {
            val eventTimeMillis =
                eventLocalDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            if (eventTimeMillis > System.currentTimeMillis()) {
                finalTriggerMillis = System.currentTimeMillis() + 1000
            } else {
                return
            }
        }

        val showIntent = Intent(context, MainActivity::class.java)
        val showPendingIntent = PendingIntent.getActivity(
            context,
            0,
            showIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmClockInfo = AlarmManager.AlarmClockInfo(finalTriggerMillis, showPendingIntent)
        alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)

    }

    fun cancelEventAlarm(event: CalendarEvent) {
        val intent = Intent(context, CalendarAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            event.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }

    }

}