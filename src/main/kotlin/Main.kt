package org.example

import java.io.File

data class Word(val original: String, val translation: String, var correctAnswersCount: Int = 0)

fun main() {
    val file = File("words.txt")

    val dictionary = mutableListOf<Word>()
    val lines = file.readLines()
    for (line in lines) {
        val wordParts = line.split('|')
        dictionary.add(Word(wordParts[0], wordParts[1], wordParts[2].toIntOrNull() ?: 0))
    }
}