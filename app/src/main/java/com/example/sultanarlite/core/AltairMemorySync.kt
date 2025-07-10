package com.sultanarlite.core

import android.content.Context
import org.json.JSONObject
import java.io.File

object AltairMemorySync {

    private const val FILE_NAME = "altair_sync.json"

    fun exportToFile(context: Context, memory: AltairLearningMemory, responseMode: AltairCommandProcessor.ResponseMode): Boolean {
        return try {
            val json = JSONObject()
            json.put("responseMode", responseMode.name)
            json.put("topCommands", memory.getMostUsedCommands(10))

            val file = File(context.filesDir, FILE_NAME)
            file.writeText(json.toString())
            true
        } catch (e: Exception) {
            false
        }
    }

    fun importFromFile(context: Context, memory: AltairLearningMemory): AltairCommandProcessor.ResponseMode {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) return AltairCommandProcessor.ResponseMode.NORMAL

        val json = JSONObject(file.readText())
        val commands = json.getJSONArray("topCommands")
        for (i in 0 until commands.length()) {
            memory.recordCommand(commands.getString(i))
        }

        val mode = json.optString("responseMode", "NORMAL")
        return AltairCommandProcessor.ResponseMode.valueOf(mode)
    }
}