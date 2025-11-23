package org.example

fun main(args: Array<String>) {

    val botToken = args[0]
    val tgBotService = TelegramBotService(botToken)
    var updateId = 0L

    val updateIdRegex = "\"update_id\":\\s*(\\d+)".toRegex()
    val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val chatIdRegex = "\"chat\"\\s*:\\s*\\{\\s?\"id\"\\s*:\\s*(\\d+)".toRegex()

    while (true) {
        val updates = tgBotService.getUpdates(updateId)
        println(updates)

        updateId = (updateIdRegex.find(updates)?.groups?.get(1)?.value?.toLong() ?: 0) + 1

        val text = messageTextRegex.find(updates)?.groups?.get(1)?.value
        println(text)
        Thread.sleep(2000)

        if (updateId != 1L && text.equals("hello", ignoreCase = true)) {

            val chatId = chatIdRegex.find(updates)?.groups?.get(1)?.value?.toLong() ?: 0
            val response = tgBotService.sendMessage(chatId, "hello!")
        }
    }
}