package com.example.sultanarlite.scenario

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.util.*

@Serializable
data class AltairScenario(
    val name: String,
    val description: String,
    val version: String,
    val commands: List<String>,
    val uiConfig: Map<String, String>
)

data class ScenarioPatch(
    val timestamp: Long,
    val scenario: AltairScenario,
    val previous: AltairScenario?,
    val diff: String
)

class AltairScenarioManager(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    private var _currentScenario: AltairScenario? = null
    private val _history = mutableListOf<ScenarioPatch>()

    val currentScenario: AltairScenario? get() = _currentScenario
    val history: List<ScenarioPatch> get() = _history

    // Загрузка сценария из assets
    suspend fun loadScenarioFromAssets(assetName: String) {
        val jsonText = withContext(Dispatchers.IO) {
            context.assets.open(assetName).bufferedReader().use { it.readText() }
        }
        loadScenario(jsonText)
    }

    // Загрузка сценария из интернета
    suspend fun loadScenarioFromUrl(url: String) {
        // Псевдо-реализация: подключите OkHttp/Retrofit!
        // val jsonText = downloadTextFromUrl(url)
        // loadScenario(jsonText)
    }

    fun loadScenario(jsonText: String) {
        val newScenario = json.decodeFromString<AltairScenario>(jsonText)
        val prev = _currentScenario
        val diff = makeDiff(prev, newScenario)
        _history.add(ScenarioPatch(System.currentTimeMillis(), newScenario, prev, diff))
        _currentScenario = newScenario
    }

    fun rollbackLast() {
        if (_history.isNotEmpty()) {
            val last = _history.removeLast()
            _currentScenario = last.previous
        }
    }

    private fun makeDiff(old: AltairScenario?, new: AltairScenario): String {
        return if (old == null) "Первый сценарий загружен"
        else "Версия: ${old.version} → ${new.version}; Команды: ${new.commands.size}"
    }
}
