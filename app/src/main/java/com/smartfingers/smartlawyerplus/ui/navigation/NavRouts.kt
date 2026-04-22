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

    data object TaskDetails : NavRoutes("task_details/{taskId}") {
        fun createRoute(taskId: Int) = "task_details/$taskId"
    }
    data object SessionDetails : NavRoutes("session_details/{sessionId}") {
        fun createRoute(sessionId: Int) = "session_details/$sessionId"
    }
    data object AppointmentDetails : NavRoutes("appointment_details/{appointmentId}") {
        fun createRoute(appointmentId: Int) = "appointment_details/$appointmentId"
    }
    data object CaseDetails : NavRoutes("case_details/{caseId}") {
        fun createRoute(caseId: Int) = "case_details/$caseId"
    }
    data object AddTask : NavRoutes("add_task")
    data object AddSession : NavRoutes("add_session")
    data object AddAppointmentNav : NavRoutes("add_appointment")

    data object Calendar : NavRoutes("calendar")
    data object Notifications : NavRoutes("notifications")
}