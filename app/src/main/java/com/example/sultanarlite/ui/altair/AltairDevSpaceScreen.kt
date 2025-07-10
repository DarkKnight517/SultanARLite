package com.example.sultanarlite.ui.altair

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
import com.example.sultanarlite.AltairUIController
import com.example.sultanarlite.AltairUIScreen
import java.text.SimpleDateFormat
import java.util.*

enum class DevTab { MAIN, DEVLOG, NET, GOAL }

@Composable
fun AltairDevSpaceScreen(altairController: AltairUIController) {
    var tab by remember { mutableStateOf(DevTab.MAIN) }
    val uiState by altairController.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(uiState.backgroundColor)
            .padding(24.dp)
    ) {
        Text(
            text = "Лаборатория ИИ Альтаира",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFFFFE4E1),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(Modifier.padding(bottom = 12.dp)) {
            TabButton("Основное", tab == DevTab.MAIN, { tab = DevTab.MAIN }, Modifier.weight(1f).padding(end = 4.dp))
            TabButton("DevLog", tab == DevTab.DEVLOG, { tab = DevTab.DEVLOG }, Modifier.weight(1f).padding(end = 4.dp))
            TabButton("Интернет", tab == DevTab.NET, { tab = DevTab.NET }, Modifier.weight(1f).padding(end = 4.dp))
            TabButton("Цель/Стратегия", tab == DevTab.GOAL, { tab = DevTab.GOAL }, Modifier.weight(1f))
        }

        Spacer(Modifier.height(8.dp))

        Box(Modifier.weight(1f)) {
            val scrollState = rememberScrollState()
            Box(Modifier.fillMaxSize().verticalScroll(scrollState)) {
                when (tab) {
                    DevTab.MAIN -> MainAltairTab(uiState, altairController)
                    DevTab.DEVLOG -> DevLogTab(uiState)
                    DevTab.NET -> NetTab(uiState, altairController)
                    DevTab.GOAL -> GoalTab(uiState, altairController)
                }
            }
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color(0xFFD7BFAE) else Color(0xFFBDBDBD)
        ),
        modifier = modifier
    ) {
        Text(text)
    }
}

@Composable
private fun MainAltairTab(
    uiState: com.example.sultanarlite.AltairUIState,
    controller: AltairUIController
) {
    AltairUIScreen(controller)
}

@Composable
private fun DevLogTab(uiState: com.example.sultanarlite.AltairUIState) {
    val dateFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }
    Column {
        Text("Журнал действий DevLog:", color = Color.Yellow, fontSize = 18.sp, modifier = Modifier.padding(bottom = 8.dp))
        if (uiState.commandHistory.isEmpty()) {
            Text("Журнал пуст", color = Color.Gray)
        } else {
            uiState.commandHistory.reversed().forEach { log ->
                Text(
                    text = "[${dateFormat.format(Date(log.timestamp))}] ${log.type}: ${log.message} (${log.status}) ${log.details.orEmpty()}",
                    color = when (log.status) {
                        com.example.sultanarlite.model.CommandStatus.SUCCESS -> Color(0xFFA5FFAB)
                        com.example.sultanarlite.model.CommandStatus.ERROR -> Color.Red
                        else -> Color.LightGray
                    },
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun NetTab(
    uiState: com.example.sultanarlite.AltairUIState,
    controller: AltairUIController
) {
    var searchText by remember { mutableStateOf("") }
    Column {
        Text("Интернет-доступ: ${if (uiState.internetEnabled) "ВКЛЮЧЕН" else "выключен"}", color = Color.Cyan)
        Row(Modifier.padding(vertical = 8.dp)) {
            Button(
                onClick = { controller.setInternetEnabled(true) },
                enabled = !uiState.internetEnabled,
                colors = ButtonDefaults.buttonColors(Color(0xFF7AEBAE)),
                modifier = Modifier.weight(1f).padding(end = 4.dp)
            ) { Text("Включить интернет") }
            Button(
                onClick = { controller.setInternetEnabled(false) },
                enabled = uiState.internetEnabled,
                colors = ButtonDefaults.buttonColors(Color(0xFFF78F80)),
                modifier = Modifier.weight(1f)
            ) { Text("Отключить интернет") }
        }

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Поиск в интернете") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (searchText.isNotBlank()) controller.handleCommand("поиск в интернете $searchText")
            },
            enabled = uiState.internetEnabled,
            modifier = Modifier.padding(top = 6.dp)
        ) { Text("Выполнить поиск") }

        Button(
            onClick = { controller.handleCommand("изучи результат") },
            enabled = uiState.lastInternetResult.isNotBlank(),
            modifier = Modifier.padding(top = 6.dp)
        ) { Text("Изучить результат") }

        if (uiState.lastInternetResult.isNotBlank()) {
            Text("Результат поиска (начало):", color = Color.Yellow, fontSize = 14.sp, modifier = Modifier.padding(top = 12.dp))
            Text(uiState.lastInternetResult.take(800), color = Color.White, fontSize = 13.sp)
        }
    }
}

@Composable
private fun GoalTab(
    uiState: com.example.sultanarlite.AltairUIState,
    controller: AltairUIController
) {
    var goalText by remember { mutableStateOf(TextFieldValue(uiState.currentGoal)) }
    Column {
        Text("Текущая цель Альтаира:", color = Color.Yellow, fontSize = 16.sp)
        OutlinedTextField(
            value = goalText,
            onValueChange = { goalText = it },
            label = { Text("Цель/стратегия") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { controller.setGoal(goalText.text) },
            modifier = Modifier.padding(top = 8.dp)
        ) { Text("Изменить цель") }

        Text("Текущая: ${uiState.currentGoal}", color = Color.LightGray, fontSize = 13.sp, modifier = Modifier.padding(top = 12.dp))
    }
}
