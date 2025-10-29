package org.example

import java.io.File

fun main() {
    val file = File("words.txt")
    val words = file.readLines()
    for (word in words) {
        println(word)
    }
}