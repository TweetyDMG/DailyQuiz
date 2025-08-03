package com.example.dailyquiz.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "quiz_attempts")
data class QuizAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val score: Int
)

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val attemptId: Int,
    val questionText: String,
    val allAnswers: List<String>,
    val correctAnswer: String,
    val userAnswer: String,
    val isCorrect: Boolean
)

data class QuizAttemptWithQuestions(
    @Embedded val attempt: QuizAttemptEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "attemptId"
    )
    val questions: List<QuestionEntity>
)