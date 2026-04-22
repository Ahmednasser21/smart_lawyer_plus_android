package com.smartfingers.smartlawyerplus.data.remote.dto

import com.google.gson.annotations.SerializedName

// ── Calendar ──────────────────────────────────────────────────────────────────

data class CalendarEventDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("title") val title: String?,
    @SerializedName("start") val start: String?,
    @SerializedName("end") val end: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("color") val color: String?,
    @SerializedName("type") val type: Int?,
    @SerializedName("extendedProperties") val extendedProperties: CalendarExtendedPropsDto?,
)

data class CalendarExtendedPropsDto(
    @SerializedName("ExecutiveCaseName") val executiveCaseName: String?,
    @SerializedName("CaseName") val caseName: String?,
    @SerializedName("ConsultationName") val consultationName: String?,
    @SerializedName("ProjectGeneralName") val projectGeneralName: String?,
    @SerializedName("StartDate") val startDate: String?,
    @SerializedName("StartTime") val startTime: String?,
    @SerializedName("StartDateHijri") val startDateHijri: String?,
    @SerializedName("AssignedUsers") val assignedUsers: List<String>?,
    @SerializedName("HearingNumber") val hearingNumber: Int?,
    @SerializedName("CourtName") val courtName: String?,
)

data class CalendarItemTypeDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
)

// ── Notifications ─────────────────────────────────────────────────────────────

data class NotificationsResponseDto(
    @SerializedName("totalNotReadItems") val totalNotReadItems: Int?,
    @SerializedName("default") val dataDefault: String?,
    @SerializedName("totalItems") val totalItems: Int?,
    @SerializedName("items") val items: List<NotificationItemDto>?,
)

data class NotificationItemDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("content") val content: String?,
    @SerializedName("itemType") val itemType: Int?,
    @SerializedName("itemTypeName") val itemTypeName: String?,
    @SerializedName("itemId") val itemId: Int?,
    @SerializedName("createdOnHijri") val createdOnHijri: String?,
    @SerializedName("isRead") val isRead: Boolean?,
    @SerializedName("createdOn") val createdOn: String?,
    @SerializedName("isDeleted") val isDeleted: Boolean?,
)
