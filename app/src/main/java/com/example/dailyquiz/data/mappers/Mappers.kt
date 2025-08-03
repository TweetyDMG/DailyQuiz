package com.example.dailyquiz.data.mappers

import com.example.dailyquiz.data.model.ApiQuestion
import com.example.dailyquiz.data.model.QuestionEntity
import com.example.dailyquiz.data.model.QuizAttemptEntity
import com.example.dailyquiz.data.model.QuizAttemptWithQuestions
import com.example.dailyquiz.domain.model.Question
import com.example.dailyquiz.domain.model.QuizAttempt
import org.jsoup.Jsoup

fun ApiQuestion.toDomain(): Question {
    val allAnswers = (incorrectAnswers + correctAnswer).shuffled()
    return Question(
        questionText = Jsoup.parse(questionText).text(),
        allAnswers = allAnswers.map { Jsoup.parse(it).text() },
        correctAnswer = Jsoup.parse(correctAnswer).text(),
        userAnswer = "",
        isCorrect = false
    )
}

fun QuizAttempt.toEntity(): QuizAttemptEntity {
    return QuizAttemptEntity(
        id = id,
        timestamp = timestamp,
        score = score
    )
}

fun Question.toEntity(attemptId: Int): QuestionEntity {
    return QuestionEntity(
        attemptId = attemptId,
        questionText = questionText,
        allAnswers = allAnswers,
        correctAnswer = correctAnswer,
        userAnswer = userAnswer,
        isCorrect = isCorrect
    )
}

fun QuizAttemptWithQuestions.toDomain(): QuizAttempt {
    return QuizAttempt(
        id = this.attempt.id,
        timestamp = this.attempt.timestamp,
        score = this.attempt.score,
        questions = this.questions.map { it.toDomain() }
    )
}

fun QuestionEntity.toDomain(): Question {
    return Question(
        questionText = questionText,
        allAnswers = allAnswers,
        correctAnswer = correctAnswer,
        userAnswer = userAnswer,
        isCorrect = isCorrect
    )
}