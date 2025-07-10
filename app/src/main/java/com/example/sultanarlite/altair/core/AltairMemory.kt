package com.example.sultanarlite.altair.core

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object AltairMemory {
    private lateinit var memoryFile: File

    var knownSkills = mutableListOf<String>()
    var patches = mutableListOf<String>()

    fun init(context: Context) {
        memoryFile = File(context.filesDir, "altair_memory.json")
        if (!memoryFile.exists()) {
            memoryFile.writeText("""{ "knownSkills": [], "appliedPatches": [] }""")
        }
        load()
    }

    fun load(): AltairMemory {
        if (!memoryFile.exists()) return this
        val text = memoryFile.readText()
        val json = JSONObject(text)
        knownSkills = json.optJSONArray("knownSkills")?.let {
            MutableList(it.length()) { i -> it.getString(i) }
        } ?: mutableListOf()
        patches = json.optJSONArray("appliedPatches")?.let {
            MutableList(it.length()) { i -> it.getString(i) }
        } ?: mutableListOf()
        return this
    }

    fun save() {
        val json = JSONObject().apply {
            put("knownSkills", JSONArray(knownSkills))
            put("appliedPatches", JSONArray(patches))
        }
        memoryFile.writeText(json.toString(2))
    }

    fun getWeakSpots(): List<String> {
        return listOf(
            "контекстное мышление",
            "обработка команд с аргументами",
            "самооценка поведения"
        ).filter { it !in knownSkills }
    }

    fun savePatch(text: String) {
        val short = text.trim().take(100)
        if (short !in patches) patches.add(short)
        save()
    }

    fun applyPatch(patchJson: JSONObject) {
        val skills = patchJson.optJSONArray("addSkills") ?: return
        for (i in 0 until skills.length()) {
            val skill = skills.getString(i)
            if (!knownSkills.contains(skill)) knownSkills.add(skill)
        }
        save()
    }

    fun absorbTextAsKnowledge(rawText: String): String {
        val learned = mutableListOf<String>()
        val lines = rawText.split("\n").map { it.trim() }.filter { it.length > 15 }

        for (line in lines) {
            if (line !in patches && patches.none { line.startsWith(it.take(30)) }) {
                patches.add(line.take(100))
                learned.add(line)
            }
        }

        if (learned.isNotEmpty()) save()
        return "Изучено фрагментов: ${learned.size}"
    }
}
