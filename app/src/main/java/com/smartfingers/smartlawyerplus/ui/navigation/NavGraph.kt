package com.smartfingers.smartlawyerplus.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.smartfingers.smartlawyerplus.domain.model.Session
import com.smartfingers.smartlawyerplus.ui.screens.appointments.AddAppointmentScreen
import com.smartfingers.smartlawyerplus.ui.screens.appointments.AppointmentDetailsScreen
import com.smartfingers.smartlawyerplus.ui.screens.calendar.CalendarScreen
import com.smartfingers.smartlawyerplus.ui.screens.cases.CaseDetailsScreen
import com.smartfingers.smartlawyerplus.ui.screens.linkentry.LinkEntryScreen
import com.smartfingers.smartlawyerplus.ui.screens.login.ForgetPasswordScreen
import com.smartfingers.smartlawyerplus.ui.screens.login.LoginScreen
import com.smartfingers.smartlawyerplus.ui.screens.main.MainScreen
import com.smartfingers.smartlawyerplus.ui.screens.notifications.NotificationsScreen
import com.smartfingers.smartlawyerplus.ui.screens.onboarding.OnboardingScreen
import com.smartfingers.smartlawyerplus.ui.screens.sessions.AddSessionScreen
import com.smartfingers.smartlawyerplus.ui.screens.sessions.SessionDetailsScreen
import com.smartfingers.smartlawyerplus.ui.screens.splash.SplashScreen
import com.smartfingers.smartlawyerplus.ui.screens.tasks.AddTaskScreen
import com.smartfingers.smartlawyerplus.ui.screens.tasks.TaskDetailsScreen
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

        composable(NavRoutes.Calendar.route) {
            CalendarScreen(
                onNotificationsClick = {
                    navController.navigate(NavRoutes.Notifications.route)
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable(NavRoutes.Notifications.route) {
            NotificationsScreen(
                onBack = { navController.popBackStack() },
            )
        }

        composable(NavRoutes.Main.route) {
            MainScreen(
                navController = navController,
                onNotificationsClick = {
                    navController.navigate(NavRoutes.Notifications.route)
                },
                onCalendarClick = {
                    navController.navigate(NavRoutes.Calendar.route)
                },
            )
        }
        composable(NavRoutes.TaskDetails.route) { backStack ->
            val taskId =
                backStack.arguments?.getString("taskId")?.toIntOrNull() ?: return@composable
            TaskDetailsScreen(
                taskId = taskId,
                onBack = { navController.popBackStack() },
                onNavigateToAddTask = { navController.navigate(NavRoutes.AddTask.route) })
        }

        composable(NavRoutes.SessionDetails.route) { backStack ->
            val sessionId =
                backStack.arguments?.getString("sessionId")?.toIntOrNull() ?: return@composable
            val session = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<Session>("session")
            if (session != null) {
                SessionDetailsScreen(session = session, onBack = { navController.popBackStack() })
            } else {
                navController.popBackStack()
            }
        }

        composable(NavRoutes.AppointmentDetails.route) { backStack ->
            val apptId =
                backStack.arguments?.getString("appointmentId")?.toIntOrNull() ?: return@composable
            AppointmentDetailsScreen(
                appointmentId = apptId,
                onBack = { navController.popBackStack() })
        }

        composable(NavRoutes.CaseDetails.route) { backStack ->
            val caseId =
                backStack.arguments?.getString("caseId")?.toIntOrNull() ?: return@composable
            CaseDetailsScreen(caseId = caseId, onBack = { navController.popBackStack() })
        }

        composable(NavRoutes.AddTask.route) {
            AddTaskScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
            )
        }

        composable(NavRoutes.AddSession.route) {
            AddSessionScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
            )
        }

        composable(NavRoutes.AddAppointmentNav.route) {
            AddAppointmentScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
            )
        }
    }
}