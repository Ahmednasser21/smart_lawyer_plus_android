package com.smartfingers.smartlawyerplus.di

import com.smartfingers.smartlawyerplus.data.local.AppPreferences
import com.smartfingers.smartlawyerplus.data.remote.api.AuthApiService
import com.smartfingers.smartlawyerplus.data.remote.api.SessionsApiService
import com.smartfingers.smartlawyerplus.data.remote.api.TasksApiService
import com.smartfingers.smartlawyerplus.data.remote.interceptor.AuthInterceptor
import com.smartfingers.smartlawyerplus.util.SessionExpiredManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val PLACEHOLDER_BASE_URL = "https://placeholder.smart-lawyer.net/"

    @Provides
    @Singleton
    fun provideOkHttpClient(
        prefs: AppPreferences,
        authInterceptor: AuthInterceptor,
    ): OkHttpClient {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())

        return OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .addInterceptor(authInterceptor)
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(PLACEHOLDER_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun provideTasksApiService(retrofit: Retrofit): TasksApiService =
        retrofit.create(TasksApiService::class.java)

    @Provides
    @Singleton
    fun provideSessionsApiService(retrofit: Retrofit): SessionsApiService =
        retrofit.create(SessionsApiService::class.java)

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        prefs: AppPreferences,
        sessionExpiredManager: SessionExpiredManager,
    ): AuthInterceptor = AuthInterceptor(prefs, sessionExpiredManager)
}