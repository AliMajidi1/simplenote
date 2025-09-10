package com.example.simplenote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.simplenote.ui.theme.NoteCardPeach
import com.example.simplenote.ui.theme.NoteCardRose
import com.example.simplenote.ui.theme.NoteCardYellow
import com.example.simplenote.ui.theme.Typography

// UI model for notes
// You should define NoteUiModel elsewhere with title, description, and colorType
enum class NoteCardColorType { YELLOW, PEACH, ROSE }
data class NoteUiModel(val title: String, val description: String, val colorType: NoteCardColorType)

@Composable
fun NoteCard(note: NoteUiModel, onClick: () -> Unit) {
    val bgColor = when (note.colorType) {
        NoteCardColorType.YELLOW -> NoteCardYellow
        NoteCardColorType.PEACH -> NoteCardPeach
        NoteCardColorType.ROSE -> NoteCardRose
    }
    Box(
        modifier = Modifier
            .width(180.dp)
            .height(120.dp)
            .background(bgColor, shape = RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = note.title,
                style = Typography.titleMedium,
                maxLines = 2
            )
            Text(
                text = note.description,
                style = Typography.bodySmall,
                maxLines = 3
            )
        }
    }
}

