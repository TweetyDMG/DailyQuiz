package com.example.dailyquiz.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailyquiz.R
import com.example.dailyquiz.domain.model.QuizAttempt
import com.example.dailyquiz.ui.theme.DailyQuizTheme
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onNavigateToDetails: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var longPressedItemId by remember { mutableStateOf<Int?>(null) }

    DailyQuizTheme {
        if (uiState.showDeleteConfirmDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.onDeleteDialogDismissed() },
                title = { Text(stringResource(R.string.history_delete_title)) },
                text = { Text(stringResource(R.string.history_delete_message)) },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteAttempt()
                        longPressedItemId = null
                    }) {
                        Text(stringResource(R.string.history_delete))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.onDeleteDialogDismissed() }) {
                        Text(stringResource(R.string.history_cancel))
                    }
                }
            )
        }

        if (uiState.showDeletionSuccessDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.onDeletionSuccessDialogDismissed() },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(28.dp),
                title = {
                    Text(
                        text = stringResource(R.string.history_deleted_title),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.history_deleted_message),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                confirmButton = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        TextButton(onClick = { viewModel.onDeletionSuccessDialogDismissed() }) {
                            Text(
                                stringResource(R.string.history_close),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Column {
                Text(
                    text = stringResource(R.string.history_title),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp, bottom = 24.dp),
                    textAlign = TextAlign.Center
                )

                if (uiState.history.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.history_empty),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) // Текст на фоне
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        itemsIndexed(
                            items = uiState.history,
                            key = { _, attempt -> attempt.id }
                        ) { index, attempt ->
                            HistoryItem(
                                attempt = attempt,
                                index = index,
                                isLongPressed = longPressedItemId == attempt.id,
                                onClick = { onNavigateToDetails(attempt.id) },
                                onLongClick = { longPressedItemId = attempt.id },
                                onDeleteClick = {
                                    viewModel.onAttemptLongPressed(attempt.id)
                                },
                                onDismissMenu = { longPressedItemId = null }
                            )
                        }
                    }
                }
            }

            if (longPressedItemId != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { longPressedItemId = null }
                        )
                )
            }
        }
    }
}

@Composable
fun HistoryItem(
    attempt: QuizAttempt,
    index: Int,
    isLongPressed: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDismissMenu: () -> Unit
) {
    val sdfDate = remember { SimpleDateFormat("d MMMM", Locale("ru")) }
    val sdfTime = remember { SimpleDateFormat("HH:mm", Locale("ru")) }
    val date = sdfDate.format(Date(attempt.timestamp))
    val time = sdfTime.format(Date(attempt.timestamp))

    Box {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick
                ),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = stringResource(R.string.history_quiz_n, index + 1),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = date,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(1.5f)
                ) {
                    repeat(5) { starIndex ->
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Star",
                            tint = if (starIndex < attempt.score) Color(0xFFFFC700) else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Text(
                    text = time,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }
        }

        DropdownMenu(
            expanded = isLongPressed,
            onDismissRequest = onDismissMenu,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
        ) {
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.history_delete),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.history_delete), color = MaterialTheme.colorScheme.error)
                    }
                },
                onClick = {
                    onDeleteClick()
                    onDismissMenu()
                }
            )
        }
    }
}
