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
            Button(
                onClick = { tab = DevTab.MAIN },
                colors = ButtonDefaults.buttonColors(if (tab == DevTab.MAIN) Color(0xFFD7BFAE) else Color(0xFFBDBDBD)),
                modifier = Modifier.weight(1f).padding(end = 4.dp)
            ) { Text("Основное") }
            Button(
                onClick = { tab = DevTab.DEVLOG },
                colors = ButtonDefaults.buttonColors(if (tab == DevTab.DEVLOG) Color(0xFFD7BFAE) else Color(0xFFBDBDBD)),
                modifier = Modifier.weight(1f).padding(end = 4.dp)
            ) { Text("DevLog") }
            Button(
                onClick = { tab = DevTab.NET },
                colors = ButtonDefaults.buttonColors(if (tab == DevTab.NET) Color(0xFFD7BFAE) else Color(0xFFBDBDBD)),
                modifier = Modifier.weight(1f).padding(end = 4.dp)
            ) { Text("Интернет") }
            Button(
                onClick = { tab = DevTab.GOAL },
                colors = ButtonDefaults.buttonColors(if (tab == DevTab.GOAL) Color(0xFFD7BFAE) else Color(0xFFBDBDBD)),
                modifier = Modifier.weight(1f)
            ) { Text("Цель/Стратегия") }
        }
        Spacer(Modifier.height(8.dp))
        // Прокрутка добавлена для каждой вкладки (все через отдельный ScrollState)
        Box(Modifier.weight(1f)) {
            when (tab) {
                DevTab.MAIN -> {
                    val scrollState = rememberScrollState()
                    Box(Modifier.fillMaxSize().verticalScroll(scrollState)) {
                        MainAltairTab(uiState, altairController)
                    }
                }
                DevTab.DEVLOG -> {
                    val scrollState = rememberScrollState()
                    Box(Modifier.fillMaxSize().verticalScroll(scrollState)) {
                        DevLogTab(uiState)
                    }
                }
                DevTab.NET -> {
                    val scrollState = rememberScrollState()
                    Box(Modifier.fillMaxSize().verticalScroll(scrollState)) {
                        NetTab(uiState, altairController)
                    }
                }
                DevTab.GOAL -> {
                    val scrollState = rememberScrollState()
                    Box(Modifier.fillMaxSize().verticalScroll(scrollState)) {
                        GoalTab(uiState, altairController)
                    }
                }
            }
        }
    }
}

@Composable
private fun MainAltairTab(
    uiState: com.example.sultanarlite.AltairUIState,
    controller: AltairUIController
) {
    // Весь лабораторный UI с внедрением кода и JSON-режимом
    AltairUIScreen(controller)
}

@Composable
private fun DevLogTab(uiState: com.example.sultanarlite.AltairUIState) {
    val dateFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }
    Column {
        Text(
            "Журнал действий DevLog:",
            color = Color.Yellow,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        if (uiState.commandHistory.isEmpty()) {
            Text("Журнал пуст", color = Color.Gray)
        } else uiState.commandHistory.reversed().forEach { log ->
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
        Text(
            "Текущая: ${uiState.currentGoal}",
            color = Color.LightGray,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}
