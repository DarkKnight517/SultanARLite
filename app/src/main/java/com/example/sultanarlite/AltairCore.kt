package com.example.sultanarlite

object AltairCore {
    private val commandMap = mutableMapOf<String, () -> Unit>()

    fun registerCommand(name: String, action: () -> Unit) {
        commandMap[name] = action
    }

    fun executeCommand(name: String) {
        commandMap[name]?.invoke()
    }

    fun clearCommands() {
        commandMap.clear()
    }
}
