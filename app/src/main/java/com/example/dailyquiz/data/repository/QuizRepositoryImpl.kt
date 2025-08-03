package com.example.dailyquiz.data.repository

import com.example.dailyquiz.data.mappers.toDomain
import com.example.dailyquiz.data.mappers.toEntity
import com.example.dailyquiz.data.source.local.QuizDao
import com.example.dailyquiz.data.source.remote.ApiService
import com.example.dailyquiz.domain.model.Question
import com.example.dailyquiz.domain.model.QuizAttempt
import com.example.dailyquiz.domain.repository.QuizRepository
import com.example.dailyquiz.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class QuizRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val quizDao: QuizDao
) : QuizRepository {

    override suspend fun getNewQuiz(): Resource<List<Question>> {
        return try {
            val response = apiService.getQuestions()
            if (response.responseCode == 0) {
                Resource.Success(response.results.map { it.toDomain() })
            } else {
                Resource.Error("API returned an error: ${response.responseCode}")
            }
        } catch (e: IOException) {
            Resource.Error("Network error. Please check your connection.")
        } catch (e: Exception) {
            Resource.Error("An unexpected error occurred: ${e.localizedMessage}")
        }
    }

    override suspend fun saveQuizAttempt(attempt: QuizAttempt) {
        val attemptId = quizDao.insertQuizAttempt(attempt.toEntity()).toInt()
        val questionsWithAttemptId = attempt.questions.map { it.toEntity(attemptId) }
        quizDao.insertQuestions(questionsWithAttemptId)
    }

    override fun getHistory(): Flow<List<QuizAttempt>> {
        return quizDao.getAllAttemptsWithQuestions().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getAttemptDetails(attemptId: Int): QuizAttempt? {
        return quizDao.getAttemptWithQuestionsById(attemptId)?.toDomain()
    }

    override suspend fun deleteAttempt(attemptId: Int) {
        quizDao.deleteAttemptById(attemptId)
    }
}