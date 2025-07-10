package com.example.sultanarlite

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import com.example.sultanarlite.model.CommandType

@Composable
fun AltairLabScreen(controller: AltairUIController) {
    var jsonInput by remember { mutableStateOf("") }
    var resultMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val uiState by controller.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Лаборатория ИИ Альтаира", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = jsonInput,
            onValueChange = { jsonInput = it },
            label = { Text("JSON-команда") },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            maxLines = 10,
            placeholder = {
                Text("{\n  \"command\": \"add_text\",\n  \"args\": { \"text\": \"Пример\" }\n}")
            }
        )

        Spacer(Modifier.height(12.dp))

        Button(onClick = {
            try {
                controller.executeJsonCommand(jsonInput)
                resultMessage = "✅ Команда выполнена"
            } catch (e: Exception) {
                resultMessage = "❌ Ошибка: ${e.message}"
            }
        }) {
            Text("Выполнить JSON")
        }

        resultMessage?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = if (it.startsWith("✅")) Color.Green else Color.Red)
        }

        Spacer(Modifier.height(24.dp))

        Text("История JSON-команд:", style = MaterialTheme.typography.titleMedium)
        uiState.commandHistory
            .filter { it.type == CommandType.JSON } // ✅ правильно
            .reversed()
            .take(5)
            .forEach {
                Text(it.message.removePrefix("[json] "), style = MaterialTheme.typography.bodySmall)
            }

    }
}
