package org.example

import java.io.File

const val WORDS_FILE_NAME = "words.txt"

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

fun main() {

    val dictionary = loadDictionary()

    while (true) {
        println("Меню: \n1 – Учить слова\n2 – Статистика\n0 – Выход")
        val choice = readln().toIntOrNull()
        when {
            choice == 1 -> println("Учить слова")
            choice == 2 -> {
                println("Статистика.")
                val totalCount = dictionary.size
                val learnedCount = dictionary.filter { it.correctAnswersCount >= 3 }.count()
                println("Выучено ${learnedCount} из $totalCount слов | ${(learnedCount.toDouble() / totalCount) * 100}%")
            }

            choice == 0 -> break
            else -> println("Введите число 1, 2 или 0")
        }
    }

    println("Работа завершена.")
}