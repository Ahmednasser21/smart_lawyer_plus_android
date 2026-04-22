package com.smartfingers.smartlawyerplus.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CasesListResponseDto(
    @SerializedName("totalItems") val totalItems: Int?,
    @SerializedName("items") val items: List<CaseItemListDto>?,
)

data class CaseItemListDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("caseNumberInSource") val caseNumberInSource: String?,
    @SerializedName("status") val status: Int?,
    @SerializedName("startDate") val startDate: String?,
    @SerializedName("startDateHijri") val startDateHijri: String?,
    @SerializedName("litigationTypeName") val litigationTypeName: String?,
    @SerializedName("legalStatusName") val legalStatusName: String?,
    @SerializedName("court") val court: String?,
    @SerializedName("clients") val clients: String?,
    @SerializedName("adversaries") val adversaries: String?,
    @SerializedName("managers") val managers: String?,
    @SerializedName("nextHearingDate") val nextHearingDate: CaseNextHearingDto?,
    @SerializedName("hasPronounceHearing") val hasPronounceHearing: Boolean?,
    @SerializedName("isAppealCaseAdded") val isAppealCaseAdded: Boolean?,
)

data class CaseNextHearingDto(
    @SerializedName("hearingId") val hearingId: Int?,
    @SerializedName("startDate") val startDate: String?,
    @SerializedName("startDateHijri") val startDateHijri: String?,
    @SerializedName("legalStatusName") val legalStatusName: String?,
)



data class CaseDetailsResponseDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("caseNumberInSource") val caseNumberInSource: String?,
    @SerializedName("status") val status: Int?,
    @SerializedName("statusName") val statusName: String?,
    @SerializedName("litigationTypeName") val litigationTypeName: String?,
    @SerializedName("legalStatusName") val legalStatusName: String?,
    @SerializedName("court") val court: String?,
    @SerializedName("circleName") val circleName: String?,
    @SerializedName("startDate") val startDate: String?,
    @SerializedName("startDateHijri") val startDateHijri: String?,
    @SerializedName("clients") val clients: String?,
    @SerializedName("adversaries") val adversaries: String?,
    @SerializedName("managers") val managers: String?,
    @SerializedName("branch") val branch: String?,
    @SerializedName("caseSource") val caseSource: String?,
    @SerializedName("caseCategory") val caseCategory: String?,
    @SerializedName("caseKind") val caseKind: String?,
    @SerializedName("caseSubKind") val caseSubKind: String?,
    @SerializedName("hearingsCount") val hearingsCount: Int?,
    @SerializedName("restDaysToCreateNextCase") val restDaysToCreateNextCase: Double?,
    @SerializedName("reformerName") val reformerName: String?,
    @SerializedName("nextHearingDate") val nextHearingDate: CaseNextHearingDto?,
)


data class CaseAttachmentsResponseDto(
    @SerializedName("totalItems") val totalItems: Int?,
    @SerializedName("items") val items: List<CaseAttachmentItemDto>?,
)

data class CaseAttachmentItemDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("path") val path: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("createdOn") val createdOn: String?,
    @SerializedName("createdBy") val createdBy: String?,
    @SerializedName("isApproved") val isApproved: Boolean?,
    @SerializedName("size") val size: Int?,
)


data class CaseClientsResponseDto(
    @SerializedName("totalItems") val totalItems: Int?,
    @SerializedName("items") val items: List<CaseClientItemDto>?,
)

data class CaseClientItemDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("identityValue") val identityValue: String?,
    @SerializedName("mobile") val mobile: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("legalStatusName") val legalStatusName: String?,
    @SerializedName("legalStatus") val legalStatus: Int?,
)


data class CasesStatusDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
)