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

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
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