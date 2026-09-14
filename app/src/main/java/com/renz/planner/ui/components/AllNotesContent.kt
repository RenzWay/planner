package com.renz.planner.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.renz.planner.data.notes.Notes

@Composable
fun AllNotesContent(
    notes: List<Notes>,
    onFavoriteToggle: (Notes) -> Unit,
    onDeleteNote: (Notes) -> Unit,
    onNoteClick: (Notes) -> Unit
) {
    if (notes.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Belum ada catatan")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = notes, key = { it.id }) { note ->
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = { dismissValue ->
                        when (dismissValue) {
                            SwipeToDismissBoxValue.StartToEnd -> {
                                onNoteClick(note)
                                false
                            }

                            SwipeToDismissBoxValue.EndToStart -> {
                                onDeleteNote(note)
                                true
                            }

                            else -> {
                                false
                            }
                        }
                    }
                )

                SwipeToDismissBox(state = dismissState, backgroundContent = {
                    val direction = dismissState.dismissDirection

                    val color by animateColorAsState(
                        targetValue = when (direction) {
                            SwipeToDismissBoxValue.StartToEnd -> Color(0xFF4CAF50)
                            SwipeToDismissBoxValue.EndToStart -> Color(0xFFE53935)
                            else -> Color.Transparent
                        },
                        label = "backgroundColor"
                    )

                    val alignment = when (dismissState.targetValue) {
                        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                        else -> Alignment.CenterEnd
                    }

                    val icon = when (direction) {
                        SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Edit
                        else -> Icons.Default.Delete
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color, shape = MaterialTheme.shapes.medium)
                            .padding(horizontal = 20.dp),
                        contentAlignment = alignment
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }) {
                    NoteItemCard(
                        note = note,
                        onFavoriteToggle = onFavoriteToggle,
                        onNoteClick = { onNoteClick(note) }
                    )
                }
            }
        }
    }
}