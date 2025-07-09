package com.example.sultanarlite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment

@Composable
fun AltairUIScreen(controller: AltairUIController) {
    val uiState by controller.uiState.collectAsState()
    var codeInput by remember { mutableStateOf("") }
    var isJsonMode by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(uiState.backgroundColor)
            .padding(16.dp)
    ) {
        Text(
            text = "Лаборатория ИИ Альтаира",
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = Color(0xFFFFE0E0)
        )

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = { isJsonMode = false },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!isJsonMode) Color(0xFFD2C2B2) else Color(0xFFB0AEB2)
                ),
                shape = RoundedCornerShape(32.dp)
            ) {
                Text("Полный режим")
            }
            Button(
                onClick = { isJsonMode = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isJsonMode) Color(0xFFD2C2B2) else Color(0xFFB0AEB2)
                ),
                shape = RoundedCornerShape(32.dp)
            ) {
                Text("JSON-режим")
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = if (isJsonMode)
                "Внедрить JSON-команду (например: { \"command\": \"add_text\", ... })"
            else
                "Внедрить код или команду (Kotlin, Compose, XML, и др.):",
            color = Color(0xFFFFE0E0),
            fontSize = 16.sp
        )
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = codeInput,
            onValueChange = { codeInput = it },
            label = {
                Text(if (isJsonMode) "JSON-команда" else "Код или команда")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
            maxLines = 5,
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = {
                try {
                    if (isJsonMode) {
                        controller.executeJsonCommand(codeInput)
                    } else {
                        controller.handleCommand(codeInput)
                    }
                    resultMessage = "✅ Код/команда принята для внедрения!"
                } catch (e: Exception) {
                    resultMessage = "❌ Ошибка: ${e.message}"
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCC8855))
        ) {
            Text("Внедрить код", fontSize = 16.sp)
        }

        resultMessage?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = if (it.startsWith("✅")) Color.Green else Color.Red)
        }

        Spacer(Modifier.height(16.dp))

        // Скроллируем только длинную часть!
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Text("📝 Все тексты:", fontWeight = FontWeight.Bold)
            if (uiState.textElements.isEmpty()) {
                Text("Нет текстов", color = Color.Gray, fontSize = 14.sp)
            } else {
                uiState.textElements.forEachIndexed { i, item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = "[${i + 1}] ",
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )
                        Text(
                            text = item.text,
                            color = item.color,
                            fontSize = item.fontSize.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Text("🔔 Уведомления:", fontWeight = FontWeight.Bold)
            if (uiState.notifications.isEmpty()) {
                Text("Нет уведомлений", color = Color.Gray, fontSize = 14.sp)
            } else {
                uiState.notifications.forEachIndexed { i, item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = "[${i + 1}] ",
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )
                        Text(
                            text = item.text,
                            color = item.color,
                            fontSize = item.fontSize.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text("📜 История внедрённых кодов:", fontWeight = FontWeight.Bold)
            uiState.commandHistory.reversed().take(10).forEachIndexed { idx, cmd ->
                Text(
                    "[${cmd.type}] ${cmd.message}",
                    fontSize = 12.sp,
                    color = when (cmd.status) {
                        com.example.sultanarlite.model.CommandStatus.SUCCESS -> Color(0xFF88FF88)
                        com.example.sultanarlite.model.CommandStatus.ERROR -> Color(0xFFFFAAAA)
                        else -> Color.LightGray
                    }
                )
            }
        }
    }
}
