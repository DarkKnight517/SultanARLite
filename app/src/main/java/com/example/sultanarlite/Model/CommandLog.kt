package com.example.sultanarlite.model

import com.example.sultanarlite.model.CommandType
import com.example.sultanarlite.model.CommandStatus

data class CommandLog(
    val message: String,
    val type: CommandType,
    val status: CommandStatus,
    val timestamp: Long = System.currentTimeMillis(),
    val details: String? = null
)
