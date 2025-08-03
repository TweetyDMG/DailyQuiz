package com.example.dailyquiz.ui.screens.quiz

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailyquiz.domain.model.Question
import com.example.dailyquiz.ui.theme.DailyQuizTheme

@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    onNavigateToHistory: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    DailyQuizTheme {
        AnimatedContent(
            targetState = uiState.quizState,
            transitionSpec = {
                fadeIn(animationSpec = androidx.compose.animation.core.tween(300)) togetherWith
                        fadeOut(animationSpec = androidx.compose.animation.core.tween(300))
            }, label = "QuizScreenAnimation"
        ) { targetState ->
            when (targetState) {
                QuizState.START -> StyledStartScreen(
                    onStartClick = { viewModel.startQuiz() },
                    onNavigateToHistory = onNavigateToHistory
                )
                QuizState.LOADING -> StyledLoadingScreen()
                QuizState.IN_PROGRESS -> StyledQuestionScreen(
                    uiState = uiState,
                    onAnswerSelected = { viewModel.onAnswerSelected(it) },
                    onNextClick = { viewModel.onNextClicked() },
                    onBack = { viewModel.resetQuiz() }
                )
                QuizState.RESULTS -> ResultsScreen(
                    uiState = uiState,
                    onRestart = { viewModel.resetQuiz() }
                )
                QuizState.ERROR -> ErrorScreen(
                    message = uiState.errorMessage,
                    onRetry = { viewModel.startQuiz() }
                )
            }
        }
    }
}

@Composable
fun StyledStartScreen(onStartClick: () -> Unit, onNavigateToHistory: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Button(
            onClick = onNavigateToHistory,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 50.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f))
        ) {
            Icon(
                Icons.Filled.History,
                contentDescription = "История",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("История")
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
        ) {
            Text(
                text = "DAILYQUIZ",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 4.sp
            )
            Spacer(modifier = Modifier.height(64.dp))

            Surface(
                shape = RoundedCornerShape(32.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 32.dp, horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Добро пожаловать в DailyQuiz!",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onStartClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = "НАЧАТЬ ВИКТОРИНУ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StyledLoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "DAILYQUIZ",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 4.sp
            )
            Spacer(modifier = Modifier.height(64.dp))
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 3.dp,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
fun StyledQuestionScreen(
    uiState: QuizUiState,
    onAnswerSelected: (String) -> Unit,
    onNextClick: () -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .align(Alignment.TopCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = MaterialTheme.colorScheme.onPrimary)
            }
            Text(
                text = "DAILYQUIZ",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Surface(
                shape = RoundedCornerShape(32.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.90f)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 64.dp)

            ) {
                QuestionCardContent(
                    uiState = uiState,
                    onAnswerSelected = onAnswerSelected,
                    onNextClick = onNextClick
                )
            }
        }

        Text(
            text = "Вернуться к предыдущим вопросам нельзя",
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}

@Composable
fun QuestionCardContent(
    uiState: QuizUiState,
    onAnswerSelected: (String) -> Unit,
    onNextClick: () -> Unit
) {
    val question = uiState.currentQuestion ?: return
    val isNextButtonEnabled = uiState.selectedAnswer.isNotEmpty()
    val isLastQuestion = uiState.currentQuestionIndex == uiState.questions.size - 1
    val buttonText = if (isLastQuestion) "ЗАВЕРШИТЬ" else "ДАЛЕЕ"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Вопрос ${uiState.currentQuestionIndex + 1} из ${uiState.questions.size}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = question.questionText,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 16.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        question.allAnswers.forEach { answer ->
            val isSelected = uiState.selectedAnswer == answer
            val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
            val backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, borderColor, RoundedCornerShape(16.dp))
                    .background(backgroundColor)
                    .clickable { onAnswerSelected(answer) }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = { onAnswerSelected(answer) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary,
                        unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = answer, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNextClick,
            enabled = isNextButtonEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
        ) {
            Text(
                text = buttonText,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
        }
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Ошибка",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = message, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Попробовать снова")
        }
    }
}

@Composable
fun ResultsScreen(uiState: QuizUiState, onRestart: () -> Unit) {
    val resultInfo = getResultInfo(uiState.score)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Результаты",
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 96.dp)
        )

        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .padding(top = 100.dp, bottom = 32.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star",
                            tint = if (index < uiState.score) Color(0xFFFFD700) else MaterialTheme.colorScheme.outline,
                            modifier = Modifier
                                .size(32.dp)
                                .padding(horizontal = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${uiState.score} из 5",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = resultInfo.first,
                    style = MaterialTheme.typography.displaySmall.copy(fontSize = 28.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = resultInfo.second,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onRestart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = "НАЧАТЬ ЗАНОВО",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

fun getResultInfo(score: Int): Pair<String, String> {
    return when (score) {
        5 -> "Идеально!" to "5/5 — вы ответили на всё правильно. Это блестящий результат!"
        4 -> "Почти идеально!" to "4/5 — очень близко к совершенству. Ещё один шаг!"
        3 -> "Хороший результат!" to "3/5 — вы на верном пути. Продолжайте тренироваться!"
        2 -> "Есть над чем поработать" to "2/5 — не расстраивайтесь, попробуйте ещё раз!"
        1 -> "Сложный вопрос?" to "1/5 — иногда просто не ваш день. Следующая попытка будет лучше!"
        else -> "Бывает и так!" to "0/5 — не отчаивайтесь. Начните заново и удивите себя!"
    }
}

@Preview(showBackground = true)
@Composable
fun StyledQuestionScreenPreview() {
    DailyQuizTheme {
        val previewQuestion = Question(
            questionText = "Как переводится слово «apple»?",
            allAnswers = listOf("Груша", "Яблоко", "Апельсин", "Ананас"),
            correctAnswer = "Яблоко",
            userAnswer = "",
            isCorrect = false
        )
        val previewState = QuizUiState(
            quizState = QuizState.IN_PROGRESS,
            questions = listOf(previewQuestion),
            currentQuestionIndex = 0,
            selectedAnswer = "Яблоко"
        )
        StyledQuestionScreen(
            uiState = previewState,
            onAnswerSelected = {},
            onNextClick = {},
            onBack = {}
        )
    }
}
