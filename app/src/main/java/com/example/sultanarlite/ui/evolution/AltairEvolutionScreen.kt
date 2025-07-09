package com.example.sultanarlite.ui.evolution

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sultanarlite.scenario.AltairScenarioManager
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun AltairEvolutionScreen(scenarioManager: AltairScenarioManager) {
    val curr = scenarioManager.currentScenario
    val history = scenarioManager.history
    val coroutineScope = rememberCoroutineScope()
    var status by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Развитие Альтаира", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))

        curr?.let {
            Text("Текущий сценарий: ${it.name} (v${it.version})")
            Text("Описание: ${it.description}")
            Text("Команды: ${it.commands.joinToString()}", color = Color.Gray)
        } ?: Text("Нет активного сценария.")

        Spacer(Modifier.height(12.dp))
        Row {
            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            scenarioManager.loadScenarioFromAssets("altair_scenario.json")
                            status = "✅ Сценарий загружен"
                        } catch (e: Exception) {
                            status = "❌ Ошибка загрузки: ${e.message}"
                        }
                    }
                }
            ) { Text("Загрузить из assets") }

            Spacer(Modifier.width(8.dp))

            Button(
                onClick = { scenarioManager.rollbackLast(); status = "Откат выполнен" },
                enabled = history.isNotEmpty()
            ) { Text("Откатить последнее") }
        }
        status?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = if (it.startsWith("✅")) Color.Green else Color.Red)
        }

        Spacer(Modifier.height(18.dp))
        Text("История изменений:", fontWeight = FontWeight.Bold)
        Column(Modifier.verticalScroll(rememberScrollState())) {
            history.reversed().forEach { patch ->
                Text("[${Date(patch.timestamp)}] ${patch.diff}", fontSize = 13.sp, color = Color.Gray)
            }
        }
    }
}
