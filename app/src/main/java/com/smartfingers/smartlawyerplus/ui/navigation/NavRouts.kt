package com.smartfingers.smartlawyerplus.ui.navigation

sealed class NavRoutes(val route: String) {

    data object Splash : NavRoutes("splash")
    data object Onboarding : NavRoutes("onboarding")
    data object LinkEntry : NavRoutes("link_entry")
    data object Login : NavRoutes("login")
    data object ForgetPassword : NavRoutes("forget_password")
    data object VerifyOtp : NavRoutes("verify_otp/{email}") {
        fun createRoute(email: String) = "verify_otp/$email"
    }

    data object ChangePassword : NavRoutes("change_password/{email}/{code}") {
        fun createRoute(email: String, code: String) = "change_password/$email/$code"
    }

    data object Main : NavRoutes("main")

    data object Tasks : NavRoutes("tasks")
    data object Sessions : NavRoutes("sessions")
    data object Home : NavRoutes("home")
    data object Appointments : NavRoutes("appointments")
    data object Cases : NavRoutes("cases")

    data object Calendar : NavRoutes("calendar")
    data object Notifications : NavRoutes("notifications")
}