package com.smartfingers.smartlawyerplus.ui.screens.linkentry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LinkEntryViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LinkEntryUiState())
    val uiState: StateFlow<LinkEntryUiState> = _uiState

    fun onLinkChange(value: String) {
        _uiState.update { it.copy(link = value, linkError = "") }
    }

    fun onCodeChange(value: String) {
        _uiState.update { it.copy(code = value, codeError = "") }
    }

    fun submit() {
        val state = _uiState.value
        var linkError = ""
        var codeError = ""

        if (state.link.isBlank()) linkError = "Link is required"
        if (state.code.isBlank()) codeError = "Code is required"

        if (linkError.isNotEmpty() || codeError.isNotEmpty()) {
            _uiState.update { it.copy(linkError = linkError, codeError = codeError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalError = "") }

            when (val result = authRepository.initApp(
                link = state.link.lowercase().trim(),
                code = state.code.lowercase().trim(),
            )) {
                is Result.Success -> _uiState.update {
                    it.copy(isLoading = false, isSuccess = true)
                }
                is Result.Error -> _uiState.update {
                    it.copy(isLoading = false, generalError = result.message)
                }
                is Result.Loading -> Unit
            }
        }
    }
}