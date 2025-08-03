package com.example.dailyquiz.ui.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailyquiz.domain.model.Question
import com.example.dailyquiz.ui.theme.DailyQuizTheme

@Composable
fun DetailsScreen(
    viewModel: DetailsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    DailyQuizTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Назад",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else if (uiState.attempt == null) {
                Text(
                    text = "Не удалось загрузить детали.",
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 72.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val questions = uiState.attempt!!.questions
                    itemsIndexed(questions) { index, question ->
                        StyledQuestionReviewItem(
                            question = question,
                            questionNumber = index + 1,
                            totalQuestions = questions.size
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StyledQuestionReviewItem(question: Question, questionNumber: Int, totalQuestions: Int) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Вопрос $questionNumber из $totalQuestions",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = question.questionText,
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            question.allAnswers.forEach { answer ->
                val isCorrectAnswer = answer == question.correctAnswer
                val isUserWrongAnswer = answer == question.userAnswer && !question.isCorrect

                val borderColor = when {
                    isCorrectAnswer -> Color(0xFF4CAF50)
                    isUserWrongAnswer -> Color(0xFFF44336)
                    else -> Color.Transparent
                }

                val backgroundColor = when {
                    isCorrectAnswer -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                    isUserWrongAnswer -> Color(0xFFF44336).copy(alpha = 0.1f)
                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                }

                val resultIcon: (@Composable () -> Unit)? = when {
                    isCorrectAnswer -> {
                        { Icon(Icons.Default.Check, contentDescription = "Правильный ответ", tint = Color(0xFF4CAF50)) }
                    }
                    isUserWrongAnswer -> {
                        { Icon(Icons.Default.Close, contentDescription = "Неправильный ответ", tint = Color(0xFFF44336)) }
                    }
                    else -> null
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            width = 1.5.dp,
                            color = borderColor,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(backgroundColor)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = answer,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    resultIcon?.invoke()
                }
            }
        }
    }
}