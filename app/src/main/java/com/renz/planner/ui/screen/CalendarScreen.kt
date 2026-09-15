package com.renz.planner.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.renz.planner.ui.theme.PlannerTheme
import com.renz.planner.ui.viewmodel.CalendarViewModel

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {

}

@Preview
@Composable
private fun CalendarScreenPrev() {
    PlannerTheme {
        CalendarScreen(
            viewModel = TODO(),
            modifier = Modifier,
            onBackClick = { }
        )

    }
}