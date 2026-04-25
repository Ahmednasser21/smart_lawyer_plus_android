package com.smartfingers.smartlawyerplus.data.remote.interceptor

import com.smartfingers.smartlawyerplus.data.local.AppPreferences
import com.smartfingers.smartlawyerplus.util.SessionExpiredManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val prefs: AppPreferences,
    private val sessionExpiredManager: SessionExpiredManager,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { prefs.getTokenOnce() }
        val language = runBlocking { prefs.getLanguageOnce() }

        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Accept-Language", language)
            .addHeader("Content-Type", "application/json")
            .build()

        val response = chain.proceed(request)

        if (response.code == 401) {
            runBlocking { prefs.clearSession() }
            sessionExpiredManager.notifySessionExpired()
        }

        return response
    }
}