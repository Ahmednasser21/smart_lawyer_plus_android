package com.smartfingers.smartlawyerplus.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfingers.smartlawyerplus.domain.repository.AuthRepository
import com.smartfingers.smartlawyerplus.domain.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgetPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgetPasswordUiState())
    val uiState: StateFlow<ForgetPasswordUiState> = _uiState

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, emailError = "") }
    }

    fun sendOtp() {
        val email = _uiState.value.email.trim()

        if (email.isBlank()) {
            _uiState.update { it.copy(emailError = "E-Mail required") }
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(emailError = "Invalid email address") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalError = "") }

            when (val result = authRepository.sendOtp(email)) {
                is Result.Success -> {
                    if (result.data) {
                        _uiState.update { it.copy(isLoading = false, otpSentToEmail = email) }
                    } else {
                        _uiState.update {
                            it.copy(isLoading = false, generalError = "Invalid email address")
                        }
                    }
                }
                is Result.Error -> _uiState.update {
                    it.copy(isLoading = false, generalError = result.message)
                }
                is Result.Loading -> Unit
            }
        }
    }
}