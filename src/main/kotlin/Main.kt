package org.example

import java.io.File

const val WORDS_FILE_NAME = "words.txt"
const val RIGHT_ANSWERS_THRESHOLD = 3
const val QUESTION_WORDS_SIZE = 4

data class Word(val original: String, val translation: String, var correctAnswersCount: Int = 0)

fun loadDictionary(): MutableList<Word> {
    val file = File(WORDS_FILE_NAME)
    val lines = file.readLines()
    return linesToWords(lines)
}

fun getNotLearned(dictionary: List<Word>) = dictionary.filter { it.correctAnswersCount < RIGHT_ANSWERS_THRESHOLD }

fun main() {

    var dictionary = loadDictionary()
    var notLearnedList = getNotLearned(dictionary)

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
                    }

                    val questionWords = notLearnedList.shuffled().take(QUESTION_WORDS_SIZE)
                    val questionWord = questionWords[(0..questionWords.size - 1).random()]
                    println("\n${questionWord.original}:")
                    questionWords.forEachIndexed { index, word -> println(" ${index + 1} - ${word.translation}") }
                    println()
                    val answerNumber = readln().toIntOrNull()
                    if (answerNumber == null || !(answerNumber in 1..questionWords.size) ||
                        questionWord.translation != questionWords[answerNumber - 1].translation
                    ) {
                        println("Неправильно! ${questionWord.original} – это ${questionWord.translation}")
                        continue
                    } else {
                        println("Правильно!")
                        questionWord.correctAnswersCount++
                        dictionary = saveDictionary(questionWord)
                        notLearnedList = getNotLearned(dictionary)
                    }
                }
            }

            choice == 2 -> {
                println("Статистика.")
                val totalCount = dictionary.size
                val learnedCount = dictionary.filter { it.correctAnswersCount >= RIGHT_ANSWERS_THRESHOLD }.count()
                println("Выучено ${learnedCount} из $totalCount слов | ${(learnedCount.toDouble() / (if (totalCount == 0) 1 else totalCount)) * 100}%\n")
            }

            choice == 0 -> break
            else -> println("Введите число 1, 2 или 0")
        }
    }

    println("Работа завершена.")
}

fun saveDictionary(word: Word): MutableList<Word> {
    val file = File(WORDS_FILE_NAME)
    var lines = file.readLines().minusElement("${word.original}|${word.translation}|${word.correctAnswersCount - 1}")
        .toMutableList()
    lines.add("${word.original}|${word.translation}|${word.correctAnswersCount}")
    file.writeText(lines.joinToString("\n"))

    return linesToWords(lines)
}

fun linesToWords(lines: List<String>): MutableList<Word> {
    val dictionary = mutableListOf<Word>()
    for (line in lines) {
        val wordParts = line.split('|')
        dictionary.add(Word(wordParts[0], wordParts[1], wordParts[2].toIntOrNull() ?: 0))
    }
    return dictionary
}