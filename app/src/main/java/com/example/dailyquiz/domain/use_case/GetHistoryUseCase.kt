package com.example.dailyquiz.domain.use_case

import com.example.dailyquiz.domain.repository.QuizRepository
import javax.inject.Inject

class GetHistoryUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    operator fun invoke() = repository.getHistory()
}