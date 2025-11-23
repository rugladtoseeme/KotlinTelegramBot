package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class TelegramBotService(val botToken: String) {

    fun sendMessage(chatId: Long, messageText: String): String {

        val urlGetUpdates =
            "https://api.telegram.org/bot$botToken/sendMessage?chat_id=$chatId&text=$messageText"
        val client: HttpClient = HttpClient.newBuilder().build()
        val requestGetUpdates: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        try {

            val responseGetUpdates: HttpResponse<String> =
                client.send(requestGetUpdates, HttpResponse.BodyHandlers.ofString())

            return responseGetUpdates.body()
        } catch (e: Exception) {
            throw RuntimeException(e.message)
        }
    }

    fun getUpdates(updateId: Long): String {

        val urlGetUpdates = "https://api.telegram.org/bot$botToken/getUpdates?offset=$updateId"

        val client: HttpClient = HttpClient.newBuilder().build()

        val requestGetUpdates: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val responseGetUpdates: HttpResponse<String> =
            client.send(requestGetUpdates, HttpResponse.BodyHandlers.ofString())

        return responseGetUpdates.body()
    }
}