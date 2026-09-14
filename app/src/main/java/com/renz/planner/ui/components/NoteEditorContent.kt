package com.renz.planner.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.renz.planner.data.notes.Notes
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun NoteEditorContent(
    noteToEdit: Notes? = null,
    onSaveClick: (Notes) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    val richTextState = rememberRichTextState()

    LaunchedEffect(noteToEdit) {
        noteToEdit?.let {
            title = it.title
            richTextState.setHtml(it.content)
        }
    }

    val currentDate =
        remember { SimpleDateFormat("dd MM yyyy", Locale.getDefault()).format(Date()) }
    val liveCharCount by remember {
        derivedStateOf {
            richTextState.annotatedString.text.length
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(onClick = {
                val htmlContent = richTextState.toHtml()
                val plainText = richTextState.annotatedString.text

                if (title.isNotBlank() || plainText.isNotBlank()) {
                    val updatedOrNewNote = Notes(
                        id = noteToEdit?.id ?: UUID.randomUUID().toString(),
                        title = title,
                        content = htmlContent,
                        createAt = noteToEdit?.createAt ?: currentDate,
                        characters = liveCharCount,
                        isFavorite = noteToEdit?.isFavorite ?: false
                    )
                    onSaveClick(updatedOrNewNote)
                }
            }) {
                Text(if (noteToEdit != null) "Perbarui" else "Simpan")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "$currentDate  •  $liveCharCount huruf",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))
        // 2. Judul Catatan (Berada di BANYAK/DI BAWAH App Bar Modal)
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            placeholder = {
                Text(
                    "Judul Catatan",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Formatting Toolbar (Bold, Italic, List)
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { richTextState.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold)) }
            ) {
                Text("B", fontWeight = FontWeight.Bold)
            }

            IconButton(
                onClick = { richTextState.toggleSpanStyle(SpanStyle(fontStyle = FontStyle.Italic)) }
            ) {
                Text("I", fontStyle = FontStyle.Italic)
            }

            IconButton(
                onClick = { richTextState.toggleUnorderedList() }
            ) {
                Text("• List")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 4. Editor Konten Catatan
        RichTextEditor(
            state = richTextState,
            placeholder = { Text("Tulis ceritamu disini...") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}