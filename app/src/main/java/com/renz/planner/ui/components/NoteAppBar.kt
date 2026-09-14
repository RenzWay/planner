package com.renz.planner.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.renz.planner.ui.theme.PlannerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteAppBar(
    onBackClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
        Text(
            text = "Notes", style = MaterialTheme.typography.titleLarge
        )
    }, navigationIcon = {
        IconButton(onClick = { onBackClick() }) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
    }, actions = {
        IconButton(onClick = onSearchClick) {
            Icon(
                imageVector = Icons.Default.Search, contentDescription = "Search note"
            )
        }
        IconButton(onClick = onMenuClick) {
            Icon(
                imageVector = Icons.Default.MoreVert, contentDescription = "Menu options"
            )
        }
    }, colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ), modifier = modifier
    )
}

@Preview
@Composable
private fun NoteAppBarPreview() {
    PlannerTheme {
        NoteAppBar()
    }
}