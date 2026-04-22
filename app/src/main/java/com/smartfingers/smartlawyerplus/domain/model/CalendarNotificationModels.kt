package com.smartfingers.smartlawyerplus.domain.model

// ── Calendar Event ────────────────────────────────────────────────────────────

data class CalendarEvent(
    val id: Int,
    val title: String?,
    val start: String?,
    val end: String?,
    val url: String?,
    val color: String?,
    val type: Int?,
    val extendedProperties: CalendarEventExtendedProps?,
) {
    fun getEventType(): CalendarEventType = when (type) {
        1 -> CalendarEventType.APPOINTMENT
        2 -> CalendarEventType.HEARING
        3 -> CalendarEventType.TASK
        4 -> CalendarEventType.CASE_APPEAL
        5 -> CalendarEventType.AGENCY
        else -> CalendarEventType.NA
    }
}

data class CalendarEventExtendedProps(
    val executiveCaseName: String?,
    val caseName: String?,
    val consultationName: String?,
    val projectGeneralName: String?,
    val startDate: String?,
    val startTime: String?,
    val startDateHijri: String?,
    val assignedUsers: List<String>?,
    val hearingNumber: Int?,
    val courtName: String?,
)

enum class CalendarEventType {
    APPOINTMENT, HEARING, TASK, CASE_APPEAL, AGENCY, NA
}

data class DayEvent(
    val event: CalendarEvent,
    val eventType: CalendarEventType,
)

// ── Calendar Day ──────────────────────────────────────────────────────────────

data class CalendarDay(
    val date: java.time.LocalDate,
    val number: String,
    val isWithinDisplayedMonth: Boolean,
    val isSelected: Boolean = false,
    val events: List<DayEvent> = emptyList(),
)

// ── Calendar Month ────────────────────────────────────────────────────────────

data class CalendarMonth(
    val days: List<CalendarDay>,
    val monthTitle: String,
    val date: java.time.LocalDate,
    val isLoaded: Boolean = false,
)

// ── Calendar Item Type ────────────────────────────────────────────────────────

data class CalendarItemType(
    val id: Int,
    val name: String,
)

// ── Notification ──────────────────────────────────────────────────────────────

data class AppNotification(
    val id: Int,
    val content: String?,
    val itemType: Int?,
    val itemTypeName: String?,
    val itemId: Int?,
    val createdOnHijri: String?,
    val isRead: Boolean,
    val createdOn: String?,
    val isDeleted: Boolean,
)

data class NotificationsPage(
    val totalNotReadItems: Int,
    val totalItems: Int,
    val items: List<AppNotification>,
)
