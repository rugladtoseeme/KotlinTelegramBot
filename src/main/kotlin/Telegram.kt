package org.example

fun main(args: Array<String>) {

    val botToken = args[0]
    val tgBotService = TelegramBotService(botToken)
    var updateId = 0L

    val updateIdRegex = "\"update_id\":\\s*(\\d+)".toRegex()
    val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val chatIdRegex = "\"chat\"\\s*:\\s*\\{\\s?\"id\"\\s*:\\s*(\\d+)".toRegex()
    val dataRegex = "\"data\":\"(.+?)\"".toRegex()

    while (true) {
        val updates = tgBotService.getUpdates(updateId)
        println(updates)

        updateId = (updateIdRegex.find(updates)?.groups?.get(1)?.value?.toLong() ?: 0) + 1

        val chatId: Long = chatIdRegex.find(updates)?.groups?.get(1)?.value?.toLongOrNull() ?: continue

        val text = messageTextRegex.find(updates)?.groups?.get(1)?.value

        val data = dataRegex.find(updates)?.groups?.get(1)?.value

        println(text)
        Thread.sleep(2000)

        if (text.equals("hello", ignoreCase = true)) {
            val response = tgBotService.sendMessage(chatId, "hello!")
        }

        if (text.equals("menu", ignoreCase = true)) {
            val response = tgBotService.sendMenu(chatId)
        }

        if (data.equals("words_learning_cliched", ignoreCase = true)) {
            val response = tgBotService.sendMessage(
                chatId, "Учить английские слова",
            )
        }

        if (data.equals("statistics_clicked", ignoreCase = true)) {
            val response = tgBotService.sendMessage(
                chatId, "Выучено 10 из 10 слов | 100%"
            )
        }
    }
}