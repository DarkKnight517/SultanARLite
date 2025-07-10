package com.example.sultanarlite.network

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject

object GptApi {
    private const val API_KEY = "sk-..." // Замени на свой настоящий ключ
    private const val ENDPOINT = "https://api.openai.com/v1/chat/completions"

    fun sendPrompt(prompt: String): String {
        return try {
            val client = OkHttpClient()

            val json = JSONObject().apply {
                put("model", "gpt-3.5-turbo")
                put("messages", listOf(
                    mapOf("role" to "user", "content" to prompt)
                ))
            }

            val mediaType = "application/json".toMediaType()
            val body = RequestBody.create(mediaType, json.toString())

            val request = Request.Builder()
                .url(ENDPOINT)
                .header("Authorization", "Bearer $API_KEY")
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: return "Ошибка: пустой ответ"

            val jsonResponse = JSONObject(responseBody)

            if (!jsonResponse.has("choices")) {
                return "Ошибка: ответ не содержит поля 'choices':\n$responseBody"
            }

            return jsonResponse
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
                .trim()

        } catch (e: Exception) {
            "Ошибка при запросе к GPT: ${e.message}"
        }
    }
}
