package com.renz.planner.helper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.renz.planner.repository.NotesRepository
import com.renz.planner.ui.viewmodel.NotesViewModel

class ViewModelFactory(private val repository: NotesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotesViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return NotesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}