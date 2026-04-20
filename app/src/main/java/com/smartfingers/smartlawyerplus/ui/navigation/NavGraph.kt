package com.smartfingers.smartlawyerplus.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.smartfingers.smartlawyerplus.ui.screens.linkentry.LinkEntryScreen
import com.smartfingers.smartlawyerplus.ui.screens.login.ForgetPasswordScreen
import com.smartfingers.smartlawyerplus.ui.screens.login.LoginScreen
import com.smartfingers.smartlawyerplus.ui.screens.main.MainScreen
import com.smartfingers.smartlawyerplus.ui.screens.onboarding.OnboardingScreen
import com.smartfingers.smartlawyerplus.ui.screens.splash.SplashScreen
import com.smartfingers.smartlawyerplus.ui.screens.tasks.TasksScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Splash.route,
    ) {

        composable(NavRoutes.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(NavRoutes.Onboarding.route) {
                        popUpTo(NavRoutes.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToLinkEntry = {
                    navController.navigate(NavRoutes.LinkEntry.route) {
                        popUpTo(NavRoutes.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(NavRoutes.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToMain = {
                    navController.navigate(NavRoutes.Main.route) {
                        popUpTo(NavRoutes.Splash.route) { inclusive = true }
                    }
                },
            )
        }

        composable(NavRoutes.Onboarding.route) {
            OnboardingScreen(
                onSkip = {
                    navController.navigate(NavRoutes.LinkEntry.route) {
                        popUpTo(NavRoutes.Onboarding.route) { inclusive = true }
                    }
                },
                onFinish = {
                    navController.navigate(NavRoutes.LinkEntry.route) {
                        popUpTo(NavRoutes.Onboarding.route) { inclusive = true }
                    }
                },
            )
        }

        composable(NavRoutes.LinkEntry.route) {
            LinkEntryScreen(
                onNavigateToLogin = {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(NavRoutes.LinkEntry.route) { inclusive = true }
                    }
                },
            )
        }

        composable(NavRoutes.Login.route) {
            LoginScreen(
                onNavigateToMain = {
                    navController.navigate(NavRoutes.Main.route) {
                        popUpTo(NavRoutes.Login.route) { inclusive = true }
                    }
                },
                onNavigateToForgotPassword = {
                    navController.navigate(NavRoutes.ForgetPassword.route)
                },
                onNavigateToRegister = { /* open browser */ },
            )
        }

        composable(NavRoutes.ForgetPassword.route) {
            ForgetPasswordScreen(
                onBack = { navController.popBackStack() },
                onOtpSent = { email ->
                    navController.navigate(NavRoutes.VerifyOtp.createRoute(email))
                },
            )
        }

        composable(NavRoutes.Main.route) {
            MainScreen(
                onNotificationsClick = { /* TODO */ },
                onCalendarClick = { /* TODO */ },
            )
        }
    }
}