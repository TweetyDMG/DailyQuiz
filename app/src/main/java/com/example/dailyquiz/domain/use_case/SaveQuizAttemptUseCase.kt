package com.example.dailyquiz.domain.use_case

import com.example.dailyquiz.domain.model.QuizAttempt
import com.example.dailyquiz.domain.repository.QuizRepository
import javax.inject.Inject

class SaveQuizAttemptUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    suspend operator fun invoke(attempt: QuizAttempt) = repository.saveQuizAttempt(attempt)
}