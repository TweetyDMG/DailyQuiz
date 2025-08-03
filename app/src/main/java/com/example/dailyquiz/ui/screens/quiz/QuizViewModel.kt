package com.example.dailyquiz.ui.screens.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyquiz.domain.model.Question
import com.example.dailyquiz.domain.model.QuizAttempt
import com.example.dailyquiz.domain.use_case.GetNewQuizUseCase
import com.example.dailyquiz.domain.use_case.SaveQuizAttemptUseCase
import com.example.dailyquiz.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class QuizState { START, LOADING, IN_PROGRESS, RESULTS, ERROR }

data class QuizUiState(
    val quizState: QuizState = QuizState.START,
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedAnswer: String = "",
    val score: Int = 0,
    val errorMessage: String = "",
    val highlightAnswer: Boolean = false
) {
    val currentQuestion: Question?
        get() = questions.getOrNull(currentQuestionIndex)
}

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val getNewQuizUseCase: GetNewQuizUseCase,
    private val saveQuizAttemptUseCase: SaveQuizAttemptUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    fun startQuiz() {
        viewModelScope.launch {
            _uiState.update { it.copy(quizState = QuizState.LOADING) }
            when (val result = getNewQuizUseCase()) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            quizState = QuizState.IN_PROGRESS,
                            questions = result.data ?: emptyList(),
                            currentQuestionIndex = 0,
                            score = 0,
                            selectedAnswer = ""
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            quizState = QuizState.ERROR,
                            errorMessage = result.message ?: "Неизвестная ошибка"
                        )
                    }
                }
            }
        }
    }

    fun onAnswerSelected(answer: String) {
        _uiState.update { it.copy(selectedAnswer = answer) }
    }

    fun onNextClicked(highlight: Boolean = false) {
        if (_uiState.value.selectedAnswer.isEmpty()) return

        if (highlight) {
            _uiState.update { it.copy(highlightAnswer = true) }
            return
        }

        val currentState = _uiState.value
        val currentQuestion = currentState.currentQuestion ?: return
        val isCorrect = currentState.selectedAnswer == currentQuestion.correctAnswer

        val updatedQuestions = currentState.questions.toMutableList()
        updatedQuestions[currentState.currentQuestionIndex] = currentQuestion.copy(
            userAnswer = currentState.selectedAnswer,
            isCorrect = isCorrect
        )

        val newScore = if (isCorrect) currentState.score + 1 else currentState.score

        if (currentState.currentQuestionIndex < currentState.questions.size - 1) {
            _uiState.update {
                it.copy(
                    questions = updatedQuestions,
                    currentQuestionIndex = it.currentQuestionIndex + 1,
                    selectedAnswer = "",
                    score = newScore,
                    highlightAnswer = false
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    questions = updatedQuestions,
                    score = newScore,
                    quizState = QuizState.RESULTS,
                    highlightAnswer = false
                )
            }
            saveResult(updatedQuestions, newScore)
        }
    }

    private fun saveResult(questions: List<Question>, score: Int) {
        viewModelScope.launch {
            val attempt = QuizAttempt(
                timestamp = System.currentTimeMillis(),
                score = score,
                questions = questions
            )
            saveQuizAttemptUseCase(attempt)
        }
    }

    fun resetQuiz() {
        _uiState.value = QuizUiState()
    }
}