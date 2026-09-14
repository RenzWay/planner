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
import com.renz.planner.data.notes.NotesDatabase
import com.renz.planner.helper.ViewModelFactory
import com.renz.planner.repository.NotesRepository
import com.renz.planner.ui.theme.PlannerTheme
import com.renz.planner.ui.viewmodel.NotesViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = NotesDatabase.getDatabase(applicationContext)
        val repository = NotesRepository(database.notesDao())

        val viewModel: NotesViewModel by viewModels {
            ViewModelFactory(repository)
        }

        setContent {
            PlannerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    App(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}