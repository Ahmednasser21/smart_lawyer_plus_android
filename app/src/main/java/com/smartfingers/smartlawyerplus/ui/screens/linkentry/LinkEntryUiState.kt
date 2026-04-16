package com.smartfingers.smartlawyerplus.ui.screens.linkentry

data class LinkEntryUiState(
    val link: String = "",
    val code: String = "",
    val linkError: String = "",
    val codeError: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val generalError: String = "",
)