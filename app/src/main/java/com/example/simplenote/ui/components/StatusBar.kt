package com.example.simplenote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.simplenote.ui.theme.NeutralWhite
import com.example.simplenote.ui.theme.Typography

@Composable
fun StatusBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .background(NeutralWhite),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "09:41", // Static for design; replace with dynamic time if needed
            style = Typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
        )
        Spacer(modifier = Modifier.width(16.dp))
        // Battery/Wifi icons can be added here if available
    }
}

