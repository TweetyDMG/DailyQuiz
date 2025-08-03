package com.example.dailyquiz.domain.repository

import com.example.dailyquiz.domain.model.Question
import com.example.dailyquiz.domain.model.QuizAttempt
import com.example.dailyquiz.util.Resource
import kotlinx.coroutines.flow.Flow

interface QuizRepository {
    suspend fun getNewQuiz(): Resource<List<Question>>
    suspend fun saveQuizAttempt(attempt: QuizAttempt)
    fun getHistory(): Flow<List<QuizAttempt>>
    suspend fun getAttemptDetails(attemptId: Int): QuizAttempt?
    suspend fun deleteAttempt(attemptId: Int)
}