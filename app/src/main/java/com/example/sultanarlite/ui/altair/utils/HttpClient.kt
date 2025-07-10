package com.sultan.altair.utils

import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

object HttpClient {
    fun getJson(url: String): Map<String, Any?> {
        val text = getText(url)
        return try {
            val jsonObject = JSONObject(text)
            jsonObject.toMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun getText(url: String): String {
        return try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.requestMethod = "GET"
            connection.inputStream.bufferedReader().use(BufferedReader::readText)
        } catch (e: Exception) {
            ""
        }
    }

    private fun JSONObject.toMap(): Map<String, Any?> =
        keys().asSequence().associateWith { key ->
            when (val value = this[key]) {
                is JSONObject -> value.toMap()
                is JSONArray -> value.toList()
                else -> value
            }
        }

    private fun JSONArray.toList(): List<Any?> =
        (0 until length()).map { index ->
            when (val value = this[index]) {
                is JSONObject -> value.toMap()
                is JSONArray -> value.toList()
                else -> value
            }
        }
}
