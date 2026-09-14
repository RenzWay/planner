package com.renz.planner.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.renz.planner.navigation.Screen
import com.renz.planner.ui.screen.HomeScreen
import com.renz.planner.ui.screen.NoteScreen
import com.renz.planner.ui.viewmodel.CalendarViewModel
import com.renz.planner.ui.viewmodel.NotesViewModel

@Composable
fun App(
    notesViewModel: NotesViewModel,
    calendarViewModel: CalendarViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // Layar 1: Home
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToNotes = { navController.navigate(Screen.Note.route) },
                onNavigateToCalendar = { navController.navigate(Screen.Calendar.route) }
            )
        }

        // Layar 2: Notes
        composable(Screen.Note.route) {
            NoteScreen(viewModel = notesViewModel, onBackClick = { navController.popBackStack() })
        }

        // Layar 3: Calendar (Placeholder sementara)
        composable(Screen.Calendar.route) {
            // Tempat layar kalender nanti
        }
    }
}