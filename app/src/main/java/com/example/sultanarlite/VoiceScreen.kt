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
        Text("🔊 Озвучка текста", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = textToSpeak,
            onValueChange = { textToSpeak = it },
            label = { Text("Введите текст") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (textToSpeak.isNotBlank()) {
                    altairController.speak(textToSpeak)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("📢 Озвучить")
        }
    }
}
