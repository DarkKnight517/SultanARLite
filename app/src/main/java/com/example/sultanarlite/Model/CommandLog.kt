package com.example.sultanarlite.model

enum class CommandType { MANUAL, JSON, INJECT, SYSTEM }
enum class CommandStatus { SUCCESS, ERROR, INFO }

data class CommandLog(
    val message: String,
    val type: CommandType,
    val status: CommandStatus,
    val timestamp: Long = System.currentTimeMillis(),
    val details: String? = null
)
