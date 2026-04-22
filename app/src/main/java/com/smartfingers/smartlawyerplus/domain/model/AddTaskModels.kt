package com.smartfingers.smartlawyerplus.domain.model

data class AddTaskRequest(
    val name: String,
    val taskNumber: String?,
    val taskUsers: String?,
    val attachments: List<String> = emptyList(),
    val requestMessage: String?,
    val period: String?,
    val isSecret: Boolean,
    val priority: String?,
    val endDateType: String?,
    val startDate: String?,
    val endDate: String?,
    val taskType: String?,
    val taskStatus: String?,
    val taskCategoryId: String?,
    val mainTaskId: String?,
    val caseId: String?,
    val consultationId: String?,
    val executiveCaseId: String?,
    val projectGeneralId: String?,
)

data class TaskCategory(val id: Int, val name: String)
data class TaskPriority(val id: Int, val name: String)
data class TaskCase(val id: Int, val name: String)
data class TaskConsultation(val id: Int, val name: String)
data class TaskExecutiveCase(val id: Int, val name: String)
data class TaskProjectGeneral(val id: Int, val name: String)
data class TaskEmployee(val id: String, val name: String)
data class NewTaskNumber(val number: Int)