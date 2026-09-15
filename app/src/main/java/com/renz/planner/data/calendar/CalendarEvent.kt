package com.renz.planner.data.calendar

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "events")
data class CalendarEvent(
    @PrimaryKey
    val id: String,
    val title: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val colorHex: Long,
    val isCompleted: Boolean = false,
    val reminderMinutesBefore: Int = 15
) : Parcelable