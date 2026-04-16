package com.smartfingers.smartlawyerplus.domain.repository

import com.smartfingers.smartlawyerplus.domain.model.AppConfig
import com.smartfingers.smartlawyerplus.domain.model.AuthSession
import com.smartfingers.smartlawyerplus.domain.model.LoggedUser
import com.smartfingers.smartlawyerplus.domain.model.LoginCredentials
import com.smartfingers.smartlawyerplus.domain.model.PasswordResetRequest
import com.smartfingers.smartlawyerplus.domain.model.Result


interface AuthRepository {

    suspend fun initApp(link: String, code: String): Result<AppConfig>

    suspend fun login(credentials: LoginCredentials): Result<AuthSession>

    suspend fun refreshToken(): Result<AuthSession>

    suspend fun sendOtp(email: String): Result<Boolean>

    suspend fun verifyOtp(request: PasswordResetRequest): Result<Boolean>

    suspend fun changePassword(request: PasswordResetRequest): Result<Boolean>

    suspend fun getCachedUser(): LoggedUser?

    suspend fun saveSession(session: AuthSession, rememberMe: Boolean)

    suspend fun clearSession()

    suspend fun isOnboardingComplete(): Boolean

    suspend fun completeOnboarding()

    suspend fun isAppConfigured(): Boolean
}