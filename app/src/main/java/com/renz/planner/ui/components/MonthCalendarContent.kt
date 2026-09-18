package com.renz.planner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.renz.planner.data.calendar.CalendarEvent
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun MonthCalendarContent(
    currentYearMonth: YearMonth,
    selectedDate: LocalDate,
    allEvents: List<CalendarEvent>,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChanged: (YearMonth) -> Unit
) {
    val groupedEvents = remember(allEvents) {
        allEvents.groupBy { it.date }
    }

    val today = remember { LocalDate.now() }

    val dayNames = listOf("M", "T", "W", "T", "F", "S", "S")

    val pageCount = 2400
    val initialPage = 1200
    val pagerState = rememberPagerState(initialPage = initialPage) { pageCount }

    // Initial sync to set the pager to the correct month on first load
    LaunchedEffect(Unit) {
        val targetPage = initialPage +
                ((currentYearMonth.year - YearMonth.now().year) * 12) +
                (currentYearMonth.monthValue - YearMonth.now().monthValue)
        pagerState.scrollToPage(targetPage)
    }

    // Sync Pager when currentYearMonth changes from OUTSIDE (e.g. Buttons)
    LaunchedEffect(currentYearMonth) {
        val targetPage = initialPage +
                ((currentYearMonth.year - YearMonth.now().year) * 12) +
                (currentYearMonth.monthValue - YearMonth.now().monthValue)

        if (pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    // Sync ViewModel when Pager is swiped by USER
    LaunchedEffect(pagerState.currentPage) {
        val diff = pagerState.currentPage - initialPage
        val newMonth = YearMonth.now().plusMonths(diff.toLong())

        if (newMonth != currentYearMonth) {
            onMonthChanged(newMonth)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            dayNames.forEach { dayname ->
                Text(
                    text = dayname,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { pageIndex ->
            // Optimization: Calculate month data once per page and remember it
            val monthData = remember(pageIndex) {
                val diff = pageIndex - initialPage
                val month = YearMonth.now().plusMonths(diff.toLong())
                val firstDay = month.atDay(1).dayOfWeek.value
                val empty = firstDay - 1
                Triple(month, month.lengthOfMonth(), empty)
            }

            val targetYearMonth = monthData.first
            val dayInMonth = monthData.second
            val emptySlots = monthData.third

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                userScrollEnabled = false
            ) {
                items(emptySlots) {
                    Spacer(modifier = Modifier.height(80.dp))
                }

                items(dayInMonth) { dayIndex ->
                    val dayNumber = dayIndex + 1
                    val date = targetYearMonth.atDay(dayNumber)
                    val isToday = date == today
                    val isSelected = date == selectedDate

                    val dayEvents = groupedEvents[date.toString()] ?: emptyList()

                    Box(
                        modifier = Modifier
                            .height(85.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                when {
                                    isSelected -> MaterialTheme.colorScheme.primaryContainer
                                    isToday -> MaterialTheme.colorScheme.surfaceVariant
                                    else -> Color.Transparent
                                }
                            )
                            .clickable { onDateSelected(date) }
                            .padding(4.dp)) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = dayNumber.toString(),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            dayEvents.take(2).forEach { event ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 1.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(event.colorHex))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = event.title,
                                        fontSize = 9.sp,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}
