package com.renz.planner.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.renz.planner.ui.theme.PlannerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteAppBar(
    onBackClick: () -> Unit = {},
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit = {},
    onMenuClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isSearchMode by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    TopAppBar(
        title = {
            // ANIMASI DI SINI
            AnimatedContent(
                targetState = isSearchMode,
                transitionSpec = {
                    if (targetState) {
                        // Animasi pas buka search: slide masuk dari kanan
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width } + fadeOut()
                        )
                    } else {
                        // Animasi pas tutup search: slide masuk dari kiri
                        (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> width } + fadeOut()
                        )
                    }
                },
                label = "SearchAnimation"
            ) { active ->
                if (active) {
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp) // Lebih slim lagi tapi aman
                            .background(
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f), // Agak transparan biar nyatu
                                shape = CircleShape
                            )
                            .focusRequester(focusRequester),
                        singleLine = true,
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        decorationBox = { innerTextField ->
                            // Box ini yang bikin teksnya beneran di tengah secara vertikal
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "Cari catatan...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                            alpha = 0.6f
                                        )
                                    )
                                }
                                innerTextField() // Ini tempat teks aslinya muncul
                            }
                        }
                    )
                    // Auto fokus pas search mode nyala
                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                    }
                } else {
                    Text(text = "Notes", style = MaterialTheme.typography.titleLarge)
                }
            }
        }, navigationIcon = {
            IconButton(onClick = {
                if (isSearchMode) {
                    isSearchMode = false
                    onSearchQueryChange("")
                } else {
                    onBackClick()
                }
            }) {
                Icon(
                    imageVector = if (isSearchMode) Icons.Default.Close else Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Action"
                )
            }
        }, actions = {
            if (!isSearchMode) {
                IconButton(onClick = { isSearchMode = true }) {
                    Icon(
                        imageVector = Icons.Default.Search, contentDescription = "Search note"
                    )
                }
            }
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.MoreVert, contentDescription = "Menu options"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ), modifier = modifier
    )
}

@Preview
@Composable
private fun NoteAppBarPreview() {
    PlannerTheme {
        NoteAppBar(
            searchQuery = "",
            onSearchQueryChange = {},
            onBackClick = {}
        )
    }
}