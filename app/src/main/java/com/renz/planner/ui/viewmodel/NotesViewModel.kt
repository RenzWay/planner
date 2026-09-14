package com.renz.planner.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.renz.planner.data.notes.Notes
import com.renz.planner.repository.NotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(private val repository: NotesRepository) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val notesList: StateFlow<List<Notes>> = _searchQuery.flatMapLatest { query ->
        if (query.isBlank()) {
            repository.getAllNotes()
        } else {
            repository.searchNotes(query)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val favoriteNotesList: StateFlow<List<Notes>> = repository.getFavoriteNotes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun insertNote(note: Notes) {
        viewModelScope.launch {
            repository.insertNote(note)
        }
    }

    fun updateNote(note: Notes) {
        viewModelScope.launch {
            repository.updateNote(note)
        }
    }


    fun deleteNote(note: Notes) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }
}