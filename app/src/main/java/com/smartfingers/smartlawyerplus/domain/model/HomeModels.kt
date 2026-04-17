package com.smartfingers.smartlawyerplus.domain.model

data class Task(
    val id: Int,
    val name: String,
    val taskNumber: String?,
    val statusName: String?,
    val taskStatus: Int?,
    val priorityName: String?,
    val remainingDays: Int,
    val remainingHours: Int,
    val createdByUser: String?,
    val taskReplyApproveRequestsCount: Int,
    val taskReplyReviewRequestsCount: Int,
    val isSecret: Boolean,
)

data class TaskFilter(
    val id: Int,
    val name: String,
    val type: TaskFilterType,
    val isSelected: Boolean = false,
)

enum class TaskFilterType { FINISHED, UNFINISHED, REQUIRES_APPROVAL }

data class RecentHearing(
    val id: Int,
    val hearingTypeName: String?,
    val courtName: String?,
    val startDate: String?,
    val caseName: String?,
)