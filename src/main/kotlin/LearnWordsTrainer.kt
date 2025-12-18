package org.example

import java.io.File

data class Word(val original: String, val translation: String, var correctAnswersCount: Int = 0) {
    fun toDictionaryFormat() = "${original}|${translation}|${correctAnswersCount}"
}

data class Statistics(
    val learned: Int,
    val total: Int,
    val percent: Double = (learned.toDouble() / (if (total == 0) 1 else total)) * 100
)

data class Question(val correctAnswer: Word, val variants: List<Word>) {

    fun questionToString(): String {
        val variantsStr =
            variants.mapIndexed { index, word -> " ${index + 1} - ${word.translation}" }.joinToString("\n")
        return "\n${correctAnswer.original}:\n" + variantsStr + "\n ---------\n 0 - выйти в меню"
    }
}

class LearnWordsTrainer(private val wordsFileName: String = DEFAULT_WORDS_FILE_NAME) {

    var currentQuestion: Question? = null
    private var dictionary = loadDictionary()

    fun getNextQuestion(): Question? {
        val notLearnedWords = dictionary.filter { it.correctAnswersCount < RIGHT_ANSWERS_THRESHOLD }
        if (notLearnedWords.isEmpty()) return null

        val variants = if (notLearnedWords.size < QUESTION_WORDS_SIZE) {
            notLearnedWords.shuffled() + dictionary.filter { it.correctAnswersCount >= RIGHT_ANSWERS_THRESHOLD }
                .shuffled()
                .take(QUESTION_WORDS_SIZE - notLearnedWords.size)
        } else {
            notLearnedWords.shuffled().take(QUESTION_WORDS_SIZE)
        }.shuffled()

        currentQuestion = Question(variants.random(), variants)
        return currentQuestion
    }

    fun checkAnswer(answerIndex: Int): Boolean {
        return currentQuestion.let {
            if (answerIndex in (0 until (currentQuestion?.variants?.size ?: 0)) &&
                currentQuestion?.correctAnswer?.translation == currentQuestion?.variants[answerIndex]?.translation
            ) {
                currentQuestion?.correctAnswer?.correctAnswersCount++
                saveDictionary()
                true
            } else false
        }
    }

    fun getStatistics(): Statistics {
        val totalCount = dictionary.size
        val learnedCount = dictionary.filter { it.correctAnswersCount >= RIGHT_ANSWERS_THRESHOLD }.count()
        return Statistics(learnedCount, totalCount)
    }

    private fun loadDictionary(): MutableList<Word> {

        dictionary = mutableListOf()
        val file = createFileIfNotExists()
        val lines = file.readLines()
        for (line in lines) {
            val wordParts = line.split('|')
            dictionary.add(Word(wordParts[0], wordParts[1], wordParts[2].toIntOrNull() ?: 0))
        }
        return dictionary
    }

    private fun saveDictionary() {
        val file = createFileIfNotExists()
        val lines = mutableListOf<String>()
        for (word in dictionary) {
            lines.add(word.toDictionaryFormat())
        }
        file.writeText(lines.joinToString("\n"))
    }

    private fun createFileIfNotExists(): File {
        val file = File(wordsFileName)
        if (!file.exists()) {
            File(DEFAULT_WORDS_FILE_NAME).copyTo(file)
        }
        return file
    }

    fun resetProgress() {
        dictionary.forEach { it.correctAnswersCount = 0 }
        saveDictionary()
    }
}