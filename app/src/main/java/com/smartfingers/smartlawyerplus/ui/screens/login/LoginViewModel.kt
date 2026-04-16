package com.smartfingers.smartlawyerplus.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfingers.smartlawyerplus.domain.model.LoginCredentials
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onUserNameChange(value: String) {
        _uiState.update { it.copy(userName = value, userNameError = "") }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordError = "") }
    }

    fun onRememberMeToggle() {
        _uiState.update { it.copy(rememberMe = !it.rememberMe) }
    }

    fun login() {
        val state = _uiState.value
        var userNameError = ""
        var passwordError = ""

        if (state.userName.isBlank()) userNameError = "Please enter your username"
        if (state.password.isBlank()) passwordError = "Please enter the password"

        if (userNameError.isNotEmpty() || passwordError.isNotEmpty()) {
            _uiState.update {
                it.copy(userNameError = userNameError, passwordError = passwordError)
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalError = "") }

            when (val result = authRepository.login(
                LoginCredentials(
                    userName = state.userName.trim(),
                    password = state.password,
                ),
            )) {
                is Result.Success -> {
                    authRepository.saveSession(result.data, state.rememberMe)
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is Result.Error -> _uiState.update {
                    it.copy(isLoading = false, generalError = result.message)
                }
                is Result.Loading -> Unit
            }
        }
    }
}
