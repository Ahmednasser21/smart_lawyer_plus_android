package com.smartfingers.smartlawyerplus.domain.model

data class Session(
    val id: Int,
    val hearingNumber: Int?,
    val startDate: String?,
    val startDateHijri: String?,
    val startTime: String?,
    val endDate: String?,
    val status: Int?,
    val hearingTypeName: String?,
    val subHearingTypeName: String?,
    val courtName: String?,
    val assignedUsers: List<String>,
    val caseName: String?,
    val caseNumberInSource: String?,
    val caseId: Int?,
    val remainingDays: Int?,
    val hasReport: Boolean,
)

data class HearingStatus(
    val id: Int,
    val name: String,
    val isSelected: Boolean = false,
)

data class HearingPeriod(
    val id: Int,
    val name: String,
)

data class HearingFilter(
    val page: Int = 0,
    val pageSize: Int = 10,
    val status: Int? = null,
    val courtId: String? = null,
    val assignedUserId: String? = null,
    val caseId: String? = null,
    val hearingTypeId: String? = null,
    val subHearingTypeId: String? = null,
    val dashboardPeriodType: String? = null,
    val judgeName: String? = null,
    val branchId: String? = null,
    val clientId: String? = null,
)