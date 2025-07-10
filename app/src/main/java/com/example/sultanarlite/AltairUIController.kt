package com.example.sultanarlite

import android.content.Context
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.sultanarlite.model.CommandLog
import com.example.sultanarlite.model.CommandType
import com.example.sultanarlite.model.CommandStatus
import com.example.sultanarlite.altair.core.AltairMemory
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.*
import org.json.JSONObject
import java.net.*
import java.util.*
import kotlin.random.Random

sealed class Render {
    data class AddText(val elem: TextElement) : Render()
    data class ShowNotification(val elem: NotificationElement) : Render()
    object ClearScreen : Render()
    object ChangeBackground : Render()
}

class AltairUIController(private val context: Context) : ViewModel(), TextToSpeech.OnInitListener {
    private val _uiState = MutableStateFlow(AltairUIState())
    val uiState: StateFlow<AltairUIState> = _uiState

    private var tts: TextToSpeech = TextToSpeech(context, this)
    private val json = Json { ignoreUnknownKeys = true }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale("ru")
        }
    }

    fun setInternetEnabled(enabled: Boolean) {
        _uiState.update { it.copy(internetEnabled = enabled) }
    }

    fun setLastInternetResult(result: String) {
        _uiState.update { it.copy(lastInternetResult = result) }
    }

    fun setGoal(goal: String) {
        _uiState.update { it.copy(currentGoal = goal) }
    }

    fun addHistory(
        message: String,
        type: CommandType = CommandType.MANUAL,
        status: CommandStatus = CommandStatus.SUCCESS,
        details: String? = null
    ) {
        _uiState.update { state ->
            state.copy(commandHistory = state.commandHistory + CommandLog(
                message = message,
                type = type,
                status = status,
                timestamp = System.currentTimeMillis(),
                details = details
            ))
        }
    }

    fun addInjectedCode(code: String) {
        addHistory(code, CommandType.INJECT, CommandStatus.INFO, "Код внедрён в историю")
        speak("Код внедрён в историю Альтаира")
    }

    fun interpret(text: String) {
        handleCommand(text)
    }

    fun handleCommand(input: String) {
        val command = input.trim()
        if (command.isBlank()) return

        when {
            command.startsWith("добавь текст") -> {
                val content = command.removePrefix("добавь текст").trim()
                render(Render.AddText(TextElement(text = content)))
                addHistory(command, CommandType.MANUAL, CommandStatus.SUCCESS, "Добавлен текст: $content")
                speak("Добавил текст: $content")
            }
            command.startsWith("показать уведомление") -> {
                val content = command.removePrefix("показать уведомление").trim()
                render(Render.ShowNotification(NotificationElement(text = content)))
                addHistory(command, CommandType.MANUAL, CommandStatus.SUCCESS, "Показал уведомление: $content")
                speak("Показал уведомление: $content")
            }
            command == "очистить экран" -> {
                render(Render.ClearScreen)
                addHistory(command, CommandType.MANUAL, CommandStatus.SUCCESS, "Экран очищен")
                speak("Экран очищен")
            }
            command == "поменяй фон" -> {
                render(Render.ChangeBackground)
                addHistory(command, CommandType.MANUAL, CommandStatus.SUCCESS, "Фон изменён")
                speak("Фон изменён")
            }
            command.startsWith("цель") -> {
                val goal = command.removePrefix("цель").trim()
                setGoal(goal)
                addHistory(command, CommandType.MANUAL, CommandStatus.SUCCESS, "Цель изменена: $goal")
                speak("Цель изменена")
            }
            command.startsWith("интернет") -> {
                val enable = command.contains("включи", ignoreCase = true)
                setInternetEnabled(enable)
                addHistory(command, CommandType.MANUAL, CommandStatus.SUCCESS, if (enable) "Интернет включен" else "Интернет выключен")
                speak(if (enable) "Интернет включен" else "Интернет выключен")
            }
            command.startsWith("поиск в интернете") -> {
                val query = command.removePrefix("поиск в интернете").trim()
                performInternetSearch(query)
                speak("Ищу в интернете: $query")
            }
            command == "изучи результат" -> {
                learnFromInternetResult()
            }
            else -> {
                addHistory(command, CommandType.MANUAL, CommandStatus.ERROR, "Команда не распознана")
                speak("Команда не распознана")
            }
        }
    }

    fun executeJsonCommand(json: String) {
        try {
            val jsonObject = JSONObject(json)
            val command = jsonObject.getString("command")
            val args = jsonObject.optJSONObject("args")

            when (command) {
                "add_text" -> {
                    val text = args?.getString("text") ?: "нет текста"
                    render(Render.AddText(TextElement(text)))
                    addHistory("[json] $text", CommandType.JSON, CommandStatus.SUCCESS, "Добавлен текст через JSON")
                    speak("Добавлен текст")
                }
                "show_notification" -> {
                    val text = args?.getString("text") ?: "уведомление"
                    render(Render.ShowNotification(NotificationElement(text)))
                    addHistory("[json] $text", CommandType.JSON, CommandStatus.SUCCESS, "Показано уведомление")
                    speak("Уведомление показано")
                }
                else -> {
                    addHistory("[json] Неизвестная команда: $command", CommandType.JSON, CommandStatus.ERROR)
                    speak("Неизвестная JSON-команда")
                }
            }
        } catch (e: Exception) {
            addHistory("[json] Ошибка JSON: ${e.message}", CommandType.JSON, CommandStatus.ERROR)
            speak("Ошибка при разборе JSON")
        }
    }

    private fun performInternetSearch(query: String) {
        val encoded = URLEncoder.encode(query, "UTF-8")
        val url = "https://duckduckgo.com/html/?q=$encoded"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                val html = connection.inputStream.bufferedReader().readText()
                connection.disconnect()

                val resultText = parseHtmlResults(html)
                setLastInternetResult(resultText)
                addHistory("поиск в интернете $query", CommandType.MANUAL, CommandStatus.SUCCESS, "Получено ${resultText.length} символов")
                speak("Результаты поиска готовы")
            } catch (e: Exception) {
                setLastInternetResult("Ошибка при подключении: ${e.message}")
                addHistory("поиск в интернете $query", CommandType.MANUAL, CommandStatus.ERROR, "Ошибка: ${e.message}")
                speak("Ошибка при поиске")
            }
        }
    }

    private fun parseHtmlResults(html: String): String {
        val results = Regex("<a[^>]+class=\"[^\"=]*result__a[^>]*\"[^>]*>(.*?)</a>")
            .findAll(html)
            .take(5)
            .joinToString("\n") {
                val title = it.groupValues[1]
                    .replace(Regex("<[^>]*>"), "")
                    .replace("&nbsp;", " ")
                "• $title"
            }

        return if (results.isBlank()) "Результаты не найдены" else results
    }

    fun learnFromInternetResult() {
        val content = _uiState.value.lastInternetResult
        if (content.isBlank()) {
            speak("Нет данных для обучения")
            return
        }

        val keywords = content.lines()
            .map { it.replace("•", "").trim() }
            .filter { it.length > 10 }
            .map { it.take(50) }

        if (keywords.isEmpty()) {
            speak("Недостаточно информации для обучения")
            return
        }

        AltairMemory.knownSkills.addAll(keywords)
        AltairMemory.savePatch(content)
        AltairMemory.save()
        speak("Альтаир изучил новый материал")
        addHistory("Изучен результат", CommandType.MANUAL, CommandStatus.SUCCESS, "${keywords.size} знаний добавлено")
    }

    private fun render(action: Render) {
        val current = _uiState.value
        when (action) {
            is Render.AddText -> _uiState.value = current.copy(
                textElements = current.textElements + action.elem
            )
            is Render.ShowNotification -> _uiState.value = current.copy(
                notifications = current.notifications + action.elem
            )
            is Render.ClearScreen -> _uiState.value = current.copy(
                textElements = emptyList(),
                notifications = emptyList(),
                backgroundColor = current.backgroundColor
            )
            is Render.ChangeBackground -> _uiState.value = current.copy(
                backgroundColor = Color(
                    Random.nextInt(256),
                    Random.nextInt(256),
                    Random.nextInt(256)
                )
            )
        }
    }

    fun speak(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onCleared() {
        super.onCleared()
        tts.stop()
        tts.shutdown()
    }
}
