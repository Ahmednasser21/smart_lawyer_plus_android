package com.smartfingers.smartlawyerplus.data.repository

import com.smartfingers.smartlawyerplus.domain.model.AppConfig
import com.smartfingers.smartlawyerplus.domain.model.AuthSession
import com.smartfingers.smartlawyerplus.domain.model.LoggedUser
import com.smartfingers.smartlawyerplus.domain.model.LoginCredentials
import com.smartfingers.smartlawyerplus.domain.model.PasswordResetRequest
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
) : AuthRepository {
    override suspend fun initApp(
        link: String,
        code: String
    ): Result<AppConfig> {
        return Result.Error("")
    }

    override suspend fun login(credentials: LoginCredentials): Result<AuthSession> {
        return Result.Error("")
    }

    override suspend fun refreshToken(): Result<AuthSession> {
        return Result.Error("")

    }

    override suspend fun sendOtp(email: String): Result<Boolean> {
        return Result.Error("")

    }

    override suspend fun verifyOtp(request: PasswordResetRequest): Result<Boolean> {
        return Result.Error("")

    }

    override suspend fun changePassword(request: PasswordResetRequest): Result<Boolean> {
        return Result.Error("")

    }

    override suspend fun getCachedUser(): LoggedUser? {

        return null
    }

    override suspend fun saveSession(
        session: AuthSession,
        rememberMe: Boolean
    ) {

    }

    override suspend fun clearSession() {

    }

    override suspend fun isOnboardingComplete(): Boolean {
        return true
    }

    override suspend fun completeOnboarding() {


    }

    override suspend fun isAppConfigured(): Boolean {
        return true
    }
}