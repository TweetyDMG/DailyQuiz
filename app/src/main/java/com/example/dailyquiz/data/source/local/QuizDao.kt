package com.example.dailyquiz.data.source.local

import androidx.room.*
import com.example.dailyquiz.data.model.QuestionEntity
import com.example.dailyquiz.data.model.QuizAttemptEntity
import com.example.dailyquiz.data.model.QuizAttemptWithQuestions
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizAttempt(attempt: QuizAttemptEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Transaction
    @Query("SELECT * FROM quiz_attempts ORDER BY timestamp DESC")
    fun getAllAttemptsWithQuestions(): Flow<List<QuizAttemptWithQuestions>>

    @Transaction
    @Query("SELECT * FROM quiz_attempts WHERE id = :attemptId")
    suspend fun getAttemptWithQuestionsById(attemptId: Int): QuizAttemptWithQuestions?

    @Query("DELETE FROM quiz_attempts WHERE id = :attemptId")
    suspend fun deleteAttemptById(attemptId: Int)
}