package com.smartfingers.smartlawyerplus.domain.model

// ── Cases filter status ───────────────────────────────────────────────────────

data class CaseStatusFilter(
    val id: Int,
    val name: String,
    val isSelected: Boolean = false,
)

// ── Case list item ────────────────────────────────────────────────────────────

data class CaseListItem(
    val id: Int,
    val name: String,
    val caseNumberInSource: String?,
    val status: Int?,
    val startDate: String?,
    val startDateHijri: String?,
    val litigationTypeName: String?,
    val legalStatusName: String?,
    val court: String?,
    val clients: String?,
    val adversaries: String?,
    val managers: String?,
    val nextHearingStartDate: String?,
    val nextHearingStartDateHijri: String?,
)

// ── Case details ──────────────────────────────────────────────────────────────

data class CaseDetails(
    val id: Int,
    val name: String,
    val caseNumberInSource: String?,
    val statusName: String?,
    val litigationTypeName: String?,
    val legalStatusName: String?,
    val court: String?,
    val circleName: String?,
    val startDate: String?,
    val startDateHijri: String?,
    val clients: String?,
    val adversaries: String?,
    val managers: String?,
    val branch: String?,
    val caseSource: String?,
    val caseCategory: String?,
    val caseKind: String?,
    val caseSubKind: String?,
    val hearingsCount: Int,
    val restDaysToCreateNextCase: Double?,
    val reformerName: String?,
    val nextHearingStartDate: String?,
)

data class CaseDetailItem(val key: String, val value: String)

// ── Case attachment ───────────────────────────────────────────────────────────

data class CaseAttachment(
    val id: Int,
    val name: String?,
    val path: String?,
    val type: String?,
    val createdOn: String?,
    val createdBy: String?,
    val isApproved: Boolean,
)

// ── Case client ───────────────────────────────────────────────────────────────

data class CaseClient(
    val id: Int,
    val name: String?,
    val identityValue: String?,
    val mobile: String?,
    val email: String?,
    val legalStatusName: String?,
)