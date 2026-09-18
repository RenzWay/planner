package com.renz.planner.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.renz.planner.data.calendar.CalendarEvent
import com.renz.planner.notification.CalendarNotificationManager
import com.renz.planner.repository.CalendarRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class CalendarViewModel(
    private val repository: CalendarRepository,
    private val notificationManager: CalendarNotificationManager
) : ViewModel() {

    private val _currentYearMonth = MutableStateFlow(YearMonth.now())
    val currentYearMonth: StateFlow<YearMonth> = _currentYearMonth.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    val allEvents: StateFlow<List<CalendarEvent>> = repository.allEvents
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val eventForSelectedDate: StateFlow<List<CalendarEvent>> = _selectedDate
        .flatMapLatest { date ->
            repository.getEventByDate(date.toString())
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
    }

    fun onMonthChanged(newMonth: YearMonth) {
        if (_currentYearMonth.value != newMonth) {
            _currentYearMonth.value = newMonth
            _selectedDate.value = newMonth.atDay(1)
        }
    }

    fun nextMonth() {
        val newMonth = _currentYearMonth.value.plusMonths(1)
        _currentYearMonth.value = newMonth
        // Sync selectedDate ke tanggal 1 bulan baru
        _selectedDate.value = newMonth.atDay(1)
    }

    fun previousMonth() {
        val newMonth = _currentYearMonth.value.minusMonths(1)
        _currentYearMonth.value = newMonth
        // Sync selectedDate ke tanggal 1 bulan baru
        _selectedDate.value = newMonth.atDay(1)
    }

    fun insertEvent(event: CalendarEvent) {
        viewModelScope.launch {
            repository.insertEvent(event)
            notificationManager.scheduleEventAlarm(event)
        }
    }

    fun updateEvent(event: CalendarEvent) {
        viewModelScope.launch {
            repository.updateEvent(event)
            notificationManager.scheduleEventAlarm(event)
        }
    }

    fun deleteEvent(event: CalendarEvent) {
        viewModelScope.launch {
            repository.deleteEvent(event)
            notificationManager.cancelEventAlarm(event)
        }
    }


}