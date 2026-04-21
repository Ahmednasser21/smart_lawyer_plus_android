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