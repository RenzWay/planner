package com.renz.planner.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.renz.planner.MainActivity
import com.renz.planner.data.calendar.CalendarEvent
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

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
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
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

        if (event.isCompleted) return


        // Formatter fleksibel untuk menangani format "H:m" maupun "HH:mm"
        val formatter = DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd ")
            .appendValue(ChronoField.HOUR_OF_DAY)
            .appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .toFormatter()

        val eventLocalDateTime = try {
            LocalDateTime.parse("${event.date} ${event.startTime}", formatter)
        } catch (e: Exception) {
            Log.e(
                "CalendarNotification",
                "Gagal parse tanggal: ${event.date} ${event.startTime}",
                e
            )
            return
        }

        val triggerLocalDateTime =
            eventLocalDateTime.minusMinutes(event.reminderMinutesBefore.toLong())
        var triggerMillis =
            triggerLocalDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val nowMillis = System.currentTimeMillis()
        var isOverdue = false

        if (triggerMillis <= nowMillis) {
            val eventTimeMillis =
                eventLocalDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            if (eventTimeMillis > nowMillis) {
                // Jika waktu pengingat sudah lewat tetapi acara belum mulai, ingatkan sekarang
                triggerMillis = nowMillis + 1000
            } else {
                isOverdue = true
                triggerMillis = nowMillis + 1000
            }
        }

        val intent = Intent(context, CalendarAlarmReceiver::class.java).apply {
            putExtra("EVENT_ID", event.id)
            putExtra("EVENT_TITLE", event.title)
            putExtra("EVENT_DATE", event.date)
            putExtra("EVENT_TIME", "${event.startTime} - ${event.endTime}")
            putExtra("IS_OVERDUE", isOverdue)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            event.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val showIntent = Intent(context, MainActivity::class.java)
        val showPendingIntent = PendingIntent.getActivity(
            context,
            0,
            showIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
                )
            } else {
                val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerMillis, showPendingIntent)
                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
            }
        } catch (e: SecurityException) {
            Log.e("CalendarNotification", "SecurityException saat menjadwalkan alarm", e)
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerMillis,
                pendingIntent
            )
        }
    }

    fun cancelEventAlarm(event: CalendarEvent) {
        val intent = Intent(context, CalendarAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            event.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }
}