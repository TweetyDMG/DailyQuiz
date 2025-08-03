package com.example.dailyquiz.domain.model

data class QuizAttempt(
    val id: Int = 0,
    val timestamp: Long,
    val score: Int,
    val questions: List<Question>
)