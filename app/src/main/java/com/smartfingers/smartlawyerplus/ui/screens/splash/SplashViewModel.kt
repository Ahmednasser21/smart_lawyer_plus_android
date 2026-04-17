package com.smartfingers.smartlawyerplus.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfingers.smartlawyerplus.domain.usecase.auth.GetCachedUserUseCase
import com.smartfingers.smartlawyerplus.domain.usecase.auth.IsAppConfiguredUseCase
import com.smartfingers.smartlawyerplus.domain.usecase.auth.IsOnboardingCompleteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getCachedUserUseCase: GetCachedUserUseCase,
    private val isOnboardingCompleteUseCase: IsOnboardingCompleteUseCase,
    private val isAppConfiguredUseCase: IsAppConfiguredUseCase,
) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination>(SplashDestination.Idle)
    val destination: StateFlow<SplashDestination> = _destination

    init { resolve() }

    private fun resolve() {
        viewModelScope.launch {
            delay(1500)
            if (!isOnboardingCompleteUseCase()) {
                _destination.value = SplashDestination.Onboarding
                return@launch
            }
            if (!isAppConfiguredUseCase()) {
                _destination.value = SplashDestination.LinkEntry
                return@launch
            }
            val user = getCachedUserUseCase()
            _destination.value = if (user == null) SplashDestination.Login else SplashDestination.Main
        }
    }
}