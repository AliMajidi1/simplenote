package com.example.simplenote.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.simplenote.R
import com.example.simplenote.ui.theme.*

@Composable
fun HomeScreen(
    notes: List<NoteUiModel>,
    onFabClick: () -> Unit,
    onNoteClick: (NoteUiModel) -> Unit,
    onSearch: (String) -> Unit,
    searchQuery: String,
    onClearSearch: () -> Unit,
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        if (notes.isEmpty()) {
            EmptyState(onFabClick, selectedTab, onTabSelected)
        } else {
            NotesListState(
                notes = notes,
                onFabClick = onFabClick,
                onNoteClick = onNoteClick,
                searchQuery = searchQuery,
                onSearch = onSearch,
                onClearSearch = onClearSearch,
                selectedTab = selectedTab,
                onTabSelected = onTabSelected
            )
        }
    }
}

@Composable
private fun EmptyState(
    onFabClick: () -> Unit,
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 96.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.start_your_journey),
            contentDescription = "Start Your Journey Illustration",
            modifier = Modifier.size(220.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Start Your Journey",
            style = Typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Create your first note and let your ideas flow.",
            style = Typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        // Arrow illustration can be added here if available
    }
    BottomNavBarWithFab(
        onFabClick = onFabClick,
        selectedTab = selectedTab,
        onTabSelected = onTabSelected
    )
}

@Composable
private fun NotesListState(
    notes: List<NoteUiModel>,
    onFabClick: () -> Unit,
    onNoteClick: (NoteUiModel) -> Unit,
    searchQuery: String,
    onSearch: (String) -> Unit,
    onClearSearch: () -> Unit,
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        StatusBar()
        SearchBar(
            query = searchQuery,
            onQueryChange = onSearch,
            onClear = onClearSearch
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Interesting Idea",
            style = Typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(start = 16.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(notes.size) { idx ->
                NoteCard(note = notes[idx], onClick = { onNoteClick(notes[idx]) })
            }
        }
    }
    BottomNavBarWithFab(
        onFabClick = onFabClick,
        selectedTab = selectedTab,
        onTabSelected = onTabSelected
    )
}

// Placeholder composables for StatusBar, SearchBar, NoteCard, BottomNavBarWithFab
// These will be implemented in their respective files
