package org.example

const val WORDS_FILE_NAME = "words.txt"
const val RIGHT_ANSWERS_THRESHOLD = 3
const val QUESTION_WORDS_SIZE = 4

fun main() {

    val trainer = try {
        LearnWordsTrainer()
    } catch (e: Exception) {
        println("Невозможно загрузить словарь")
        return
    }

    while (true) {
        println("Меню: \n1 – Учить слова\n2 – Статистика\n0 – Выход")
        val choice = readln().toIntOrNull()
        when (choice) {
            1 -> {
                println("Учить слова:")

                while (true) {
                    val question = trainer.getNextQuestion()

                    if (question?.variants?.isEmpty() != false) {
                        println("Все слова в словаре выучены.")
                        break
                    }

                    println(question.questionToString())
                    val answerNumber = readln().toIntOrNull()
                    if (answerNumber != null) {
                        if (answerNumber == 0) break
                        else {
                            if (trainer.checkAnswer(answerNumber.minus(1))) println("Правильно!")
                            else println("Неправильно! ${question.correctAnswer.original} – это ${question.correctAnswer.translation}")
                        }
                    }
                }
            }

            2 -> {
                println("Статистика.")
                val statistics = trainer.getStatistics()
                println(
                    "Выучено ${statistics.learned} из ${statistics.total} слов | ${
                        String.format(
                            "%.2f",
                            statistics.percent
                        )
                    }%\n"
                )
            }

            0 -> break
            else -> println("Введите число 1, 2 или 0")
        }
    }

    println("Работа завершена.")
}