package com.smartfingers.smartlawyerplus.domain.model

data class LoginCredentials(
    val userName: String,
    val password: String,
)

data class AuthSession(
    val authToken: String,
    val refreshToken: String,
    val expiresIn: Int,
    val picture: String?,
)

data class LoggedUser(
    val id: String?,
    val givenName: String?,
    val email: String?,
    val role: String?,
)

data class AppConfig(
    val logo: String?,
    val appUrl: String,
    val appCode: String,
    val baseUrl: String,
)

data class PasswordResetRequest(
    val email: String,
    val resetCode: String? = null,
    val newPassword: String? = null,
)