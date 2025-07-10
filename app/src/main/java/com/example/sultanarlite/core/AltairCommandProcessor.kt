package com.sultanarlite.core

import android.content.Context
import android.widget.Toast

class AltairCommandProcessor(private val context: Context) {

    private val commandHistory = mutableListOf<String>()
    private var isPaused = false
    private var responseMode = ResponseMode.NORMAL
    private val memory = AltairLearningMemory()
    private var lastCommand: String? = null

    enum class ResponseMode {
        SHORT, NORMAL, DETAILED
    }

    fun setResponseMode(mode: ResponseMode) {
        responseMode = mode
    }

    fun process(command: String): String {
        if (isPaused && !command.contains("продолжи", ignoreCase = true)) {
            return "Ожидаю. Скажи «продолжи», чтобы вернуться к работе."
        }

        memory.recordCommand(command)
        lastCommand?.let { memory.recordCommandPair(it, command) }
        lastCommand = command

        commandHistory.add(command)

        return when {
            command.contains("добавь текст", true) -> {
                val text = command.substringAfter("добавь текст").trim()
                if (text.isNotEmpty()) {
                    "Текст добавлен: $text"
                } else {
                    "Пожалуйста, укажи текст для добавления."
                }
            }

            command.contains("очистить экран", true) -> {
                "Экран очищен."
            }

            command.contains("показать уведомление", true) -> {
                Toast.makeText(context, "Уведомление", Toast.LENGTH_SHORT).show()
                "Уведомление показано."
            }

            command.contains("переключи тему", true) -> {
                "Тема оформления изменена."
            }

            command.contains("сделай паузу", true) -> {
                isPaused = true
                "Режим паузы активирован. Скажи «продолжи», чтобы снять паузу."
            }

            command.contains("продолжи", true) -> {
                isPaused = false
                "Продолжаем работу."
            }

            command.contains("сохрани как шаблон", true) -> {
                "Последняя команда сохранена как шаблон."
            }

            command.contains("покажи последние команды", true) -> {
                val recent = commandHistory.takeLast(5).joinToString("\n")
                "Последние команды:\n$recent"
            }

            command.contains("ответь коротко", true) -> {
                responseMode = ResponseMode.SHORT
                "Теперь отвечаю кратко."
            }

            command.contains("ответь развернуто", true) -> {
                responseMode = ResponseMode.DETAILED
                "Теперь отвечаю развернуто."
            }

            else -> {
                when (responseMode) {
                    ResponseMode.SHORT -> "Принято."
                    ResponseMode.DETAILED -> "Я получил твою команду: «$command». Выполняю по стандартному сценарию."
                    else -> "Команда принята: $command"
                }
            }
        }
    }
}