package com.smartfingers.smartlawyerplus.data.remote.dto

import com.google.gson.annotations.SerializedName

data class HearingsListResponseDto(
    @SerializedName("totalItems") val totalItems: Int?,
    @SerializedName("items") val items: List<HearingListItemDto>?,
)

data class HearingListItemDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("hearingNumber") val hearingNumber: Int?,
    @SerializedName("startDate") val startDate: String?,
    @SerializedName("startDateHijri") val startDateHijri: String?,
    @SerializedName("startTime") val startTime: String?,
    @SerializedName("endDate") val endDate: String?,
    @SerializedName("status") val status: Int?,
    @SerializedName("hearingTypeName") val hearingTypeName: String?,
    @SerializedName("subHearingTypeName") val subHearingTypeName: String?,
    @SerializedName("courtName") val courtName: String?,
    @SerializedName("assignedUsers") val assignedUsers: List<String>?,
    @SerializedName("hearingReportId") val hearingReportId: Int?,
    @SerializedName("hearingReportStatus") val hearingReportStatus: Int?,
    @SerializedName("case") val caseName: String?,
    @SerializedName("caseNumberInSource") val caseNumberInSource: String?,
    @SerializedName("caseId") val caseId: Int?,
    @SerializedName("remainingDays") val remainingDays: Int?,
)

data class HearingStatusDto(
    @SerializedName("name") val name: String?,
    @SerializedName("id") val id: Int?,
)

data class HearingPeriodDto(
    @SerializedName("name") val name: String?,
    @SerializedName("id") val id: Int?,
)

data class HearingDetailsDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("hearingNumber") val hearingNumber: Int?,
    @SerializedName("courtCircle") val courtCircle: String?,
    @SerializedName("hearingDesc") val hearingDesc: String?,
    @SerializedName("hearingDescs") val hearingDescs: List<String>?,
    @SerializedName("requiredDocs") val requiredDocs: String?,
    @SerializedName("endDate") val endDate: String?,
    @SerializedName("endDateHijri") val endDateHijri: String?,
    @SerializedName("endTime") val endTime: String?,
    @SerializedName("startDate") val startDate: String?,
    @SerializedName("startDateHijri") val startDateHijri: String?,
    @SerializedName("startTime") val startTime: String?,
    @SerializedName("judgeName") val judgeName: String?,
    @SerializedName("judgeOfficeNumber") val judgeOfficeNumber: String?,
    @SerializedName("status") val status: Int?,
    @SerializedName("statusName") val statusName: String?,
    @SerializedName("legalStatusName") val legalStatusName: String?,
    @SerializedName("hearingTypeName") val hearingTypeName: String?,
    @SerializedName("subHearingTypeName") val subHearingTypeName: String?,
    @SerializedName("courtName") val courtName: String?,
    @SerializedName("assignedUsers") val assignedUsers: List<String>?,
    @SerializedName("caseNumberInSource") val caseNumberInSource: String?,
    @SerializedName("assignedUsersIds") val assignedUsersIds: List<String>?,
    @SerializedName("caseManagersIds") val caseManagersIds: List<String>?,
    @SerializedName("hearingReportId") val hearingReportId: Int?,
    @SerializedName("hearingReportStatus") val hearingReportStatus: Int?,
    @SerializedName("case") val caseName: String?,
    @SerializedName("caseId") val caseId: Int?,
    @SerializedName("createdBy") val createdBy: String?,
    @SerializedName("createdOn") val createdOn: String?,
    @SerializedName("createdOnHijri") val createdOnHijri: String?,
    @SerializedName("updatedOn") val updatedOn: String?,
    @SerializedName("updatedOnHijri") val updatedOnHijri: String?,
    @SerializedName("remainingDays") val remainingDays: Int?,
    @SerializedName("attachments") val attachments: List<HearingAttachmentDto>?,
)

data class HearingAttachmentDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("path") val path: String?,
    @SerializedName("size") val size: Int?,
    @SerializedName("createdOn") val createdOn: String?,
    @SerializedName("createdBy") val createdBy: String?,
    @SerializedName("isApproved") val isApproved: Boolean?,
    @SerializedName("projectName") val projectName: String?,
)

// For last hearing by case
data class LastHearingNumberDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("hearingNumber") val hearingNumber: Int?,
    @SerializedName("circleNumber") val circleNumber: String?,
    @SerializedName("circleName") val circleName: String?,
    @SerializedName("courtId") val courtId: Int?,
    @SerializedName("assignedUserIds") val assignedUserIds: List<String>?,
)

data class LastHearingDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("caseId") val caseId: Int?,
    @SerializedName("hearingId") val hearingId: Int?,
    @SerializedName("hearingNumber") val hearingNumber: Int?,
    @SerializedName("status") val status: Int?,
    @SerializedName("hearingTypeId") val hearingTypeId: Int?,
    @SerializedName("subHearingTypeId") val subHearingTypeId: Int?,
    @SerializedName("courtId") val courtId: Int?,
    @SerializedName("courtCircle") val courtCircle: String?,
    @SerializedName("judgeName") val judgeName: String?,
    @SerializedName("judgeOfficeNumber") val judgeOfficeNumber: String?,
    @SerializedName("assignedUserIds") val assignedUserIds: String?,
    @SerializedName("requiredDocs") val requiredDocs: String?,
)

// Hearing action samples
data class HearingActionSamplesResponseDto(
    @SerializedName("totalItems") val totalItems: Int?,
    @SerializedName("items") val items: List<HearingActionSampleItemDto>?,
)

data class HearingActionSampleItemDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("createdOn") val createdOn: String?,
    @SerializedName("createdBy") val createdBy: String?,
    @SerializedName("isDeleted") val isDeleted: Boolean?,
)

data class HearingActionSampleBodyDto(
    @SerializedName("name") val name: String?,
)

// Sub hearing type
data class SubHearingTypesResponseDto(
    @SerializedName("totalItems") val totalItems: Int?,
    @SerializedName("items") val items: List<FilterItemDto>?,
)