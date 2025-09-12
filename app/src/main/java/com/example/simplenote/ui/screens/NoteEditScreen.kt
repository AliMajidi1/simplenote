package com.example.simplenote.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simplenote.R
import com.example.simplenote.ui.components.PrimaryPillButton
import org.koin.androidx.compose.koinViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
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
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf<String?>(null) }
    val showAiDialog by viewModel.showAiDialog.collectAsState()
    val aiPrompt by viewModel.aiPrompt.collectAsState()
    val aiLoading by viewModel.aiLoading.collectAsState()
    val aiError by viewModel.aiError.collectAsState()
    val aiResult by viewModel.aiResult.collectAsState()
    val apiKey = "AIzaSyBjuweXz5gBkQARDwosDipcQuhBIE02rQY"

    fun handleBack() {
        if (uiState !is NoteEditUiState.Loading && uiState !is NoteEditUiState.Deleting) {
            viewModel.saveNote(
                onSuccess = onNoteSaved,
                onError = { showError = it }
            )
        }
    }

    LaunchedEffect(noteId) {
        viewModel.loadNote(noteId)
    }

    if (showError != null) {
        AlertDialog(
            onDismissRequest = { showError = null },
            confirmButton = {
                TextButton(onClick = { showError = null }) { Text("OK") }
            },
            title = { Text("Error", color = Color.Black) },
            text = { Text(showError ?: "", color = Color.Black) },
        )
    }

    if (showDeleteDialog) {
        ModalBottomSheet(
            onDismissRequest = { showDeleteDialog = false },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Want to Delete this Note?",
                        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF191932)),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { showDeleteDialog = false }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = "Close",
                            tint = Color(0xFFB3B0C6)
                        )
                    }
                }
                HorizontalDivider(thickness = 1.dp, color = Color(0xFFE5E5EA))
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showDeleteDialog = false
                            viewModel.deleteNote(
                                onSuccess = onNoteDeleted,
                                onError = { showError = it }
                            )
                        }
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = "Delete Note",
                        tint = Color(0xFFFF3B30),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Delete Note",
                        color = Color(0xFFFF3B30),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(56.dp)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF6C4EE6),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { handleBack() }
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Back",
                    color = Color(0xFF6C4EE6),
                    fontSize = 16.sp,
                    modifier = Modifier.clickable { handleBack() }
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
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
                        .weight(1f),
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
            HorizontalDivider(thickness = 1.dp, color = Color(0xFFE5E5EA))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Last edited on " + (lastEdited?.let {
                        try {
                            val dt = java.time.OffsetDateTime.parse(it)
                            dt.toLocalTime().format(DateTimeFormatter.ofPattern("HH.mm"))
                        } catch (e: Exception) {
                            "-"
                        }
                    } ?: "-"),
                    color = Color(0xFF191932),
                    fontSize = 16.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 24.dp)
                )
                if (noteId != null) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFF6C00E6))
                            .clickable { showDeleteDialog = true },
                        contentAlignment = Alignment.Center
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
                            .background(Color(0xFFFFFFFF).copy(alpha = 0.7f)),
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
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 24.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = Color(0xFF6C4EE6),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .size(56.dp)
                    .clickable { viewModel.setShowAiDialog(true) }
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("AI", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        }
        if (showAiDialog) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.setShowAiDialog(false); viewModel.clearAiError(); viewModel.clearAiResult() },
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                containerColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("AI Drafting", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF191932))
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = aiPrompt,
                        onValueChange = viewModel::onAiPromptChange,
                        placeholder = { Text("Enter your prompt (e.g. Summarize, Motivate...)") },
                        singleLine = false,
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4,
                        textStyle = TextStyle(color = Color.Black, fontSize = 16.sp)
                    )
                    Spacer(Modifier.height(12.dp))
                    if (aiLoading) {
                        CircularProgressIndicator(color = Color(0xFF6C4EE6))
                    } else {
                        PrimaryPillButton(
                            text = if (aiResult == null) "Generate" else "Apply to Note",
                            onClick = {
                                if (aiResult == null) {
                                    viewModel.requestAiDraft(apiKey)
                                } else {
                                    viewModel.applyAiDraft()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    if (aiError != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(aiError ?: "", color = Color.Red, fontSize = 14.sp)
                        TextButton(onClick = { viewModel.requestAiDraft(apiKey) }) {
                            Text("Retry", color = Color(0xFF6C4EE6))
                        }
                    }
                    if (aiResult != null) {
                        Spacer(Modifier.height(8.dp))
                        Text("AI Suggestion:", fontWeight = FontWeight.Medium, fontSize = 16.sp)
                        Text(aiResult ?: "", color = Color(0xFF191932), fontSize = 15.sp)
                    }
                }
            }
        }
    }
    BackHandler(enabled = true) {
        handleBack()
    }
}
