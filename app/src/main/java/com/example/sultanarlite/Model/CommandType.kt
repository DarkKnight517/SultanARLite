package com.example.sultanarlite.model

enum class CommandType {
    MANUAL,     // команда от пользователя (введённая вручную)
    JSON,       // команда, пришедшая в формате JSON
    INJECT,     // внедрённый код
    SYSTEM      // системная команда или статус
}
