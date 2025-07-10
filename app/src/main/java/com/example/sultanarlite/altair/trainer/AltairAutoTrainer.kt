package com.example.sultanarlite.altair.trainer

import com.example.sultanarlite.altair.core.AltairMemory
import com.example.sultanarlite.network.GptApi

object AltairAutoTrainer {
    fun trainOnce() {
        val memory = AltairMemory.load()
        val goals = memory.getWeakSpots()

        if (goals.isEmpty()) return

        val request = "Научи ИИ улучшить: ${goals.first()}. Дай пример, код и стратегию."
        val reply = GptApi.sendPrompt(request)

        memory.savePatch(reply.take(80)) // Запомнить описание
        memory.save() // Применить изменения (уже внутри есть запись)
    }
}
