package com.smartfingers.smartlawyerplus.ui.screens.splash

sealed class SplashDestination {
    data object Onboarding : SplashDestination()
    data object LinkEntry : SplashDestination()
    data object Login : SplashDestination()
    data object Main : SplashDestination()
    data object Idle : SplashDestination()
}