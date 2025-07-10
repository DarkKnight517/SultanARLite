package com.example.sultanarlite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sultanarlite.model.CommandStatus
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DialogScreen(viewModel: MainViewModel, altairController: AltairUIController) {
    val uiState by altairController.uiState.collectAsState()
    var inputText by remember { mutableStateOf(TextFieldValue("")) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(uiState.backgroundColor)
            .padding(16.dp)
    ) {
        Text(
            "Диалог с Альтаиром",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Введите команду") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    val text = inputText.text.trim()
                    if (text.isNotEmpty()) {
                        altairController.handleCommand(text)
                        inputText = TextFieldValue("")
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Отправить")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { altairController.speak("Привет, я Альтаир") },
                modifier = Modifier.weight(1f)
            ) {
                Text("🔊 Голос")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "История команд:",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Yellow
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(top = 8.dp)
        ) {
            val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            uiState.commandHistory.reversed().forEach { entry ->
                Text(
                    text = "[${dateFormat.format(Date(entry.timestamp))}] ${entry.message} (${entry.status})",
                    color = when (entry.status) {
                        CommandStatus.SUCCESS -> Color(0xFFA5FFAB)
                        CommandStatus.ERROR -> Color.Red
                        else -> Color.LightGray
                    },
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
    }
}
