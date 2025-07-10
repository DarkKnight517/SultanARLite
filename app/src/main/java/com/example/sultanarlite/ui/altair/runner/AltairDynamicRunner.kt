package com.sultan.altair.runner

import java.io.File
import javax.script.ScriptEngineManager

object AltairDynamicRunner {
    fun runTrainerScript() {
        try {
            val engine = ScriptEngineManager().getEngineByExtension("kts")
            val scriptFile = File("/data/data/com.sultan/files/AltairAutoTrainer.kt")
            if (!scriptFile.exists()) {
                println("⚠️ Скрипт не найден.")
                return
            }
            val result = engine.eval(scriptFile.readText())
            println("✅ Скрипт выполнен: $result")
        } catch (e: Exception) {
            println("❌ Ошибка при запуске скрипта: ${e.message}")
        }
    }
}
