package com.smartfingers.smartlawyerplus.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TaskDetailsDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("picture") val picture: String?,
    @SerializedName("taskNumber") val taskNumber: String?,
    @SerializedName("priority") val priority: String?,
    @SerializedName("isSecret") val isSecret: Boolean?,
    @SerializedName("taskType") val taskType: Int?,
    @SerializedName("taskStatus") val taskStatus: Int?,
    @SerializedName("createdBy") val createdBy: TaskCreatedByDto?,
    @SerializedName("taskManagers") val taskManagers: List<String>?,
    @SerializedName("taskUsers") val taskUsers: List<String>?,
    @SerializedName("requestMessage") val requestMessage: String?,
    @SerializedName("startDate") val startDate: String?,
    @SerializedName("endDate") val endDate: String?,
    @SerializedName("startDateHijri") val startDateHijri: String?,
    @SerializedName("endDateHijri") val endDateHijri: String?,
    @SerializedName("remainingTime") val remainingTime: RemainingTimeDto?,
    @SerializedName("attachmentsCount") val attachmentsCount: Int?,
    @SerializedName("extendRequestsCount") val extendRequestsCount: Int?,
    @SerializedName("apologyRequestsCount") val apologyRequestsCount: Int?,
    @SerializedName("replyApproveRequestsCount") val replyApproveRequestsCount: Int?,
    @SerializedName("replyReviewRequestsCount") val replyReviewRequestsCount: Int?,
    @SerializedName("caseId") val caseId: Int?,
    @SerializedName("consultationId") val consultationId: Int?,
    @SerializedName("executiveCaseId") val executiveCaseId: Int?,
    @SerializedName("projectGeneralId") val projectGeneralId: Int?,
)

data class TaskCreatedByDto(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("picture") val picture: String?,
)

data class TaskRepliesDto(
    @SerializedName("appUser") val appUser: TaskUserDto?,
    @SerializedName("taskReplies") val taskReplies: List<TaskReplyDto>?,
    @SerializedName("extendRequests") val extendRequests: List<TaskExtendRequestDto>?,
    @SerializedName("apologyRequests") val apologyRequests: List<TaskExtendRequestDto>?,
)

data class TaskUserDto(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("picture") val picture: String?,
)

data class TaskReplyDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("replyMessage") val replyMessage: String?,
    @SerializedName("elapsedTime") val elapsedTime: String?,
    @SerializedName("replyDateTime") val replyDateTime: String?,
    @SerializedName("replyDateHijri") val replyDateHijri: String?,
    @SerializedName("workingHoursNo") val workingHoursNo: Int?,
    @SerializedName("workingMinutesNo") val workingMinutesNo: Int?,
    @SerializedName("isDone") val isDone: Boolean?,
    @SerializedName("attachmentCount") val attachmentCount: Int?,
    @SerializedName("taskReplyFormTemplatesCount") val taskReplyFormTemplatesCount: Int?,
    @SerializedName("taskReplyReviewRequestsCount") val taskReplyReviewRequestsCount: Int?,
    @SerializedName("taskReplyApproveRequestsCount") val taskReplyApproveRequestsCount: Int?,
    @SerializedName("taskReplyNotes") val taskReplyNotes: List<TaskReplyNoteDto>?,
)

data class TaskReplyNoteDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("message") val message: String?,
    @SerializedName("createdByUser") val createdByUser: TaskCreatedByDto?,
    @SerializedName("createdOn") val createdOn: String?,
    @SerializedName("period") val period: Int?,
    @SerializedName("periodType") val periodType: Int?,
    @SerializedName("attachmentCount") val attachmentCount: Int?,
)

data class TaskExtendRequestDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("note") val note: String?,
    @SerializedName("period") val period: Int?,
    @SerializedName("periodType") val periodType: Int?,
    @SerializedName("periodTypeName") val periodTypeName: String?,
    @SerializedName("createdOn") val createdOn: String?,
    @SerializedName("status") val status: Int?,
    @SerializedName("statusName") val statusName: String?,
)

data class TaskProjectInfoDto(
    @SerializedName("nextHearingDate") val nextHearingDate: NextHearingDto?,
    @SerializedName("caseDocumentCount") val caseDocumentCount: Int?,
    @SerializedName("caseHearingCount") val caseHearingCount: Int?,
    @SerializedName("caseAppointmentCount") val caseAppointmentCount: Int?,
    @SerializedName("caseAttachmentCount") val caseAttachmentCount: Int?,
    @SerializedName("case") val dataCase: String?,
    @SerializedName("executiveCaseClients") val executiveCaseClients: String?,
)

data class NextHearingDto(@SerializedName("startDate") val startDate: String?)

data class AddTaskDto(
    @SerializedName("name") val name: String?,
    @SerializedName("taskNumber") val taskNumber: String?,
    @SerializedName("taskUsers") val taskUsers: String?,
    @SerializedName("requestMessage") val requestMessage: String?,
    @SerializedName("period") val period: String?,
    @SerializedName("isSecret") val isSecret: Boolean?,
    @SerializedName("priority") val priority: String?,
    @SerializedName("endDateType") val endDateType: String?,
    @SerializedName("startDate") val startDate: String?,
    @SerializedName("endDate") val endDate: String?,
    @SerializedName("taskType") val taskType: String?,
    @SerializedName("taskStatus") val taskStatus: String?,
    @SerializedName("taskCategoryId") val taskCategoryId: String?,
    @SerializedName("mainTaskId") val mainTaskId: String?,
    @SerializedName("caseId") val caseId: String?,
    @SerializedName("consultationId") val consultationId: String?,
    @SerializedName("executiveCaseId") val executiveCaseId: String?,
    @SerializedName("projectGeneralId") val projectGeneralId: String?,
)

data class TaskCategoryDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
)

data class TaskCategoriesResponseDto(
    @SerializedName("totalItems") val totalItems: Int?,
    @SerializedName("items") val items: List<TaskCategoryDto>?,
)

data class TaskPriorityDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
)

data class AddSessionDto(
    @SerializedName("caseId") val caseId: String?,
    @SerializedName("assignedUserIds") val assignedUserIds: String?,
    @SerializedName("hearingNumber") val hearingNumber: String?,
    @SerializedName("hearingTypeId") val hearingTypeId: String?,
    @SerializedName("subHearingTypeId") val subHearingTypeId: String?,
    @SerializedName("courtId") val courtId: String?,
    @SerializedName("courtCircle") val courtCircle: String?,
    @SerializedName("startDate") val startDate: String?,
    @SerializedName("startTime") val startTime: String?,
    @SerializedName("judgeName") val judgeName: String?,
    @SerializedName("judgeOfficeNumber") val judgeOfficeNumber: String?,
    @SerializedName("hearingDesc") val hearingDesc: String?,
    @SerializedName("requiredDocs") val requiredDocs: String?,
    @SerializedName("status") val status: Int = 1,
)

data class AddAppointmentDto(
    @SerializedName("typeId") val typeId: String?,
    @SerializedName("assignedUserIds") val assignedUserIds: String?,
    @SerializedName("partiesIds") val partiesIds: String?,
    @SerializedName("startDate") val startDate: String?,
    @SerializedName("startTime") val startTime: String?,
    @SerializedName("subject") val subject: String?,
    @SerializedName("caseId") val caseId: String?,
    @SerializedName("consultationId") val consultationId: String?,
    @SerializedName("executiveCaseId") val executiveCaseId: String?,
    @SerializedName("projectGeneralId") val projectGeneralId: String?,
    @SerializedName("clientRequestId") val clientRequestId: String?,
)