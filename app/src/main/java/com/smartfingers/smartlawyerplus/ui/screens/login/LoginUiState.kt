package com.smartfingers.smartlawyerplus.ui.screens.login

data class LoginUiState(
    val userName: String = "",
    val password: String = "",
    val rememberMe: Boolean = true,
    val userNameError: String = "",
    val passwordError: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val generalError: String = "",
)