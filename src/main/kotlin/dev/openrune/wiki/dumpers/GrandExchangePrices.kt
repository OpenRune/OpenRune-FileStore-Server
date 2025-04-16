package dev.openrune.wiki.dumpers

import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

data class PriceData(
    val high: Int,
    val highTime: Long,
    val low: Int,
    val lowTime: Long
)

data class LatestPricesResponse(
    val data: Map<String, PriceData>
)

class GrandExchangePrices {
    private val gson = Gson()
    private val prices: MutableMap<Int, PriceData> = mutableMapOf()

    fun loadFromJson(json: String) {
        val latestResponse = gson.fromJson(json, LatestPricesResponse::class.java)
        latestResponse.data.forEach { (key, value) ->
            key.toIntOrNull()?.let { prices[it] = value }
        }
    }

    fun fetchLatestPrices(): Boolean {
        val url = URL("https://prices.runescape.wiki/api/v1/osrs/latest")
        val connection = url.openConnection() as HttpURLConnection

        return try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val streamReader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = streamReader.use { it.readText() }
                loadFromJson(response)
                true
            } else {
                println("Failed to fetch data: HTTP $responseCode")
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            connection.disconnect()
        }
    }

    fun getPriceData(itemId: Int): Int = if (prices.containsKey(itemId)) prices[itemId]?.high?: 0 else 0

    fun getAllPrices(): Map<Int, PriceData> = prices
}
