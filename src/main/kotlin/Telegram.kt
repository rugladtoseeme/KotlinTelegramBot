package org.example

const val MENU_STATISTICS_MESSAGE = "Выучено 10 из 10 слов | 100%"
const val MENU_STATISTICS_DATA_KEY = "statistics_clicked"
const val MENU_LEARN_MESSAGE = "Учить английские слова"
const val MENU_LEARN_DATA_KEY = "words_learning_cliched"

const val BUTTON_TEXT_STATISTICS = "Статистика"
const val BUTTON_TEXT_LEARN_WORDS = "Изучить слова"

const val MENU_COMMAND = "menu"
const val HELLO_COMMAND = "hello"
const val HELLO_MESSAGE = "hello!"

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

        if (text.equals(HELLO_COMMAND, ignoreCase = true)) {
            val response = tgBotService.sendMessage(chatId, HELLO_MESSAGE)
        }

        if (text.equals(MENU_COMMAND, ignoreCase = true)) {
            val response = tgBotService.sendMenu(chatId)
        }

        if (data.equals(MENU_LEARN_DATA_KEY, ignoreCase = true)) {
            val response = tgBotService.sendMessage(
                chatId, MENU_LEARN_MESSAGE,
            )
        }

        if (data.equals(MENU_STATISTICS_DATA_KEY, ignoreCase = true)) {
            val response = tgBotService.sendMessage(
                chatId, MENU_STATISTICS_MESSAGE
            )
        }
    }
}