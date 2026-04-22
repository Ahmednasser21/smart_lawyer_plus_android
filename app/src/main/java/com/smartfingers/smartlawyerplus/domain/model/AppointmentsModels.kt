package com.smartfingers.smartlawyerplus.domain.model

// ── Appointment list item ─────────────────────────────────────────────────────

data class AppointmentListItem(
    val id: Int,
    val typeName: String?,
    val startDate: String?,
    val startDateHijri: String?,
    val startTime: String?,
    val createdByUser: String?,
    val assignedUsers: List<String>,
    val parties: List<String>,
    val subject: String?,
    val caseName: String?,
    val remainingDays: Int,
    val remainingHours: Int,
    val isFinished: Boolean,
)

// ── Appointment details ───────────────────────────────────────────────────────

data class AppointmentDetails(
    val id: Int,
    val typeName: String?,
    val startDate: String?,
    val startDateHijri: String?,
    val startTime: String?,
    val createdByUser: String?,
    val assignedUsers: List<String>,
    val parties: List<String>,
    val subject: String?,
    val caseName: String?,
    val remainingDays: Int,
    val remainingHours: Int,
    val isFinished: Boolean,
    val attachments: List<AppointmentAttachment>,
    val createdOn: String?,
    val updatedOn: String?,
    val clientRequest: String?,
    val executiveCase: String?,
    val consultation: String?,
    val projectGeneral: String?,
)

data class AppointmentAttachment(
    val id: Int,
    val name: String?,
    val path: String?,
    val createdOn: String?,
    val createdBy: String?,
    val isApproved: Boolean,
)

// ── Appointment type ──────────────────────────────────────────────────────────

data class AppointmentType(
    val id: Int,
    val name: String,
)

// ── Appointment filter ────────────────────────────────────────────────────────

data class AppointmentsFilter(
    val isFinished: Boolean = false,
    val page: Int = 0,
    val pageSize: Int = 10,
)