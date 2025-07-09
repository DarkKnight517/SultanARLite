package com.example.sultanarlite

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DialogScreen(
    viewModel: MainViewModel,
    altairController: AltairUIController
) {
    val context = LocalContext.current
    var commandText by remember { mutableStateOf("") }
    val uiState by altairController.uiState.collectAsState()

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()

            spokenText?.let {
                commandText = it
                altairController.handleCommand(it)
                Toast.makeText(context, "📤 Команда отправлена", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Голосовой ИИ Альтаир", style = MaterialTheme.typography.headlineSmall)

        // Голосовой ввод
        Button(onClick = {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru-RU")
            speechLauncher.launch(intent)
        }) {
            Text("🎙️ Говори команду")
        }

        // Поле для ввода текста команды
        OutlinedTextField(
            value = commandText,
            onValueChange = { commandText = it },
            label = { Text("Введи команду...") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2,
            singleLine = false
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                if (commandText.isNotBlank()) {
                    altairController.handleCommand(commandText)
                    commandText = ""
                }
            }) {
                Text("Выполнить")
            }
            Button(onClick = {
                if (commandText.isNotBlank()) {
                    altairController.speak(commandText)
                }
            }) {
                Text("Озвучить")
            }
            Button(onClick = {
                altairController.handleCommand("очистить экран")
            }) {
                Text("Очистить")
            }
        }

        // Быстрые команды
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                altairController.handleCommand("добавь текст Пример текста")
            }) {
                Text("Добавить текст")
            }
            Button(onClick = {
                altairController.handleCommand("показать уведомление Тестовое уведомление")
            }) {
                Text("Показать уведомление")
            }
            Button(onClick = {
                altairController.handleCommand("поменяй фон")
            }) {
                Text("Сменить фон")
            }
        }

        Spacer(Modifier.height(8.dp))
        Divider()
        Spacer(Modifier.height(8.dp))

        // История команд (кроме JSON)
        Text("История команд:", style = MaterialTheme.typography.titleMedium)
        uiState.commandHistory
            .filter { it.type != com.example.sultanarlite.model.CommandType.JSON }
            .reversed()
            .take(10)
            .forEach {
                Text(
                    "${it.message} [${it.status}] — ${it.details ?: ""}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

        // Вывод текстовых элементов
        Spacer(Modifier.height(8.dp))
        uiState.textElements.forEach {
            Text(
                it.text,
                color = it.color,
                fontSize = it.fontSize.sp
            )
        }

        // Уведомления (если есть)
        uiState.notifications.forEach {
            Text(
                "⚡ ${it.text}",
                color = it.color,
                fontSize = it.fontSize.sp
            )
        }
    }
}
