package com.example.sultanarlite

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun VoiceScreen(viewModel: MainViewModel, altairController: AltairUIController) {
    var textToSpeak by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("🎙️ Голосовой интерфейс", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = textToSpeak,
            onValueChange = { textToSpeak = it },
            label = { Text("Введите команду или текст") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    if (textToSpeak.isNotBlank()) {
                        altairController.interpret(textToSpeak)
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("✅ Выполнить")
            }

            Button(
                onClick = {
                    if (textToSpeak.isNotBlank()) {
                        altairController.speak(textToSpeak)
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("📢 Озвучить")
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    altairController.handleCommand("добавь текст $textToSpeak")
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("➕ Добавить текст")
            }

            Button(
                onClick = {
                    altairController.handleCommand("показать уведомление $textToSpeak")
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("🔔 Уведомление")
            }

            Button(
                onClick = {
                    altairController.handleCommand("очистить экран")
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("🧹 Очистить")
            }
        }
    }
}
