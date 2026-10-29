package com.renz.planner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.renz.planner.app.App
import com.renz.planner.data.calendar.CalendarDatabase
import com.renz.planner.data.notes.NotesDatabase
import com.renz.planner.helper.ViewModelFactory
import com.renz.planner.navigation.Screen
import com.renz.planner.notification.CalendarNotificationManager
import com.renz.planner.repository.CalendarRepository
import com.renz.planner.repository.NotesRepository
import com.renz.planner.ui.theme.PlannerTheme
import com.renz.planner.ui.viewmodel.CalendarViewModel
import com.renz.planner.ui.viewmodel.NotesViewModel
import java.time.LocalDate
import java.time.YearMonth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
                    .launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        val notesDatabase = NotesDatabase.getDatabase(applicationContext)
        val calendarDatabase = CalendarDatabase.getDatabase(applicationContext)

        val notesRepository = NotesRepository(notesDatabase.notesDao())
        val calendarRepository = CalendarRepository(calendarDatabase.eventDao())
        val notificationManager = CalendarNotificationManager(applicationContext)

        val factory = ViewModelFactory(
            notesRepository = notesRepository,
            calendarRepository = calendarRepository,
            notificationManager = notificationManager
        )

        val notesViewModel: NotesViewModel by viewModels { factory }
        val calendarViewModel: CalendarViewModel by viewModels { factory }

        val selectedDateStr = intent?.getStringExtra("SELECTED_DATE")
        if (!selectedDateStr.isNullOrEmpty()) {
            runCatching {
                val date = LocalDate.parse(selectedDateStr)
                calendarViewModel.onDateSelected(date)
                calendarViewModel.onMonthChanged((YearMonth.from((date))))
            }
        }

        val navigateTo = intent?.getStringExtra("NAVIGATE_TO")
        val startDestination =
            if (navigateTo == "calendar") Screen.Calendar.route else Screen.Home.route

        setContent {
            PlannerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    App(
                        notesViewModel = notesViewModel,
                        calendarViewModel = calendarViewModel,
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}