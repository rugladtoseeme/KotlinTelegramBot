package org.example

import java.io.File

const val WORDS_FILE_NAME = "words.txt"
const val RIGHT_ANSWERS_THRESHOLD = 3
const val QUESTION_WORDS_SIZE = 4

data class Word(val original: String, val translation: String, var correctAnswersCount: Int = 0)

fun loadDictionary(): MutableList<Word> {
    val file = File(WORDS_FILE_NAME)
    val dictionary = mutableListOf<Word>()
    val lines = file.readLines()
    for (line in lines) {
        val wordParts = line.split('|')
        dictionary.add(Word(wordParts[0], wordParts[1], wordParts[2].toIntOrNull() ?: 0))
    }
    return dictionary
}

fun getNotLearned(dictionary: List<Word>) = dictionary.filter { it.correctAnswersCount < RIGHT_ANSWERS_THRESHOLD }

fun main() {

    val dictionary = loadDictionary()
    val notLearnedList = getNotLearned(dictionary)

    while (true) {
        println("Меню: \n1 – Учить слова\n2 – Статистика\n0 – Выход")
        val choice = readln().toIntOrNull()
        when {
            choice == 1 -> {
                println("Учить слова:")

                while (true) {
                    if (notLearnedList.isEmpty()) {
                        println("Все слова в словаре выучены.")
                        break
                    } else {

                        val questionWords = notLearnedList.shuffled().take(QUESTION_WORDS_SIZE)
                        val questionWord = questionWords[(0..QUESTION_WORDS_SIZE - 1).random()]
                        println("\n${questionWord.original}:")
                        var counter = 1
                        for (word in questionWords) {
                            println(" ${counter++} - ${word.translation}")
                        }
                        println()
                        val answerNumber = readln().toInt()
                        if (!(answerNumber in 1..QUESTION_WORDS_SIZE) ||
                            questionWord.translation != questionWords[answerNumber - 1].translation
                        ) {
                            println("Неправильно! ${questionWord.original} – это ${questionWord.translation}")
                            continue
                        } else {
                            println("Правильно!")
                            questionWord.correctAnswersCount++
                        }
                    }
                }
            }

            choice == 2 -> {
                println("Статистика.")
                val totalCount = dictionary.size
                val learnedCount = dictionary.filter { it.correctAnswersCount >= RIGHT_ANSWERS_THRESHOLD }.count()
                println("Выучено ${learnedCount} из $totalCount слов | ${(learnedCount.toDouble() / (if (totalCount == 0) 1 else 0)) * 100}%\n")
            }

            choice == 0 -> break
            else -> println("Введите число 1, 2 или 0")
        }
    }

    println("Работа завершена.")
}