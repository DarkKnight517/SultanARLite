package com.sultanarlite.core

class AltairLearningMemory {

    private val usageFrequency = mutableMapOf<String, Int>()
    private val commandPairs = mutableMapOf<Pair<String, String>, Int>()
    var preferredResponseMode: AltairCommandProcessor.ResponseMode = AltairCommandProcessor.ResponseMode.NORMAL

    fun recordCommand(command: String) {
        usageFrequency[command] = (usageFrequency[command] ?: 0) + 1
    }

    fun recordCommandPair(prev: String, current: String) {
        val pair = Pair(prev, current)
        commandPairs[pair] = (commandPairs[pair] ?: 0) + 1
    }

    fun getMostUsedCommands(limit: Int = 3): List<String> {
        return usageFrequency.entries.sortedByDescending { it.value }.take(limit).map { it.key }
    }

    fun suggestNextCommand(lastCommand: String): String? {
        val candidates = commandPairs.filterKeys { it.first == lastCommand }
        return candidates.maxByOrNull { it.value }?.key?.second
    }
}