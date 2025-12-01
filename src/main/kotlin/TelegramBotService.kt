package org.example

import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

class TelegramBotService(val botToken: String) {

    val client = HttpClient.newBuilder().build()

    fun sendMessage(chatId: Long, messageText: String): String {

        val urlGetUpdates =
            "$URL_TELEGRAM_API$botToken/sendMessage?chat_id=$chatId&text=${
                URLEncoder.encode(messageText, StandardCharsets.UTF_8)
            }"
        val requestGetUpdates: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        try {

            val responseGetUpdates: HttpResponse<String> =
                client.send(requestGetUpdates, HttpResponse.BodyHandlers.ofString())

            return responseGetUpdates.body()
        } catch (e: Exception) {
            throw e
        }
    }

    fun getUpdates(updateId: Long): String {

        val urlGetUpdates = "$URL_TELEGRAM_API$botToken/getUpdates?offset=$updateId"

        val requestGetUpdates: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val responseGetUpdates: HttpResponse<String> =
            client.send(requestGetUpdates, HttpResponse.BodyHandlers.ofString())

        return responseGetUpdates.body()
    }

    fun sendMenu(chatId: Long): String {
        val urlGetUpdates =
            "$URL_TELEGRAM_API$botToken/sendMessage"

        val sendMenuBody = """
            {
                "chat_id": $chatId,
                "text": "Основное меню",
                "reply_markup":
                {
                    "inline_keyboard":
                    [
            			[
            				{
            				"text":"Изучить слова",
            				"callback_data":"words_learning_cliched"	
            				},
            				{
            				"text":"Статистика",
            				"callback_data":"statistics_clicked"	
            				}
            			]
            	    ]
                }
            }
        """.trimIndent()

        val requestGetUpdates: HttpRequest =
            HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).header("Content-type", "application/json").POST(
                HttpRequest.BodyPublishers.ofString(sendMenuBody)
            ).build()

        try {
            val responseGetUpdates: HttpResponse<String> =
                client.send(requestGetUpdates, HttpResponse.BodyHandlers.ofString())

            return responseGetUpdates.body()
        } catch (e: Exception) {
            throw e
        }
    }
}