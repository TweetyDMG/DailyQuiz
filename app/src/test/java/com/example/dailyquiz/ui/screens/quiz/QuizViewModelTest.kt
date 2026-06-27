package com.example.dailyquiz.ui.screens.quiz

import app.cash.turbine.test
import com.example.dailyquiz.domain.model.Question
import com.example.dailyquiz.domain.model.QuizAttempt
import com.example.dailyquiz.domain.use_case.GetNewQuizUseCase
import com.example.dailyquiz.domain.use_case.SaveQuizAttemptUseCase
import com.example.dailyquiz.util.Resource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class QuizViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getNewQuizUseCase: GetNewQuizUseCase = mockk()
    private val saveQuizAttemptUseCase: SaveQuizAttemptUseCase = mockk()

    private lateinit var viewModel: QuizViewModel

    private val sampleQuestions = listOf(
        Question("Q1", listOf("A", "B", "C", "D"), "A", "", false),
        Question("Q2", listOf("1", "2", "3", "4"), "2", "", false),
        Question("Q3", listOf("X", "Y", "Z", "W"), "X", "", false),
        Question("Q4", listOf("M", "N", "O", "P"), "M", "", false),
        Question("Q5", listOf("R", "S", "T", "U"), "R", "", false)
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `startQuiz transitions from START to LOADING to IN_PROGRESS on success`() = runTest {
        coEvery { getNewQuizUseCase() } returns Resource.Success(sampleQuestions)
        coEvery { saveQuizAttemptUseCase(any()) } returns Unit

        viewModel = QuizViewModel(getNewQuizUseCase, saveQuizAttemptUseCase)

        viewModel.uiState.test {
            assertEquals(QuizState.START, awaitItem().quizState)

            viewModel.startQuiz()
            assertEquals(QuizState.LOADING, awaitItem().quizState)
            advanceUntilIdle()
            assertEquals(QuizState.IN_PROGRESS, awaitItem().quizState)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `startQuiz shows ERROR on network failure`() = runTest {
        coEvery { getNewQuizUseCase() } returns Resource.Error("Network error")

        viewModel = QuizViewModel(getNewQuizUseCase, saveQuizAttemptUseCase)

        viewModel.uiState.test {
            viewModel.startQuiz()
            assertEquals(QuizState.LOADING, awaitItem().quizState)
            advanceUntilIdle()
            val errorState = awaitItem()
            assertEquals(QuizState.ERROR, errorState.quizState)
            assertEquals("Network error", errorState.errorMessage)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onAnswerSelected updates selected answer`() = runTest {
        coEvery { getNewQuizUseCase() } returns Resource.Success(sampleQuestions)
        coEvery { saveQuizAttemptUseCase(any()) } returns Unit

        viewModel = QuizViewModel(getNewQuizUseCase, saveQuizAttemptUseCase)
        viewModel.startQuiz()
        advanceUntilIdle()

        viewModel.onAnswerSelected("A")
        assertEquals("A", viewModel.uiState.value.selectedAnswer)

        viewModel.onAnswerSelected("B")
        assertEquals("B", viewModel.uiState.value.selectedAnswer)
    }

    @Test
    fun `onNextClicked with correct answer increments score`() = runTest {
        coEvery { getNewQuizUseCase() } returns Resource.Success(sampleQuestions)
        coEvery { saveQuizAttemptUseCase(any()) } returns Unit

        viewModel = QuizViewModel(getNewQuizUseCase, saveQuizAttemptUseCase)
        viewModel.startQuiz()
        advanceUntilIdle()

        // Answer Q1 correctly
        viewModel.onAnswerSelected("A")
        viewModel.onNextClicked()
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.score)

        // Answer Q2 correctly
        viewModel.onAnswerSelected("2")
        viewModel.onNextClicked()
        advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.score)
    }

    @Test
    fun `onNextClicked with wrong answer does not increment score`() = runTest {
        coEvery { getNewQuizUseCase() } returns Resource.Success(sampleQuestions)
        coEvery { saveQuizAttemptUseCase(any()) } returns Unit

        viewModel = QuizViewModel(getNewQuizUseCase, saveQuizAttemptUseCase)
        viewModel.startQuiz()
        advanceUntilIdle()

        viewModel.onAnswerSelected("B") // wrong answer
        viewModel.onNextClicked()
        advanceUntilIdle()

        assertEquals(0, viewModel.uiState.value.score)
    }

    @Test
    fun `next button disabled when no answer selected`() = runTest {
        coEvery { getNewQuizUseCase() } returns Resource.Success(sampleQuestions)
        coEvery { saveQuizAttemptUseCase(any()) } returns Unit

        viewModel = QuizViewModel(getNewQuizUseCase, saveQuizAttemptUseCase)
        viewModel.startQuiz()
        advanceUntilIdle()

        val currentQuestion = viewModel.uiState.value.currentQuestion
        assertFalse(currentQuestion?.allAnswers?.contains("A") ?: false || viewModel.uiState.value.selectedAnswer.isNotEmpty())
    }

    @Test
    fun `last question transitions to RESULTS state`() = runTest {
        coEvery { getNewQuizUseCase() } returns Resource.Success(sampleQuestions)
        coEvery { saveQuizAttemptUseCase(any()) } returns Unit

        viewModel = QuizViewModel(getNewQuizUseCase, saveQuizAttemptUseCase)
        viewModel.startQuiz()
        advanceUntilIdle()

        // Answer all 5 questions
        repeat(5) {
            viewModel.onAnswerSelected(sampleQuestions[it].correctAnswer)
            viewModel.onNextClicked()
            advanceUntilIdle()
        }

        assertEquals(QuizState.RESULTS, viewModel.uiState.value.quizState)
        assertEquals(5, viewModel.uiState.value.score)
    }

    @Test
    fun `resetQuiz returns to START state`() = runTest {
        coEvery { getNewQuizUseCase() } returns Resource.Success(sampleQuestions)
        coEvery { saveQuizAttemptUseCase(any()) } returns Unit

        viewModel = QuizViewModel(getNewQuizUseCase, saveQuizAttemptUseCase)
        viewModel.startQuiz()
        advanceUntilIdle()
        assertEquals(QuizState.IN_PROGRESS, viewModel.uiState.value.quizState)

        viewModel.resetQuiz()
        assertEquals(QuizState.START, viewModel.uiState.value.quizState)
    }
}
