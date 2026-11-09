
package com.swin.radar

import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

data class RadarObject(
    val id: Int,
    val type: String,
    val x: Float,
    val y: Float
)

object RadarRepository {

    fun demoObjects(): List<RadarObject> = listOf(
        RadarObject(1, "player", -48f, -42f),
        RadarObject(2, "player", 42f, -28f),
        RadarObject(3, "player", -55f, 36f),
        RadarObject(4, "bot", 30f, 48f),
        RadarObject(5, "bot", 68f, 12f),
        RadarObject(6, "loot", -18f, 66f),
        RadarObject(7, "loot", 60f, -65f),
        RadarObject(8, "crate", 8f, -72f)
    )

    fun fetch(endpoint: String): List<RadarObject> {
        val url = URL(endpoint)

        require(url.protocol == "https") {
            "Используй HTTPS-адрес сервера"
        }

        val connection =
            url.openConnection() as HttpURLConnection

        connection.requestMethod = "GET"
        connection.connectTimeout = 7000
        connection.readTimeout = 7000
        connection.setRequestProperty("Accept", "application/json")

        try {
            val code = connection.responseCode

            val stream = if (code in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            }

            val body = stream?.bufferedReader()?.use {
                it.readText()
            } ?: throw IOException("Пустой ответ сервера")

            if (code !in 200..299) {
                throw IOException("HTTP " + code)
            }

            val root = JSONObject(body)
            val array = root.optJSONArray("objects")
                ?: throw IOException("Нет массива objects")

            return (0 until array.length()).mapNotNull { index ->
                val item = array.optJSONObject(index)
                    ?: return@mapNotNull null

                val type = item.optString("type", "")

                if (type !in setOf(
                    "player", "bot", "loot", "crate"
                )) {
                    return@mapNotNull null
                }

                RadarObject(
                    id = item.optInt("id", index),
                    type = type,
                    x = item.optDouble("x", 0.0)
                        .toFloat().coerceIn(-100f, 100f),
                    y = item.optDouble("y", 0.0)
                        .toFloat().coerceIn(-100f, 100f)
                )
            }
        } finally {
            connection.disconnect()
        }
    }
}

