package com.smartfingers.smartlawyerplus.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
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
) : Parcelable

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
    val clientMobile: String? = null,
    val clientNumber: String? = null,
)

data class FilterOption(
    val id: String,
    val name: String,
)
data class HearingDetails(
    val id: Int,
    val hearingNumber: Int?,
    val courtCircle: String?,
    val hearingDesc: String?,
    val hearingDescs: List<String>,
    val requiredDocs: String?,
    val startDate: String?,
    val startDateHijri: String?,
    val startTime: String?,
    val endDate: String?,
    val endDateHijri: String?,
    val endTime: String?,
    val judgeName: String?,
    val judgeOfficeNumber: String?,
    val status: Int?,
    val statusName: String?,
    val hearingTypeName: String?,
    val subHearingTypeName: String?,
    val courtName: String?,
    val assignedUsers: List<String>,
    val caseName: String?,
    val caseId: Int?,
    val createdBy: String?,
    val createdOn: String?,
    val updatedOn: String?,
    val hearingReportId: Int?,
    val hearingReportStatus: Int?,
    val remainingDays: Int?,
    val attachments: List<HearingAttachment>,
)

data class HearingAttachment(
    val id: Int,
    val name: String?,
    val path: String?,
    val size: Int?,
    val createdOn: String?,
    val createdBy: String?,
    val isApproved: Boolean,
    val projectName: String?,
)

data class HearingActionSample(
    val id: Int,
    val name: String,
)

data class LastHearing(
    val id: Int?,
    val hearingNumber: Int?,
    val courtId: Int?,
    val hearingTypeId: Int?,
    val subHearingTypeId: Int?,
    val courtCircle: String?,
    val judgeName: String?,
    val judgeOfficeNumber: String?,
    val assignedUserIds: String?,
)

// For add session actions required
data class SessionActionRequired(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String = "",
    val isSelected: Boolean = false,
    val isChecked: Boolean = false,
)