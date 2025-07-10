package com.example.sultanarlite.altair.update

import android.content.Context
import kotlinx.coroutines.delay
import org.json.JSONObject
import java.io.File

object AltairUpdater {
    private const val PATCHES_FOLDER = "altair_patches"

    fun checkAndUpdate(context: Context) {
        val patchDir = File(context.filesDir, PATCHES_FOLDER)
        if (!patchDir.exists()) return

        val patches = patchDir.listFiles { file -> file.extension == "json" } ?: return
        if (patches.isEmpty()) return

        val memory = com.example.sultanarlite.altair.core.AltairMemory
        memory.init(context)
        memory.load()

        for (patchFile in patches) {
            try {
                val patchJson = JSONObject(patchFile.readText())
                memory.applyPatch(patchJson)
                memory.savePatch(patchJson.optString("description", patchFile.name))
                patchFile.delete()
            } catch (_: Exception) {
                // Патч повреждён — пропустить
            }
        }

        memory.save()
    }
}
