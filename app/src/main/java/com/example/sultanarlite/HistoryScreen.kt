package com.example.sultanarlite

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState


@Composable
fun HistoryScreen(controller: AltairUIController) {
    val state = controller.uiState.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("📚 История уведомлений", style = MaterialTheme.typography.headlineMedium)

        if (state.notifications.isEmpty()) {
            Text("Пока нет уведомлений", style = MaterialTheme.typography.bodyMedium)
        } else {
            state.notifications.reversed().forEachIndexed { index, note ->
                Text("🔔 [$index] $note", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("📝 Все тексты:", style = MaterialTheme.typography.titleMedium)
        state.textElements.reversed().forEachIndexed { index, text ->
            Text("[$index] $text", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
