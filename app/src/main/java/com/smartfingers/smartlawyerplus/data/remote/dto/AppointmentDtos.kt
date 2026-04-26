package com.smartfingers.smartlawyerplus.data.remote.dto

import com.google.gson.annotations.SerializedName


data class AppointmentsListResponseDto(
    @SerializedName("totalItems") val totalItems: Int?,
    @SerializedName("items") val items: List<AppointmentItemDto>?,
)

data class AppointmentItemDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("typeName") val typeName: String?,
    @SerializedName("startDate") val startDate: String?,
    @SerializedName("startDateHijri") val startDateHijri: String?,
    @SerializedName("startTime") val startTime: String?,
    @SerializedName("createdByUser") val createdByUser: String?,
    @SerializedName("assignedUsers") val assignedUsers: List<String>?,
    @SerializedName("parties") val parties: List<String>?,
    @SerializedName("subject") val subject: String?,
    @SerializedName("caseId") val caseId: Int?,
    @SerializedName("case") val caseName: String?,
    @SerializedName("remainingTime") val remainingTime: AppointmentRemainingTimeDto?,
    @SerializedName("isFinished") val isFinished: Boolean?,
)

data class AppointmentRemainingTimeDto(
    @SerializedName("days") val days: Int?,
    @SerializedName("hours") val hours: Int?,
    @SerializedName("minutes") val minutes: Int?,
)


data class AppointmentDetailsResponseDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("typeName") val typeName: String?,
    @SerializedName("startDate") val startDate: String?,
    @SerializedName("startDateHijri") val startDateHijri: String?,
    @SerializedName("startTime") val startTime: String?,
    @SerializedName("createdByUser") val createdByUser: String?,
    @SerializedName("assignedUsers") val assignedUsers: List<String>?,
    @SerializedName("parties") val parties: List<String>?,
    @SerializedName("subject") val subject: String?,
    @SerializedName("caseId") val caseId: Int?,
    @SerializedName("case") val caseName: String?,
    @SerializedName("remainingTime") val remainingTime: AppointmentRemainingTimeDto?,
    @SerializedName("isFinished") val isFinished: Boolean?,
    @SerializedName("attachments") val attachments: List<AppointmentAttachmentDto>?,
    @SerializedName("appointmentFormTemplates") val formTemplates: List<AppointmentFormTemplateDto>?,
    @SerializedName("createdOn") val createdOn: String?,
    @SerializedName("updatedOn") val updatedOn: String?,
    @SerializedName("clientRequest") val clientRequest: String?,
    @SerializedName("executiveCase") val executiveCase: String?,
    @SerializedName("consultation") val consultation: String?,
    @SerializedName("projectGeneral") val projectGeneral: String?,
)

data class AppointmentAttachmentDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("path") val path: String?,
    @SerializedName("createdOn") val createdOn: String?,
    @SerializedName("createdBy") val createdBy: String?,
    @SerializedName("isApproved") val isApproved: Boolean?,
    @SerializedName("size") val size: Int?,
)


data class AppointmentTypesResponseDto(
    @SerializedName("totalItems") val totalItems: Int?,
    @SerializedName("items") val items: List<AppointmentTypeItemDto>?,
)

data class AppointmentTypeItemDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
)

data class AppointmentFormTemplateDto(
    @SerializedName("name") val name: String?,
    @SerializedName("body") val body: String?,
    @SerializedName("printSettingsTemplateId") val printSettingsTemplateId: String?,
)