package com.renz.planner.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Note : Screen("notes")
    data object Calendar : Screen("calendar")
    data object NoteDetail : Screen("note_detail")
}
