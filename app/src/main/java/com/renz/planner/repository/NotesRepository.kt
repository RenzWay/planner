package com.renz.planner.repository

import com.renz.planner.data.notes.Notes
import com.renz.planner.data.notes.NotesDao
import kotlinx.coroutines.flow.Flow

class NotesRepository(private val notesDao: NotesDao) {
    fun getAllNotes(): Flow<List<Notes>> {
        return notesDao.getAllNotes()
    }

    suspend fun getNoteById(id: String): Notes? {
        return notesDao.getNoteById(id)
    }

    fun getFavoriteNotes(): Flow<List<Notes>> = notesDao.getFavoriteNotes()

    fun searchNotes(query: String): Flow<List<Notes>> {
        return notesDao.searchNotes(query)
    }

    suspend fun insertNote(note: Notes) {
        notesDao.insertNote(note)
    }

    suspend fun deleteNote(note: Notes) {
        notesDao.deleteNote(note)
    }

    suspend fun updateNote(note: Notes) {
        notesDao.updateNote(note)
    }

}