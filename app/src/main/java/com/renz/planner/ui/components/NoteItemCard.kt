package com.renz.planner.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.richeditor.annotation.ExperimentalRichTextApi
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import com.renz.planner.data.notes.Notes
import com.renz.planner.ui.theme.PlannerTheme

@OptIn(ExperimentalRichTextApi::class)
@Composable
fun NoteItemCard(
    note: Notes,
    onFavoriteToggle: ((Notes) -> Unit)? = null,
    onNoteClick: () -> Unit
) {
    val richTextState = rememberRichTextState()

    val starColor by animateColorAsState(
        targetValue = if (note.isFavorite) Color(0xFFFFC107) else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "starColor"
    )

    LaunchedEffect(note.content) {
        richTextState.setHtml(note.content)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onNoteClick
    ) {
        Row(modifier = Modifier.padding(16.dp)) {

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(6.dp))

                RichText(
                    state = richTextState,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${note.createAt} • ${note.characters} huruf ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = { onFavoriteToggle?.invoke(note) },

                ) {
                Icon(
                    imageVector = if (note.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Favorite",
                    tint = starColor
                )
            }
        }
    }
}

@Preview
@Composable
private fun NoteItemCardPreview() {
    PlannerTheme {
        NoteItemCard(
            note = Notes(
                id = "1",
                title = "Hari ini",
                content = "Jika ku begini maka begitu",
                createAt = "12/12/2023",
                characters = 9,
                isFavorite = true,
            ),
            onFavoriteToggle = TODO(),
            onNoteClick = TODO()
        )
    }
}