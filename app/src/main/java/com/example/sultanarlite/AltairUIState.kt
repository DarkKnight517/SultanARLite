package com.example.sultanarlite

import androidx.compose.ui.graphics.Color
import com.example.sultanarlite.model.CommandLog

data class TextElement(
    val text: String,
    val color: Color = Color.White,
    val fontSize: Int = 16
)

data class NotificationElement(
    val text: String,
    val color: Color = Color(0xFFFFD700),
    val fontSize: Int = 16
)

data class AltairUIState(
    val internetEnabled: Boolean = false,
    val lastInternetResult: String = "",
    val currentGoal: String = "",
    val textElements: List<TextElement> = emptyList(),
    val notifications: List<NotificationElement> = emptyList(),
    val commandHistory: List<CommandLog> = emptyList(),
    val backgroundColor: Color = Color(0xFF1A0D0B)
)
