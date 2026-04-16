package com.smartfingers.smartlawyerplus.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfingers.smartlawyerplus.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination>(SplashDestination.Idle)
    val destination: StateFlow<SplashDestination> = _destination

    init {
        resolve()
    }

    private fun resolve() {
        viewModelScope.launch {
            delay(1500)
            val isOnboarding = !authRepository.isOnboardingComplete()
            if (isOnboarding) {
                _destination.value = SplashDestination.Onboarding
                return@launch
            }

            val isConfigured = authRepository.isAppConfigured()
            if (!isConfigured) {
                _destination.value = SplashDestination.LinkEntry
                return@launch
            }

            val user = authRepository.getCachedUser()
            if (user == null) {
                _destination.value = SplashDestination.Login
            } else {
                _destination.value = SplashDestination.Main
            }
        }
    }
}