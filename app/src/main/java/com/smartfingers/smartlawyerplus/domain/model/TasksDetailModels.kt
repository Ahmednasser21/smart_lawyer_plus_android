package com.smartfingers.smartlawyerplus.domain.model

data class TaskDetails(
    val id: Int,
    val name: String,
    val picture: String?,
    val taskNumber: String?,
    val priority: String?,
    val isSecret: Boolean,
    val taskType: Int?,
    val taskStatus: Int?,
    val createdBy: TaskCreatedBy?,
    val taskManagers: List<String>,
    val taskUsers: List<String>,
    val requestMessage: String?,
    val startDate: String?,
    val endDate: String?,
    val startDateHijri: String?,
    val endDateHijri: String?,
    val remainingTime: TaskRemainingTime?,
    val attachmentsCount: Int,
    val extendRequestsCount: Int,
    val apologyRequestsCount: Int,
    val replyApproveRequestsCount: Int,
    val replyReviewRequestsCount: Int,
    val caseId: Int?,
    val consultationId: Int?,
    val executiveCaseId: Int?,
    val projectGeneralId: Int?,
)

data class TaskCreatedBy(val id: String?, val name: String?, val picture: String?)

data class TaskRemainingTime(val days: Int, val hours: Int, val minutes: Int)

data class TaskReply(
    val id: Int,
    val replyMessage: String?,
    val elapsedTime: String?,
    val replyDateTime: String?,
    val replyDateHijri: String?,
    val workingHoursNo: Int,
    val workingMinutesNo: Int,
    val isDone: Boolean,
    val attachmentCount: Int,
    val taskReplyFormTemplatesCount: Int,
    val taskReplyReviewRequestsCount: Int,
    val taskReplyApproveRequestsCount: Int,
    val taskReplyNotes: List<TaskReplyNote>,
)

data class TaskReplyNote(
    val id: Int,
    val message: String?,
    val createdByUser: TaskCreatedBy?,
    val createdOn: String?,
    val period: Int?,
    val periodType: Int?,
    val attachmentCount: Int,
)

data class TaskExtendRequest(
    val id: Int,
    val note: String?,
    val period: Int?,
    val periodType: Int?,
    val periodTypeName: String?,
    val createdOn: String?,
    val status: Int?,
    val statusName: String?,
)

data class TaskProjectInfo(
    val nextHearingDate: String?,
    val caseDocumentCount: Int,
    val caseHearingCount: Int,
    val caseAppointmentCount: Int,
    val caseAttachmentCount: Int,
    val dataCase: String?,
    val executiveCaseClients: String?,
)