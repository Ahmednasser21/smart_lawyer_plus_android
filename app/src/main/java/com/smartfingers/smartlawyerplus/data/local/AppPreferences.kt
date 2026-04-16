package com.smartfingers.smartlawyerplus.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "smart_lawyer_prefs")

@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    companion object {
        val KEY_TOKEN = stringPreferencesKey("token")
        val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val KEY_LOGO = stringPreferencesKey("logo")
        val KEY_LOGGED_USER_JSON = stringPreferencesKey("logged_user")
        val KEY_LANGUAGE = stringPreferencesKey("language")           // "ar" | "en"
        val KEY_APP_URL = stringPreferencesKey("entered_url")
        val KEY_APP_CODE = stringPreferencesKey("entered_code")
        val KEY_BASE_URL = stringPreferencesKey("base_url")
        val KEY_IS_ONBOARDING = booleanPreferencesKey("is_onboarding")
        val KEY_EXPIRES_IN = intPreferencesKey("expires_in")
    }


    val token: Flow<String> = context.dataStore.data.map { it[KEY_TOKEN] ?: "" }

    suspend fun setToken(value: String) {
        context.dataStore.edit { it[KEY_TOKEN] = value }
    }

    suspend fun getTokenOnce(): String =
        context.dataStore.data.first()[KEY_TOKEN] ?: ""


    val refreshToken: Flow<String> = context.dataStore.data.map { it[KEY_REFRESH_TOKEN] ?: "" }

    suspend fun setRefreshToken(value: String) {
        context.dataStore.edit { it[KEY_REFRESH_TOKEN] = value }
    }


    val logo: Flow<String> = context.dataStore.data.map { it[KEY_LOGO] ?: "" }

    suspend fun setLogo(value: String) {
        context.dataStore.edit { it[KEY_LOGO] = value }
    }


    val loggedUserJson: Flow<String?> = context.dataStore.data.map { it[KEY_LOGGED_USER_JSON] }

    suspend fun setLoggedUserJson(value: String?) {
        context.dataStore.edit { prefs ->
            if (value != null) prefs[KEY_LOGGED_USER_JSON] = value
            else prefs.remove(KEY_LOGGED_USER_JSON)
        }
    }


    val language: Flow<String> = context.dataStore.data.map { it[KEY_LANGUAGE] ?: "ar" }

    suspend fun setLanguage(value: String) {
        context.dataStore.edit { it[KEY_LANGUAGE] = value }
    }

    suspend fun getLanguageOnce(): String =
        context.dataStore.data.first()[KEY_LANGUAGE] ?: "ar"


    val appUrl: Flow<String> = context.dataStore.data.map { it[KEY_APP_URL] ?: "" }

    suspend fun setAppUrl(value: String) {
        context.dataStore.edit { it[KEY_APP_URL] = value }
    }

    suspend fun getAppUrlOnce(): String =
        context.dataStore.data.first()[KEY_APP_URL] ?: ""

    val appCode: Flow<String> = context.dataStore.data.map { it[KEY_APP_CODE] ?: "" }

    suspend fun setAppCode(value: String) {
        context.dataStore.edit { it[KEY_APP_CODE] = value }
    }

    val baseUrl: Flow<String> = context.dataStore.data.map { it[KEY_BASE_URL] ?: "" }

    suspend fun setBaseUrl(value: String) {
        context.dataStore.edit { it[KEY_BASE_URL] = value }
    }

    suspend fun getBaseUrlOnce(): String =
        context.dataStore.data.first()[KEY_BASE_URL] ?: ""


    val isOnboarding: Flow<Boolean> = context.dataStore.data.map { it[KEY_IS_ONBOARDING] ?: true }

    suspend fun setOnboarding(value: Boolean) {
        context.dataStore.edit { it[KEY_IS_ONBOARDING] = value }
    }

    suspend fun getIsOnboardingOnce(): Boolean =
        context.dataStore.data.first()[KEY_IS_ONBOARDING] ?: true


    val expiresIn: Flow<Int> = context.dataStore.data.map { it[KEY_EXPIRES_IN] ?: 0 }

    suspend fun setExpiresIn(value: Int) {
        context.dataStore.edit { it[KEY_EXPIRES_IN] = value }
    }

    suspend fun getExpiresInOnce(): Int =
        context.dataStore.data.first()[KEY_EXPIRES_IN] ?: 0


    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_TOKEN)
            prefs.remove(KEY_REFRESH_TOKEN)
            prefs.remove(KEY_LOGGED_USER_JSON)
            prefs.remove(KEY_EXPIRES_IN)
        }
    }
}