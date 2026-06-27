package com.example.dailyquiz.domain.model

data class Question(
    val questionText: String,
    val allAnswers: List<String>,
    val correctAnswer: String,
    val userAnswer: String,
    val isCorrect: Boolean
)