package org.example

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

const val DEFAULT_WORDS_FILE_NAME = "words.txt"

const val MENU_STATISTICS_DATA_KEY = "statistics_clicked"
const val MENU_LEARN_DATA_KEY = "words_learning_cliched"
const val MENU_RESET_DATA_KEY = "reset_dictionary_cliched"

const val BUTTON_TEXT_STATISTICS = "Статистика"
const val BUTTON_TEXT_LEARN_WORDS = "Изучить слова"
const val BUTTON_TEXT_RESET_DICTIONARY = "Сбросить прогресс"

const val MENU_COMMAND = "menu"

const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"

@Serializable
data class Response(val result: List<Update>)

@Serializable
data class Update(
    @SerialName("update_id") val updateId: Long,
    val message: Message? = null,
    @SerialName("callback_query") val callbackQuery: CallbackQuery? = null
)

@Serializable
data class Message(val chat: Chat, val text: String = "")

@Serializable
data class CallbackQuery(val message: Message?, val data: String)

@Serializable
data class Chat(@SerialName("id") val chatId: Long)

fun main(args: Array<String>) {

    val json = Json { ignoreUnknownKeys = true }

    val chatsTrainersMap = HashMap<Long, LearnWordsTrainer>()
    val botToken = args[0]
    val tgBotService = TelegramBotService(botToken, json)
    var lastUpdateId = 0L

    while (true) {
        val response: Response = tgBotService.getUpdates(lastUpdateId)
        val sortedUpdates = response.result.sortedBy { it.updateId }
        sortedUpdates.forEach { handleUpdate(it, tgBotService, chatsTrainersMap) }
        lastUpdateId = sortedUpdates.lastOrNull()?.let { it.updateId + 1 } ?: continue
    }
}

fun handleUpdate(update: Update, tgBotService: TelegramBotService, trainers: HashMap<Long, LearnWordsTrainer>) {
    val text = update.message?.text
    val chatId = update.message?.chat?.chatId ?: update.callbackQuery?.message?.chat?.chatId ?: return
    val data = update.callbackQuery?.data

    val trainer = trainers.getOrPut(chatId) {
        LearnWordsTrainer(wordsFileName = "$chatId.txt")
    }

    println(text)
    Thread.sleep(2000)

    if (text.equals(MENU_COMMAND, ignoreCase = true) || data.equals(MENU_COMMAND, ignoreCase = true)) {
        tgBotService.sendMenu(chatId)
    }

    if (data.equals(MENU_STATISTICS_DATA_KEY, ignoreCase = true)) {

        val statistics = trainer.getStatistics()

        val statisticsStr = "Выучено ${statistics.learned} из ${statistics.total} слов | ${
            String.format(
                "%.2f",
                statistics.percent
            )
        }%\n"
        tgBotService.sendMessage(
            chatId, statisticsStr
        )
    }

    if (trainer.currentQuestion != null && data?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) == true) {
        val answerIndex = data.removePrefix(CALLBACK_DATA_ANSWER_PREFIX).toIntOrNull()
        if (answerIndex != null && trainer.checkAnswer(answerIndex)) {
            tgBotService.sendMessage(
                chatId, "Правильно!"
            )
        } else tgBotService.sendMessage(
            chatId,
            "Неправильно! ${trainer.currentQuestion?.correctAnswer?.original} – это ${trainer.currentQuestion?.correctAnswer?.translation}"
        )

        trainer.currentQuestion = checkNextQuestionAndSend(chatId, tgBotService, trainer)
    }

    if (data.equals(MENU_LEARN_DATA_KEY, ignoreCase = true)) {
        trainer.currentQuestion = checkNextQuestionAndSend(chatId, tgBotService, trainer)
    }

    if (data.equals(MENU_RESET_DATA_KEY, ignoreCase = true)) {
        trainer.resetProgress()
        tgBotService.sendMessage(chatId, "Прогресс изучения слов сброшен.")
    }
}

fun checkNextQuestionAndSend(
    chatId: Long,
    tgBotService: TelegramBotService,
    trainer: LearnWordsTrainer
): Question? {
    val question = trainer.getNextQuestion()
    if (question == null) {
        tgBotService.sendMessage(chatId, "Вы выучили все слова в базе!")
    } else {
        tgBotService.sendQuestion(
            chatId, question,
        )
    }
    return question
}