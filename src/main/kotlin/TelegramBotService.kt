package org.example

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

@Serializable
data class SendMessageRequest(
    @SerialName("chat_id") val chatId: Long,
    val text: String,
    @SerialName("reply_markup") val replyMarkup: ReplyMarkup
)

@Serializable
data class ReplyMarkup(
    @SerialName("inline_keyboard") val inlineKeyboard: List<List<InlineKeyboard>>
)

@Serializable
data class InlineKeyboard(
    val text: String,
    @SerialName("callback_data") val callbackData: String,
)

class TelegramBotService(val botToken: String, val json: Json) {

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

    fun getUpdates(updateId: Long): Response {

        val urlGetUpdates = "$URL_TELEGRAM_API$botToken/getUpdates?offset=$updateId"

        val requestGetUpdates: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val responseGetUpdates: HttpResponse<String> =
            client.send(requestGetUpdates, HttpResponse.BodyHandlers.ofString())
        return json.decodeFromString(responseGetUpdates.body())
    }

    fun sendMenu(chatId: Long): String {
        val urlSendMessage =
            "$URL_TELEGRAM_API$botToken/sendMessage"

        val sendMenuRequest = SendMessageRequest(
            chatId = chatId,
            text = "Основное меню",
            replyMarkup = ReplyMarkup(
                listOf(
                    listOf(
                        InlineKeyboard(BUTTON_TEXT_LEARN_WORDS, MENU_LEARN_DATA_KEY),
                        InlineKeyboard(BUTTON_TEXT_STATISTICS, MENU_STATISTICS_DATA_KEY)
                    )
                )
            )
        )
        val requestSendMenu: HttpRequest =
            HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).header("Content-type", "application/json").POST(
                HttpRequest.BodyPublishers.ofString(json.encodeToString(sendMenuRequest))
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

        val sendQuestionRequest = SendMessageRequest(
            chatId,
            text = question.correctAnswer.original,
            replyMarkup = ReplyMarkup(question.variants.mapIndexed { index, value ->
                listOf(
                    InlineKeyboard(
                        text = value.translation,
                        callbackData = "${CALLBACK_DATA_ANSWER_PREFIX}$index"
                    )
                )
            } + listOf(
                listOf(
                    InlineKeyboard(text = "<-- меню", callbackData = "menu")
                )
            )))

        val requestSendMessage: HttpRequest =
            HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).header("Content-type", "application/json")
                .POST(
                    HttpRequest.BodyPublishers.ofString(json.encodeToString(sendQuestionRequest))
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