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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import com.renz.planner.data.calendar.CalendarEvent
import java.time.LocalDate
import java.util.UUID

@Composable
fun EventEditorSheet(
    selectedDate: LocalDate,
    onSaveClick: (CalendarEvent) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("09:00") }
    var endTime by remember { mutableStateOf("10:00") }

    val colorOptions = listOf(
        0xFF3F51B5, // Royal Blue
        0xFF4CAF50, // Forest Green
        0xFFFF9800, // Amber/Orange
        0xFFE91E63, // Pink/Red
        0xFF9C27B0  // Purple
    )
    var selectedColor by remember { mutableLongStateOf(colorOptions[0]) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Tambah Event Baru",
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
                onValueChange = { startTime = it },
                label = { Text("Jam Mulai (HH:mm)") },
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = endTime,
                onValueChange = { endTime = it },
                label = { Text("Jam Selesai (HH:mm)") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Warna Kategori",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

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
                            id = UUID.randomUUID().toString(),
                            title = title,
                            date = selectedDate.toString(), // YYYY-MM-DD
                            startTime = startTime,
                            endTime = endTime,
                            colorHex = selectedColor
                        )
                        onSaveClick(newEvent)
                    }
                }
            ) {
                Text("Simpan Event")
            }
        }
    }
}