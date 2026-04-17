package com.smartfingers.smartlawyerplus.domain.model

data class HomeStats(
    val tasksCount: Int,
    val sessionsCount: Int,
    val appointmentsCount: Int,
    val casesCount: Int,
    val pendingTasksCount: Int,
    val upcomingSessionsCount: Int,
)

data class RecentTask(
    val id: Int,
    val name: String,
    val statusName: String,
    val priorityName: String?,
    val remainingDays: Int,
    val taskNumber: String?,
)

data class RecentHearing(
    val id: Int,
    val hearingTypeName: String?,
    val courtName: String?,
    val startDate: String?,
    val caseName: String?,
)