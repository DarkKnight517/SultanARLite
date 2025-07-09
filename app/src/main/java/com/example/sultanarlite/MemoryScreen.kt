package com.example.sultanarlite

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MemoryScreen(controller: AltairUIController) {
    val uiState by controller.uiState.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("🧠 История команд", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.commandHistory.isEmpty()) {
            Text("Нет команд", style = MaterialTheme.typography.bodyLarge)
        } else {
            uiState.commandHistory.reversed().forEachIndexed { index, command ->
                Text("[$index] $command")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("🔔 Уведомления", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.notifications.isEmpty()) {
            Text("Нет уведомлений", style = MaterialTheme.typography.bodyLarge)
        } else {
            uiState.notifications.reversed().forEach {
                Text("• $it")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            controller.handleCommand("очистить экран")
        }) {
            Text("Очистить экран")
        }
    }
}
