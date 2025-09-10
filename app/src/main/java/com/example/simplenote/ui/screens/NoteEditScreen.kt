package com.example.simplenote.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simplenote.R
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun NoteEditScreen(
    noteId: Int?,
    onBack: () -> Unit,
    onNoteDeleted: () -> Unit,
    onNoteSaved: () -> Unit,
    viewModel: NoteEditViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val title by viewModel.title.collectAsState()
    val description by viewModel.description.collectAsState()
    val lastEdited by viewModel.lastEdited.collectAsState()
    val focusManager = LocalFocusManager.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(noteId) {
        viewModel.loadNote(noteId)
    }

    if (showError != null) {
        AlertDialog(
            onDismissRequest = { showError = null },
            confirmButton = {
                TextButton(onClick = { showError = null }) { Text("OK") }
            },
            title = { Text("Error") },
            text = { Text(showError ?: "") }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.deleteNote(
                        onSuccess = onNoteDeleted,
                        onError = { showError = it }
                    )
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            },
            title = { Text("Delete Note") },
            text = { Text("Are you sure you want to delete this note?") }
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 12.dp, end = 12.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF6C4EE6),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onBack() }
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Back",
                    color = Color(0xFF6C4EE6),
                    fontSize = 16.sp,
                    modifier = Modifier.clickable { onBack() }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = title,
                onValueChange = viewModel::onTitleChange,
                textStyle = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF191932)),
                placeholder = { Text("Title", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF191932)) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = viewModel::onDescriptionChange,
                textStyle = TextStyle(fontSize = 16.sp, color = Color(0xFFB3B0C6)),
                placeholder = { Text("Feel Free to Write Here...", color = Color(0xFFB3B0C6)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                maxLines = 20
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(bottom = 8.dp, start = 12.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (lastEdited != null) {
                val formatted = try {
                    val dt = LocalDateTime.parse(lastEdited)
                    dt.format(DateTimeFormatter.ofPattern("HH:mm"))
                } catch (e: Exception) {
                    lastEdited
                }
                Text(
                    text = "Last edited on ${formatted ?: "-"}",
                    color = Color(0xFFB3B0C6),
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f)
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
            if (noteId != null) {
                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF6C4EE6), shape = RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = "Delete Note",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
        when (uiState) {
            is NoteEditUiState.Loading, is NoteEditUiState.Saving, is NoteEditUiState.Deleting -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF6C4EE6))
                }
            }
            is NoteEditUiState.Error -> {
                val msg = (uiState as NoteEditUiState.Error).message
                LaunchedEffect(msg) { showError = msg }
            }
            else -> {}
        }
    }
    BackHandler(enabled = true) {
        if (uiState !is NoteEditUiState.Loading && uiState !is NoteEditUiState.Deleting) {
            viewModel.saveNote(
                onSuccess = onNoteSaved,
                onError = { showError = it }
            )
        }
    }
}
