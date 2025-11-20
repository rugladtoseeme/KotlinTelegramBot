package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0L

    while (true) {
        Thread.sleep(2000)
        val updates = getUpdates(botToken, updateId)
        println(updates)

        val updateIdRegex = "\"update_id\":(.+?),\n\"".toRegex()
        updateId = (updateIdRegex.find(updates)?.groups?.get(1)?.value?.toLong() ?: 0) + 1

        val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
        val text = messageTextRegex.find(updates)?.groups?.get(1)?.value
        println(text)
    }
}

fun getUpdates(botToken: String, updateId: Long): String {

    val urlGetUpdates = "https://api.telegram.org/bot$botToken/getUpdates?offset=$updateId"

    val client: HttpClient = HttpClient.newBuilder().build()

    val requestGetUpdates: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
    val responseGetUpdates: HttpResponse<String> = client.send(requestGetUpdates, HttpResponse.BodyHandlers.ofString())

    return responseGetUpdates.body()
}
