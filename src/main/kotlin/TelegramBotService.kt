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
        val urlSendMessage =
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
            				"text":"$BUTTON_TEXT_LEARN_WORDS",
            				"callback_data":"$MENU_LEARN_DATA_KEY"	
            				},
            				{
            				"text":"$BUTTON_TEXT_STATISTICS",
            				"callback_data":"$MENU_STATISTICS_DATA_KEY"	
            				}
            			]
            	    ]
                }
            }
        """.trimIndent()

        val requestSendMenu: HttpRequest =
            HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).header("Content-type", "application/json").POST(
                HttpRequest.BodyPublishers.ofString(sendMenuBody)
            ).build()

        try {
            val responseSendMenu: HttpResponse<String> =
                client.send(requestSendMenu, HttpResponse.BodyHandlers.ofString())

            return responseSendMenu.body()
        } catch (e: Exception) {
            throw e
        }
    }

    fun sendQuestion(chatId: Long, question: Question): String {
        val urlSendMessage =
            "$URL_TELEGRAM_API$botToken/sendMessage"

        val sendQuestionBody = """
            {
                "chat_id": $chatId,
                "text": "${question.correctAnswer.original}",
                "reply_markup":
                {
                    "inline_keyboard":
                    [
            				${
            question.variants.mapIndexed { index, value -> "[{\"text\":\"${value.translation}\", \"callback_data\":\"${CALLBACK_DATA_ANSWER_PREFIX}$index\" }]" }
                .joinToString(", ")
                            },
                            [{"text":"<-- меню",
            				"callback_data":"menu"
                            }]
            	    ]
                }
            }
        """.trimIndent()

        val requestSendMessage: HttpRequest =
            HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).header("Content-type", "application/json")
                .POST(
                    HttpRequest.BodyPublishers.ofString(sendQuestionBody)
                ).build()

        try {
            val responseSendMessage: HttpResponse<String> =
                client.send(requestSendMessage, HttpResponse.BodyHandlers.ofString())
            return responseSendMessage.body()
        } catch (e: Exception) {
            throw e
        }

    }
}