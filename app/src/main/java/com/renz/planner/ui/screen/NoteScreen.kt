package com.renz.planner.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.renz.planner.data.notes.Notes
import com.renz.planner.ui.components.AllNotesContent
import com.renz.planner.ui.components.NoteAppBar
import com.renz.planner.ui.components.NoteEditorContent
import com.renz.planner.ui.viewmodel.NotesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(viewModel: NotesViewModel, modifier: Modifier = Modifier) {
    val tabs = listOf("All", "Favorite")

    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    val allNotes by viewModel.notesList.collectAsStateWithLifecycle()
    val favoriteNotes by viewModel.favoriteNotesList.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var showBottomSheet by remember { mutableStateOf(false) }
    var noteToEdit by remember { mutableStateOf<Notes?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val onFavoriteToggle: (Notes) -> Unit = { note ->
        viewModel.updateNote(note.copy(isFavorite = !note.isFavorite))
    }

    val onDeleteNote: (Notes) -> Unit = { note ->
        viewModel.deleteNote(note)
    }

    val openEditorForEdit: (Notes) -> Unit = { note ->
        noteToEdit = note
        showBottomSheet = true
    }

    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        topBar = { NoteAppBar() },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                noteToEdit = null
                showBottomSheet = true
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add notes")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                TabRow(selectedTabIndex = pagerState.currentPage) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = { Text(text = title) }
                        )
                    }
                }

                HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
                    when (page) {
                        0 -> AllNotesContent(
                            notes = allNotes,
                            onFavoriteToggle = onFavoriteToggle,
                            onDeleteNote = onDeleteNote,
                            onNoteClick = openEditorForEdit
                        )

                        1 -> FavoriteNotesContent(
                            notes = favoriteNotes,
                            onFavoriteToggle = onFavoriteToggle,
                            onDeleteNote = onDeleteNote,
                            onNoteClick = openEditorForEdit
                        )
                    }
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = sheetState,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    NoteEditorContent(
                        noteToEdit = noteToEdit,
                        onSaveClick = { note ->
                            viewModel.insertNote(note)
                            coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) showBottomSheet = false
                            }
                        },
                        onDismiss = {
                            coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) showBottomSheet = false
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteNotesContent(
    notes: List<Notes>, onFavoriteToggle: (Notes) -> Unit,
    onDeleteNote: (Notes) -> Unit,
    onNoteClick: (Notes) -> Unit
) {
    AllNotesContent(
        notes = notes,
        onFavoriteToggle = onFavoriteToggle,
        onDeleteNote = onDeleteNote,
        onNoteClick = onNoteClick
    )
}