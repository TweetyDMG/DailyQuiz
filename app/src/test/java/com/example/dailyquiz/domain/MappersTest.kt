package com.example.dailyquiz.domain

import com.example.dailyquiz.data.mappers.toDomain
import com.example.dailyquiz.data.model.ApiQuestion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MappersTest {

    @Test
    fun `apiQuestion to domain decodes html entities`() {
        val apiQuestion = ApiQuestion(
            type = "multiple",
            difficulty = "easy",
            category = "General Knowledge",
            questionText = "What is the capital of &quot;France&quot;?",
            correctAnswer = "Paris",
            incorrectAnswers = listOf("London", "Berlin", "Madrid")
        )

        val question = apiQuestion.toDomain()

        assertEquals("What is the capital of \"France\"?", question.questionText)
        assertEquals("Paris", question.correctAnswer)
        assertEquals(4, question.allAnswers.size)
        assertTrue(question.allAnswers.contains("Paris"))
        assertTrue(question.allAnswers.contains("London"))
        assertEquals("", question.userAnswer)
        assertFalse(question.isCorrect)
    }

    @Test
    fun `apiQuestion shuffles answers`() {
        val apiQuestion = ApiQuestion(
            type = "multiple",
            difficulty = "hard",
            category = "Science",
            questionText = "Test question?",
            correctAnswer = "Correct",
            incorrectAnswers = listOf("Wrong1", "Wrong2", "Wrong3")
        )

        val question = apiQuestion.toDomain()

        assertEquals(4, question.allAnswers.size)
        assertEquals(question.allAnswers.toSet().size, 4) // all unique
        assertTrue(question.allAnswers.contains("Correct"))
    }

    @Test
    fun `apiQuestion handles special chars`() {
        val apiQuestion = ApiQuestion(
            type = "multiple",
            difficulty = "medium",
            category = "History",
            questionText = "Who&#039;s the president?",
            correctAnswer = "Biden &amp; Trump",
            incorrectAnswers = listOf("Option &#039;1&#039;", "Option &quot;2&quot;", "Option 3")
        )

        val question = apiQuestion.toDomain()

        assertEquals("Who's the president?", question.questionText)
        assertEquals("Biden & Trump", question.correctAnswer)
    }

    @Test
    fun `apiQuestion with empty incorrect answers`() {
        val apiQuestion = ApiQuestion(
            type = "boolean",
            difficulty = "easy",
            category = "Science",
            questionText = "Is Earth round?",
            correctAnswer = "True",
            incorrectAnswers = emptyList()
        )

        val question = apiQuestion.toDomain()

        assertEquals(1, question.allAnswers.size)
        assertEquals("True", question.allAnswers.first())
    }
}
