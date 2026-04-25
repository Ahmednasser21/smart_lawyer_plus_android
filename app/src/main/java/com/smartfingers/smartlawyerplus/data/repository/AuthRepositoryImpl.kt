package com.smartfingers.smartlawyerplus.data.repository

import com.google.gson.Gson
import com.smartfingers.smartlawyerplus.data.local.AppPreferences
import com.smartfingers.smartlawyerplus.data.remote.api.AuthApiService
import com.smartfingers.smartlawyerplus.data.remote.dto.LoginRequestDto
import com.smartfingers.smartlawyerplus.data.remote.dto.PasswordResetRequestDto
import com.smartfingers.smartlawyerplus.domain.model.AppConfig
import com.smartfingers.smartlawyerplus.domain.model.AuthSession
import com.smartfingers.smartlawyerplus.domain.model.LoggedUser
import com.smartfingers.smartlawyerplus.domain.model.LoginCredentials
import com.smartfingers.smartlawyerplus.domain.model.PasswordResetRequest
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.repository.AuthRepository
import com.smartfingers.smartlawyerplus.util.AppErrorState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val prefs: AppPreferences,
    private val api: AuthApiService,
    appErrorState: AppErrorState,
) : AuthRepository, BaseRepository(appErrorState) {
    
    private fun buildBaseUrl(code: String) = "https://$code-api.smart-lawyer.net/"

    override suspend fun initApp(link: String, code: String): Result<AppConfig> = safeApiCall {
        val baseUrl = buildBaseUrl(code)
        val url = "$baseUrl$link/api/SystemSettings/theme"
        val response = api.initApp(url)
        if (response.isSuccess == true) {
            val logo = response.data?.logo?.let { raw ->
                val parts = raw.split(",")
                if (parts.size == 2) parts[1] else raw
            }
            prefs.setAppUrl(link)
            prefs.setAppCode(code)
            prefs.setBaseUrl(baseUrl)
            logo?.let { prefs.setLogo(it) }
            Result.Success(
                AppConfig(
                    logo = logo,
                    appUrl = link,
                    appCode = code,
                    baseUrl = baseUrl,
                )
            )
        } else {
            Result.Error(response.errorList?.firstOrNull() ?: "Initialization failed")
        }
    }

    override suspend fun login(credentials: LoginCredentials): Result<AuthSession> = safeApiCall {
        val baseUrl = prefs.getBaseUrlOnce()
        val appUrl = prefs.getAppUrlOnce()
        val url = "$baseUrl$appUrl/api/auth/login"
        val response = api.login(url, LoginRequestDto(credentials.userName, credentials.password))
        if (response.isSuccess == true && response.data != null) {
            val data = response.data
            Result.Success(
                AuthSession(
                    authToken = data.authToken ?: "",
                    refreshToken = data.refreshToken ?: "",
                    expiresIn = data.expiresIn ?: 0,
                    picture = data.picture,
                )
            )
        } else {
            Result.Error(response.errorList?.firstOrNull() ?: "Login failed")
        }
    }

    override suspend fun refreshToken(): Result<AuthSession> =
        Result.Error("Not implemented")

    override suspend fun sendOtp(email: String): Result<Boolean> = safeApiCall {
        val baseUrl = prefs.getBaseUrlOnce()
        val appUrl = prefs.getAppUrlOnce()
        val url = "$baseUrl$appUrl/api/auth/send-reset-password-email"
        val response = api.sendOtp(url, PasswordResetRequestDto(email = email, resetCode = null, newPassword = null))
        if (response.isSuccess == true) {
            Result.Success(response.data ?: false)
        } else {
            Result.Error(response.errorList?.firstOrNull() ?: "Failed to send OTP")
        }
    }

    override suspend fun verifyOtp(request: PasswordResetRequest): Result<Boolean> = safeApiCall {
        val baseUrl = prefs.getBaseUrlOnce()
        val appUrl = prefs.getAppUrlOnce()
        val url = "$baseUrl$appUrl/api/auth/send-reset-password-code"
        val response = api.verifyOtp(
            url,
            PasswordResetRequestDto(email = request.email, resetCode = request.resetCode, newPassword = null),
        )
        if (response.isSuccess == true) {
            Result.Success(response.data ?: false)
        } else {
            Result.Error(response.errorList?.firstOrNull() ?: "Invalid OTP")
        }
    }

    override suspend fun changePassword(request: PasswordResetRequest): Result<Boolean> = safeApiCall {
        val baseUrl = prefs.getBaseUrlOnce()
        val appUrl = prefs.getAppUrlOnce()
        val url = "$baseUrl$appUrl/api/auth/reset-password"
        val response = api.changePassword(
            url,
            PasswordResetRequestDto(email = request.email, resetCode = request.resetCode, newPassword = request.newPassword),
        )
        if (response.isSuccess == true) {
            Result.Success(response.data ?: false)
        } else {
            Result.Error(response.errorList?.firstOrNull() ?: "Password change failed")
        }
    }

    override suspend fun getCachedUser(): LoggedUser? {
        val token = prefs.getTokenOnce()
        if (token.isBlank()) return null
        val json = prefs.loggedUserJson.first() ?: return null
        return try { Gson().fromJson(json, LoggedUser::class.java) } catch (_: Exception) { null }
    }

    override suspend fun saveSession(session: AuthSession, rememberMe: Boolean) {
        prefs.setToken(session.authToken)
        prefs.setRefreshToken(session.refreshToken)
        prefs.setExpiresIn((System.currentTimeMillis() / 1000 + 3200).toInt())
        session.picture?.let { raw ->
            val base64 = if (raw.contains(",")) raw.split(",")[1] else raw
            prefs.setUserPicture(base64)
            prefs.setLogo(base64)
        }
        if (rememberMe) {
            val user = decodeJwtUser(session.authToken)
            prefs.setLoggedUserJson(Gson().toJson(user))
        }
    }

    private fun decodeJwtUser(token: String): LoggedUser {
        return try {
            val parts = token.split(".")
            if (parts.size < 2) return LoggedUser(null, null, null, null)
            val payload = parts[1]
            val padded = payload + "=".repeat((4 - payload.length % 4) % 4)
            val decoded = String(android.util.Base64.decode(padded, android.util.Base64.URL_SAFE), Charsets.UTF_8)
            val json = com.google.gson.JsonParser.parseString(decoded).asJsonObject
            LoggedUser(
                id = json.get("id")?.asString,
                givenName = json.get("given_name")?.asString,
                email = json.get("email")?.asString,
                role = null,
            )
        } catch (_: Exception) {
            LoggedUser(null, null, null, null)
        }
    }

    override suspend fun clearSession() {
        prefs.clearSession()
    }

    override suspend fun isOnboardingComplete(): Boolean = !prefs.getIsOnboardingOnce()

    override suspend fun completeOnboarding() { prefs.setOnboarding(false) }

    override suspend fun isAppConfigured(): Boolean = prefs.getAppUrlOnce().isNotBlank()

    override fun getLogo(): Flow<String> = prefs.logo

    override fun getUserPicture(): Flow<String> = prefs.userPicture
    override suspend fun getUserPictureOnce(): String = prefs.getUserPictureOnce()
}