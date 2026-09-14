package com.renz.planner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.renz.planner.app.App
import com.renz.planner.data.calendar.CalendarDatabase
import com.renz.planner.data.notes.NotesDatabase
import com.renz.planner.helper.ViewModelFactory
import com.renz.planner.repository.CalendarRepository
import com.renz.planner.repository.NotesRepository
import com.renz.planner.ui.theme.PlannerTheme
import com.renz.planner.ui.viewmodel.CalendarViewModel
import com.renz.planner.ui.viewmodel.NotesViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val notesDatabase = NotesDatabase.getDatabase(applicationContext)
        val calendarDatabase = CalendarDatabase.getDatabase(applicationContext)

        val notesRepository = NotesRepository(notesDatabase.notesDao())
        val calendarRepository = CalendarRepository(calendarDatabase.eventDao())

        val factory = ViewModelFactory(
            notesRepository = notesRepository,
            calendarRepository = calendarRepository
        )

        val notesViewModel: NotesViewModel by viewModels { factory }
        val calendarViewModel: CalendarViewModel by viewModels { factory }

        setContent {
            PlannerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    App(
                        notesViewModel = notesViewModel,
                        calendarViewModel = calendarViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}