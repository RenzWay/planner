package com.renz.planner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.renz.planner.data.calendar.CalendarEvent
import java.time.LocalDate
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventEditorSheet(
    selectedDate: LocalDate,
    eventToEdit: CalendarEvent? = null,
    onSaveClick: (CalendarEvent) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(eventToEdit?.title ?: "") }
    var startTime by remember { mutableStateOf(eventToEdit?.startTime ?: "09:00") }
    var endTime by remember { mutableStateOf(eventToEdit?.endTime ?: "10:00") }
    var reminderMinutes by remember { mutableStateOf(eventToEdit?.reminderMinutesBefore?.toString() ?: "15") }
    var showTimePicker by remember { mutableStateOf(false) }
    var pickingStartTime by remember { mutableStateOf(true) }

    val timePickerState = rememberTimePickerState(
        initialHour = if (pickingStartTime) {
            startTime.split(":")[0].toIntOrNull() ?: 9
        } else {
            endTime.split(":")[0].toIntOrNull() ?: 10
        },
        initialMinute = if (pickingStartTime) {
            startTime.split(":")[1].toIntOrNull() ?: 0
        } else {
            endTime.split(":")[1].toIntOrNull() ?: 0
        },
        is24Hour = true
    )


    val colorOptions = listOf(
        0xFF3F51B5, // Royal Blue
        0xFF4CAF50, // Forest Green
        0xFFFF9800, // Amber/Orange
        0xFFE91E63, // Pink/Red
        0xFF9C27B0  // Purple
    )
    var selectedColor by remember { mutableLongStateOf(eventToEdit?.colorHex ?: colorOptions[0]) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = if (eventToEdit == null) "Tambah Event Baru" else "Edit Event",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Judul Acara") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Input Jam Mulai & Selesai Simple
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = startTime,
                onValueChange = {},
                label = { Text("Jam Mulai") },
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        pickingStartTime = true
                        showTimePicker = true
                    },
                enabled = false, // Biar gak bisa diketik manual
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            OutlinedTextField(
                value = endTime,
                onValueChange = {},
                label = { Text("Jam Selesai") },
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        pickingStartTime = false
                        showTimePicker = true
                    },
                enabled = false, // Biar gak bisa diketik manual
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Warna Kategori",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = reminderMinutes,
            onValueChange = { reminderMinutes = it.filter { char -> char.isDigit() } },
            label = { Text("Pengingat (menit sebelum)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Color Picker Bulat-bulat
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            colorOptions.forEach { colorHex ->
                val color = Color(colorHex)
                val isSelected = selectedColor == colorHex

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = if (isSelected) 3.dp else 0.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { selectedColor = colorHex }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val newEvent = CalendarEvent(
                            id = eventToEdit?.id ?: UUID.randomUUID().toString(),
                            title = title,
                            date = selectedDate.toString(), // YYYY-MM-DD
                            startTime = startTime,
                            endTime = endTime,
                            colorHex = selectedColor,
                            reminderMinutesBefore = reminderMinutes.toIntOrNull() ?: 15,
                            isCompleted = eventToEdit?.isCompleted ?: false
                        )
                        onSaveClick(newEvent)
                    }
                }
            ) {
                Text(if (eventToEdit == null) "Simpan Event" else "Perbarui Event")
            }
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    // Sekitar baris 178
                    val formattedTime = String.format(
                        java.util.Locale.getDefault(),
                        "%02d:%02d",
                        timePickerState.hour,
                        timePickerState.minute
                    )
                    if (pickingStartTime) startTime = formattedTime else endTime = formattedTime
                    showTimePicker = false
                }) { Text("OK") }
            },
            title = { Text("Pilih Waktu") }
        ) {
            TimePicker(state = timePickerState)
        }
    }
}

@Composable
fun TimePickerDialog(
    title: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        title = title,
        text = { content() }
    )
}