package com.smartfingers.smartlawyerplus.ui.screens.login

data class ForgetPasswordUiState(
    val email: String = "",
    val emailError: String = "",
    val isLoading: Boolean = false,
    val otpSentToEmail: String? = null,
    val generalError: String = "",
)