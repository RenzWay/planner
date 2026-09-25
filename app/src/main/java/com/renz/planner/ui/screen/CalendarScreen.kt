package com.renz.planner.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.renz.planner.data.calendar.CalendarEvent
import com.renz.planner.data.calendar.CalendarViewMode
import com.renz.planner.helper.AutostartHelper
import com.renz.planner.ui.components.AutostartDialog
import com.renz.planner.ui.components.CalendarSpeedDialFab
import com.renz.planner.ui.components.CalendarTopBar
import com.renz.planner.ui.components.DailyTimelineContent
import com.renz.planner.ui.components.EventEditorSheet
import com.renz.planner.ui.components.MonthCalendarContent
import com.renz.planner.ui.theme.PlannerTheme
import com.renz.planner.ui.viewmodel.CalendarViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    onBackClick: () -> Unit,
) {
    val currentYearMonth by viewModel.currentYearMonth.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val eventsForSelectedDate by viewModel.eventForSelectedDate.collectAsStateWithLifecycle()
    val allEvents by viewModel.allEvents.collectAsStateWithLifecycle()
    var viewMode by remember { mutableStateOf(CalendarViewMode.MONTH) }
    var selectedEventForEdit by remember { mutableStateOf<CalendarEvent?>(null) }

    val context = LocalContext.current
    var showAutostartDialog by remember {
        mutableStateOf(AutostartHelper.shouldShowDialog(context))
    }

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    BackHandler(enabled = viewMode == CalendarViewMode.DAILY) {
        viewMode = CalendarViewMode.MONTH
    }

    val coroutineScope = rememberCoroutineScope()
    val handleBack = {
        if (viewMode == CalendarViewMode.DAILY) {
            viewMode = CalendarViewMode.MONTH
        } else {
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            CalendarTopBar(
                currentYearMonth = currentYearMonth,
                viewMode = viewMode,
                onBackClick = handleBack,
                onPreviousMonth = { viewModel.previousMonth() },
                onNextMonth = { viewModel.nextMonth() },
                onToggleViewMode = {
                    viewMode = if (viewMode == CalendarViewMode.MONTH) {
                        CalendarViewMode.DAILY
                    } else {
                        CalendarViewMode.MONTH
                    }
                },
                onOpenAutostartInfo = { showAutostartDialog = true }
            )
        },
        floatingActionButton = {
            CalendarSpeedDialFab(
                onAddEventClick = {
                    selectedEventForEdit = null
                    showBottomSheet = true
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(targetState = viewMode, transitionSpec = {
                if (targetState == CalendarViewMode.DAILY) {
                    (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                        slideOutHorizontally { width -> -width } + fadeOut()
                    )
                } else {
                    (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                        slideOutHorizontally { width -> width } + fadeOut()
                    ).using(SizeTransform(clip = false))
                }
            }, label = "CalendarViewTransition") { targetMode ->
                when (targetMode) {
                    CalendarViewMode.MONTH -> {
                        MonthCalendarContent(
                            currentYearMonth = currentYearMonth,
                            selectedDate = selectedDate,
                            allEvents = allEvents,
                            onDateSelected = { date ->
                                viewModel.onDateSelected(date)
                                viewMode = CalendarViewMode.DAILY
                            },
                            onMonthChanged = { newMonth ->
                                viewModel.onMonthChanged(newMonth)
                            }
                        )
                    }

                    CalendarViewMode.DAILY -> {
                        DailyTimelineContent(
                            selectedDate = selectedDate,
                            events = eventsForSelectedDate,
                            onEventClick = { event ->
                                selectedEventForEdit = event
                                showBottomSheet = true
                            },
                            onDeleteEvent = { event ->
                                viewModel.deleteEvent(event)
                            }
                        )
                    }
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                    selectedEventForEdit = null
                },
                sheetState = sheetState,
                modifier = Modifier.fillMaxWidth()
            ) {
                EventEditorSheet(
                    selectedDate = selectedDate,
                    eventToEdit = selectedEventForEdit,
                    onSaveClick = { event ->
                        if (selectedEventForEdit == null) {
                            viewModel.insertEvent(event)
                        } else {
                            viewModel.updateEvent(event)
                        }
                        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                                selectedEventForEdit = null
                            }
                        }
                    },
                    onDismiss = {
                        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                                selectedEventForEdit = null
                            }
                        }
                    }
                )
            }
        }

        if (showAutostartDialog) {
            AutostartDialog(
                onOpenSettings = {
                    AutostartHelper.openAutostartOrBatterySettings(context)
                    AutostartHelper.setDontShowAgain(context, true)
                    showAutostartDialog = false
                },
                onDismiss = {
                    AutostartHelper.setDontShowAgain(context, true)
                    showAutostartDialog = false
                }
            )
        }
    }

}

@Preview
@Composable
private fun CalendarScreenPrev() {
    PlannerTheme {
        CalendarScreen(
            viewModel = TODO(),
            onBackClick = { }
        )

    }
}