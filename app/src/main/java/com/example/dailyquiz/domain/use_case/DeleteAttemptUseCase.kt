package com.example.dailyquiz.domain.use_case

import com.example.dailyquiz.domain.repository.QuizRepository
import javax.inject.Inject

class DeleteAttemptUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    suspend operator fun invoke(attemptId: Int) = repository.deleteAttempt(attemptId)
}