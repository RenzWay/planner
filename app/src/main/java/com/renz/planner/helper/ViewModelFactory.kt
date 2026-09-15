package com.renz.planner.helper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.renz.planner.repository.CalendarRepository
import com.renz.planner.repository.NotesRepository
import com.renz.planner.ui.viewmodel.CalendarViewModel
import com.renz.planner.ui.viewmodel.NotesViewModel

class ViewModelFactory(
    private val notesRepository: NotesRepository,
    private val calendarRepository: CalendarRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return when {
            modelClass.isAssignableFrom(NotesViewModel::class.java) -> {
                NotesViewModel(notesRepository) as T
            }

            modelClass.isAssignableFrom(CalendarViewModel::class.java) -> {
                CalendarViewModel(calendarRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class ${modelClass.name}")
        } as T
    }

}