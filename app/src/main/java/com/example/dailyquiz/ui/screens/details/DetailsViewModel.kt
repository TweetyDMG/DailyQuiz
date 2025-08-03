package com.example.dailyquiz.ui.screens.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyquiz.domain.model.QuizAttempt
import com.example.dailyquiz.domain.use_case.GetAttemptDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailsUiState(
    val isLoading: Boolean = true,
    val attempt: QuizAttempt? = null
)

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getAttemptDetailsUseCase: GetAttemptDetailsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailsUiState())
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

    init {
        savedStateHandle.get<Int>("attemptId")?.let { attemptId ->
            loadDetails(attemptId)
        }
    }

    private fun loadDetails(attemptId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val attempt = getAttemptDetailsUseCase(attemptId)
            _uiState.update { it.copy(isLoading = false, attempt = attempt) }
        }
    }
}