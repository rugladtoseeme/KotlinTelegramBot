package org.example

const val MENU_STATISTICS_DATA_KEY = "statistics_clicked"
const val MENU_LEARN_DATA_KEY = "words_learning_cliched"

const val BUTTON_TEXT_STATISTICS = "Статистика"
const val BUTTON_TEXT_LEARN_WORDS = "Изучить слова"

const val MENU_COMMAND = "menu"

const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"

fun main(args: Array<String>) {

    val trainer = try {
        LearnWordsTrainer()
    } catch (e: Exception) {
        println("Невозможно загрузить словарь")
        return
    }

    val botToken = args[0]
    val tgBotService = TelegramBotService(botToken)
    var updateId = 0L

    val updateIdRegex = "\"update_id\":\\s*(\\d+)".toRegex()
    val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val chatIdRegex = "\"chat\"\\s*:\\s*\\{\\s?\"id\"\\s*:\\s*(-*\\d+)".toRegex()
    val dataRegex = "\"data\":\"(.+?)\"".toRegex()

    var question: Question? = null

    while (true) {
        val updates = tgBotService.getUpdates(updateId)
        println(updates)

        updateId = (updateIdRegex.find(updates)?.groups?.get(1)?.value?.toLong() ?: 0) + 1

        val chatId: Long = chatIdRegex.find(updates)?.groups?.get(1)?.value?.toLongOrNull() ?: continue

        val text = messageTextRegex.find(updates)?.groups?.get(1)?.value

        val data = dataRegex.find(updates)?.groups?.get(1)?.value

        println(text)
        Thread.sleep(2000)

        if (text.equals(MENU_COMMAND, ignoreCase = true) || data.equals(MENU_COMMAND, ignoreCase = true)) {
            val response = tgBotService.sendMenu(chatId)
        }

        if (data.equals(MENU_STATISTICS_DATA_KEY, ignoreCase = true)) {

            val statistics = trainer.getStatistics()

            val statisticsStr = "Выучено ${statistics.learned} из ${statistics.total} слов | ${
                String.format(
                    "%.2f",
                    statistics.percent
                )
            }%\n"

            val response = tgBotService.sendMessage(
                chatId, statisticsStr
            )
        }

        if (question != null && data?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) == true) {
            val answerIndex = data.removePrefix(CALLBACK_DATA_ANSWER_PREFIX).toIntOrNull()
            val response =
                if (answerIndex != null && trainer.checkAnswer(answerIndex)) {
                    tgBotService.sendMessage(
                        chatId, "Правильно!"
                    )
                } else tgBotService.sendMessage(
                    chatId,
                    "Неправильно! ${question.correctAnswer.original} – это ${question.correctAnswer.translation}"
                )

            question = checkNextQuestionAndSend(chatId, tgBotService, trainer)
        }

        if (data.equals(MENU_LEARN_DATA_KEY, ignoreCase = true)) {

            question = checkNextQuestionAndSend(chatId, tgBotService, trainer)
        }
    }
}

fun checkNextQuestionAndSend(
    chatId: Long,
    tgBotService: TelegramBotService,
    trainer: LearnWordsTrainer
): Question? {
    val question = trainer.getNextQuestion()
    val response = if (question == null) {
        tgBotService.sendMessage(chatId, "Вы выучили все слова в базе!")
    } else {
        tgBotService.sendQuestion(
            chatId, question,
        )
    }
    return question
}
