package com.renz.planner.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.renz.planner.data.calendar.CalendarViewMode
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarTopBar(
    currentYearMonth: YearMonth,
    viewMode: CalendarViewMode,
    onBackClick: () -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onToggleViewMode: () -> Unit,
    onOpenAutostartInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = currentYearMonth.format(
                        DateTimeFormatter.ofPattern("MMM yyyy", Locale("id", "ID"))
                    ),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
            }
        },
        actions = {
            IconButton(onClick = onOpenAutostartInfo) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Izin Autostart & Baterai"
                )
            }
            IconButton(onClick = onPreviousMonth) {
                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Bulan Lalu")
            }
            IconButton(onClick = onNextMonth) {
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Bulan Depan")
            }
            // 🌟 Switch Mode View (Month <-> Daily) dengan Ikon
            IconButton(onClick = onToggleViewMode) {
                Icon(
                    imageVector = if (viewMode == CalendarViewMode.MONTH) Icons.Default.FormatListBulleted else Icons.Default.DateRange,
                    contentDescription = "Ganti View Mode"
                )
            }
        },
        modifier = modifier
    )
}