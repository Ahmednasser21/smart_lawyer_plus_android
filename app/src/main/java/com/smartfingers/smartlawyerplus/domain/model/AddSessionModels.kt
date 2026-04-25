package com.smartfingers.smartlawyerplus.domain.model


data class HearingType(val id: Int, val name: String)
data class Court(val id: Int, val name: String)

data class AddSessionRequest(
    val caseId: String?,
    val assignedUserIds: String?,
    val hearingNumber: String?,
    val hearingTypeId: String?,
    val subHearingTypeId: String?,
    val courtId: String?,
    val courtCircle: String?,
    val startDate: String?,
    val startTime: String?,
    val judgeName: String?,
    val judgeOfficeNumber: String?,
    val hearingDesc: String?,
    val requiredDocs: String?,
    val hearingDescs: List<HearingDescRequest> = emptyList(),
)

data class HearingDescRequest(
    val text: String?,
    val checked: Boolean = false,
)