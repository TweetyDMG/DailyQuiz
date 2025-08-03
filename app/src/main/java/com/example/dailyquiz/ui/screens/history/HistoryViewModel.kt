package com.example.dailyquiz.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyquiz.domain.model.QuizAttempt
import com.example.dailyquiz.domain.use_case.DeleteAttemptUseCase
import com.example.dailyquiz.domain.use_case.GetHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUiState(
    val isLoading: Boolean = true,
    val history: List<QuizAttempt> = emptyList(),
    val showDeleteConfirmDialog: Boolean = false,
    val showDeletionSuccessDialog: Boolean = false,
    val attemptIdToDelete: Int? = null,
    val toastMessage: String? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getHistoryUseCase: GetHistoryUseCase,
    private val deleteAttemptUseCase: DeleteAttemptUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            getHistoryUseCase().collect { attempts ->
                _uiState.update {
                    it.copy(isLoading = false, history = attempts)
                }
            }
        }
    }

    fun onAttemptLongPressed(attemptId: Int) {
        _uiState.update { it.copy(showDeleteConfirmDialog = true, attemptIdToDelete = attemptId) }
    }

    fun onDeleteDialogDismissed() {
        _uiState.update { it.copy(showDeleteConfirmDialog = false, attemptIdToDelete = null) }
    }

    fun deleteAttempt() {
        _uiState.value.attemptIdToDelete?.let { id ->
            viewModelScope.launch {
                deleteAttemptUseCase(id)
                _uiState.update {
                    it.copy(
                        showDeleteConfirmDialog = false,
                        attemptIdToDelete = null,
                        showDeletionSuccessDialog = true,
                        toastMessage = "Попытка удалена"
                    )
                }
            }
        }
    }

    fun onDeletionSuccessDialogDismissed() {
        _uiState.update { it.copy(showDeletionSuccessDialog = false) }
    }

    fun toastMessageShown() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}