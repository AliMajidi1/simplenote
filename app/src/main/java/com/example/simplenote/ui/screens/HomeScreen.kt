package com.example.simplenote.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simplenote.R
import com.example.simplenote.data.model.Note
import com.example.simplenote.ui.theme.NeutralBlack
import com.example.simplenote.ui.theme.NeutralWhite
import com.example.simplenote.ui.theme.Primary
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onAddNote: () -> Unit = {},
    onNoteClick: (Note) -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F6FB))) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(bottom = 56.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = null,
                    tint = Color(0xFFB3B0C6),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    placeholder = { Text("Search...", color = Color(0xFFB3B0C6)) },
                    textStyle = LocalTextStyle.current.copy(color = NeutralBlack),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF3F2F7),
                        focusedContainerColor = Color(0xFFF3F2F7),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 48.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = { viewModel.onSearchQueryChange(searchQuery) }
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            when (uiState) {
                is HomeUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary)
                    }
                }
                is HomeUiState.Empty -> {
                    if (searchQuery.isBlank()) {
                        EmptyHomeContent(onAddNote = onAddNote)
                    } else {
                        NoSearchResultsContent()
                    }
                }
                is HomeUiState.Success -> {
                    val state = uiState as HomeUiState.Success
                    val notes = state.notes
                    NotesHomeContent(
                        notes = notes,
                        onNoteClick = onNoteClick,
                        isLoadingMore = state.isLoadingMore,
                        endReached = state.endReached,
                        onLoadMore = { viewModel.loadNextPage() }
                    )
                }
                is HomeUiState.Error -> {
                    val message = (uiState as HomeUiState.Error).message
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = message, color = Color.Red)
                    }
                }
            }
        }
        HomeBottomNav(
            onHomeClick = onHomeClick,
            onSettingsClick = onSettingsClick
        )
        HomeFab(
            onClick = onAddNote,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-40).dp)
        )
    }
}

@Composable
fun NoSearchResultsContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = null,
            tint = Color(0xFFB3B0C6),
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "No notes found",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = NeutralBlack,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Try a different search keyword.",
            fontSize = 16.sp,
            color = Color(0xFFB3B0C6),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
fun EmptyHomeContent(onAddNote: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(bottom = 56.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.home_illustration),
            contentDescription = null,
            modifier = Modifier.size(220.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Start Your Journey",
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = NeutralBlack,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Every big step start with small step.\nNotes your first idea and start your journey!",
            fontSize = 16.sp,
            color = Color(0xFFB3B0C6),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
fun NotesHomeContent(
    notes: List<Note>,
    onNoteClick: (Note) -> Unit,
    isLoadingMore: Boolean = false,
    endReached: Boolean = false,
    onLoadMore: () -> Unit = {}
) {
    val gridState = androidx.compose.foundation.lazy.grid.rememberLazyGridState()
    val shouldLoadMore =
        !endReached && !isLoadingMore && gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == notes.lastIndex
    androidx.compose.runtime.LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) onLoadMore()
    }
    Column(modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .padding(bottom = 56.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Notes",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = NeutralBlack,
            modifier = Modifier.padding(start = 20.dp, bottom = 8.dp)
        )
        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(notes) { note ->
                NoteCard(note = note, onClick = { onNoteClick(note) })
            }
            if (isLoadingMore) {
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Primary, strokeWidth = 2.dp, modifier = Modifier.size(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun NoteCard(note: Note, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7D6)),
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = note.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NeutralBlack
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = note.description,
                fontSize = 14.sp,
                color = Color(0xFF6B6B6B),
                maxLines = 4
            )
        }
    }
}

@Composable
fun HomeFab(onClick: () -> Unit, modifier: Modifier = Modifier) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = Primary,
        shape = CircleShape,
        modifier = modifier
            .size(64.dp)
            .shadow(8.dp, CircleShape)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_add),
            contentDescription = "Add Note",
            tint = NeutralWhite,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
fun HomeBottomNav(onHomeClick: () -> Unit, onSettingsClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(NeutralWhite)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onHomeClick() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_home),
                    contentDescription = "Home",
                    tint = Primary,
                    modifier = Modifier.size(28.dp)
                )
                Text("Home", color = Primary, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.width(160.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onSettingsClick() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_settings),
                    contentDescription = "Settings",
                    tint = Color(0xFFB3B0C6),
                    modifier = Modifier.size(28.dp)
                )
                Text("Settings", color = Color(0xFFB3B0C6), fontSize = 12.sp)
            }
        }
    }
}