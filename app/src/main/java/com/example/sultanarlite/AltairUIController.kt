package com.example.sultanarlite

import android.content.Context
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.sultanarlite.model.CommandLog
import com.example.sultanarlite.model.CommandType
import com.example.sultanarlite.model.CommandStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.*
import java.util.*
import kotlin.random.Random

// ИМПОРТ UI ELEMENTS:
import com.example.sultanarlite.TextElement
import com.example.sultanarlite.NotificationElement
import com.example.sultanarlite.AltairUIState

// --- Render events --- (можно оставить здесь, если только здесь используются)
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

    // Методы управления новыми полями
    fun setInternetEnabled(enabled: Boolean) {
        _uiState.update { it.copy(internetEnabled = enabled) }
    }
    fun setLastInternetResult(result: String) {
        _uiState.update { it.copy(lastInternetResult = result) }
    }
    fun setGoal(goal: String) {
        _uiState.update { it.copy(currentGoal = goal) }
    }

    // Универсальное добавление истории с метаданными
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
        addHistory(
            message = code,
            type = CommandType.INJECT,
            status = CommandStatus.INFO,
            details = "Код внедрён в историю"
        )
        speak("Код внедрён в историю Альтаира")
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
            // --- Дополнительно обработка команд для цели, интернет и т.д. ---
            command.startsWith("цель") -> {
                val goal = command.removePrefix("цель").trim()
                setGoal(goal)
                addHistory(command, CommandType.MANUAL, CommandStatus.SUCCESS, "Цель изменена: $goal")
                speak("Цель изменена")
            }
            command.startsWith("интернет") -> {
                val enable = command.contains("включи", ignoreCase = true)
                setInternetEnabled(enable)
                addHistory(
                    command,
                    CommandType.MANUAL,
                    CommandStatus.SUCCESS,
                    if (enable) "Интернет включен" else "Интернет выключен"
                )
                speak(if (enable) "Интернет включен" else "Интернет выключен")
            }
            else -> {
                addHistory(command, CommandType.MANUAL, CommandStatus.ERROR, "Команда не распознана")
                speak("Команда не распознана")
            }
        }
    }

    fun executeJsonCommand(jsonString: String) {
        try {
            val root = json.parseToJsonElement(jsonString).jsonObject
            val command = root["command"]?.jsonPrimitive?.contentOrNull
            val args = root["args"]?.jsonObject

            if (command == null) {
                addHistory(
                    message = jsonString,
                    type = CommandType.JSON,
                    status = CommandStatus.ERROR,
                    details = "Ошибка: отсутствует команда"
                )
                speak("Ошибка: отсутствует команда")
                return
            }

            when (command) {
                "add_text" -> {
                    val text = args?.get("text")?.jsonPrimitive?.contentOrNull
                    val color = args?.get("color")?.jsonPrimitive?.contentOrNull ?: "#FFFFFF"
                    val fontSize = args?.get("fontSize")?.jsonPrimitive?.intOrNull ?: 16
                    if (text != null) {
                        render(Render.AddText(TextElement(text, Color(android.graphics.Color.parseColor(color)), fontSize)))
                        addHistory(jsonString, CommandType.JSON, CommandStatus.SUCCESS, "Добавлен текст: $text")
                        speak("Добавлен текст: $text")
                    } else {
                        addHistory(jsonString, CommandType.JSON, CommandStatus.ERROR, "Текст не найден")
                        speak("Ошибка: текст не найден")
                    }
                }
                "show_notification" -> {
                    val message = args?.get("message")?.jsonPrimitive?.contentOrNull
                    val color = args?.get("color")?.jsonPrimitive?.contentOrNull ?: "#FFD700"
                    val fontSize = args?.get("fontSize")?.jsonPrimitive?.intOrNull ?: 16
                    if (message != null) {
                        render(Render.ShowNotification(NotificationElement(message, Color(android.graphics.Color.parseColor(color)), fontSize)))
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        addHistory(jsonString, CommandType.JSON, CommandStatus.SUCCESS, "Уведомление: $message")
                        speak("Уведомление: $message")
                    } else {
                        addHistory(jsonString, CommandType.JSON, CommandStatus.ERROR, "Сообщение не найдено")
                        speak("Ошибка: сообщение не найдено")
                    }
                }
                "clear_screen" -> {
                    render(Render.ClearScreen)
                    addHistory(jsonString, CommandType.JSON, CommandStatus.SUCCESS, "Экран очищен")
                    speak("Экран очищен")
                }
                "change_background" -> {
                    render(Render.ChangeBackground)
                    addHistory(jsonString, CommandType.JSON, CommandStatus.SUCCESS, "Фон изменён")
                    speak("Фон изменён")
                }
                // Пример для управления интернетом и целью через JSON
                "set_internet_enabled" -> {
                    val enabled = args?.get("enabled")?.jsonPrimitive?.booleanOrNull ?: false
                    setInternetEnabled(enabled)
                    addHistory(jsonString, CommandType.JSON, CommandStatus.SUCCESS, "Интернет: $enabled")
                    speak(if (enabled) "Интернет включен" else "Интернет выключен")
                }
                "set_goal" -> {
                    val goal = args?.get("goal")?.jsonPrimitive?.contentOrNull.orEmpty()
                    setGoal(goal)
                    addHistory(jsonString, CommandType.JSON, CommandStatus.SUCCESS, "Цель изменена: $goal")
                    speak("Цель изменена")
                }
                "set_last_internet_result" -> {
                    val result = args?.get("result")?.jsonPrimitive?.contentOrNull.orEmpty()
                    setLastInternetResult(result)
                    addHistory(jsonString, CommandType.JSON, CommandStatus.SUCCESS, "Результат поиска обновлён")
                }
                else -> {
                    addHistory(jsonString, CommandType.JSON, CommandStatus.ERROR, "Неизвестная команда: $command")
                    speak("Неизвестная команда: $command")
                }
            }
        } catch (e: Exception) {
            addHistory(
                message = jsonString,
                type = CommandType.JSON,
                status = CommandStatus.ERROR,
                details = "Ошибка JSON: ${e.message}"
            )
            speak("Ошибка JSON: ${e.message}")
            Toast.makeText(context, "Ошибка в формате JSON", Toast.LENGTH_SHORT).show()
        }
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
