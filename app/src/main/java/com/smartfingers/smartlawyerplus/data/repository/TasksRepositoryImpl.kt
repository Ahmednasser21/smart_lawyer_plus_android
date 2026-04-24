package com.smartfingers.smartlawyerplus.data.repository

import com.smartfingers.smartlawyerplus.data.local.AppPreferences
import com.smartfingers.smartlawyerplus.data.remote.api.TasksDetailApiService
import com.smartfingers.smartlawyerplus.data.remote.dto.*
import com.smartfingers.smartlawyerplus.domain.model.*
import com.smartfingers.smartlawyerplus.domain.repository.TasksRepository
import javax.inject.Inject

class TasksRepositoryImpl @Inject constructor(
    private val prefs: AppPreferences,
    private val api: TasksDetailApiService,
) : TasksRepository {

    private suspend fun base() = "${prefs.getBaseUrlOnce()}${prefs.getAppUrlOnce()}"

    override suspend fun getTaskDetails(taskId: Int): Result<TaskDetails> = runCatching {
        val r = api.getTaskDetails("${base()}/api/tasks/get-details/$taskId")
        if (r.isSuccess == true && r.data != null) {
            val d = r.data
            Result.Success(TaskDetails(
                id = d.id ?: taskId, name = d.name ?: "",
                taskNumber = d.taskNumber, priority = d.priority,
                isSecret = d.isSecret ?: false, taskType = d.taskType,
                taskStatus = d.taskStatus,
                createdBy = d.createdBy?.let { TaskCreatedBy(it.id, it.name, it.picture) },
                taskManagers = d.taskManagers ?: emptyList(),
                taskUsers = d.taskUsers ?: emptyList(),
                requestMessage = d.requestMessage,
                startDate = d.startDate, endDate = d.endDate,
                startDateHijri = d.startDateHijri, endDateHijri = d.endDateHijri,
                remainingTime = d.remainingTime?.let { TaskRemainingTime(it.days ?: 0, it.hours ?: 0, it.minutes ?: 0) },
                attachmentsCount = d.attachmentsCount ?: 0,
                extendRequestsCount = d.extendRequestsCount ?: 0,
                apologyRequestsCount = d.apologyRequestsCount ?: 0,
                replyApproveRequestsCount = d.replyApproveRequestsCount ?: 0,
                replyReviewRequestsCount = d.replyReviewRequestsCount ?: 0,
                caseId = d.caseId, consultationId = d.consultationId,
                executiveCaseId = d.executiveCaseId, projectGeneralId = d.projectGeneralId,
            ))
        } else Result.Error(r.errorList?.firstOrNull() ?: "Error")
    }.getOrElse { Result.Error(it.message ?: "Network error") }

    override suspend fun getTaskProjectInfo(taskId: Int): Result<TaskProjectInfo> = runCatching {
        val r = api.getTaskProjectInfo("${base()}/api/tasks/get-project-details/$taskId")
        if (r.isSuccess == true && r.data != null) {
            val d = r.data
            Result.Success(TaskProjectInfo(
                nextHearingDate = d.nextHearingDate?.startDate,
                caseDocumentCount = d.caseDocumentCount ?: 0,
                caseHearingCount = d.caseHearingCount ?: 0,
                caseAppointmentCount = d.caseAppointmentCount ?: 0,
                caseAttachmentCount = d.caseAttachmentCount ?: 0,
                dataCase = d.dataCase,
                executiveCaseClients = d.executiveCaseClients,
            ))
        } else Result.Error(r.errorList?.firstOrNull() ?: "Error")
    }.getOrElse { Result.Error(it.message ?: "Network error") }

    override suspend fun getTaskReplies(taskId: Int): Result<List<TaskReply>> = runCatching {
        val response = api.getTaskReplies("${base()}/api/taskReply?taskId=$taskId")
        val list = response.data ?: emptyList()
        val replies = list.flatMap { group ->
            group.taskReplies?.map { dto ->
                TaskReply(
                    id = dto.id ?: 0, replyMessage = dto.replyMessage,
                    elapsedTime = dto.elapsedTime, replyDateTime = dto.replyDateTime,
                    replyDateHijri = dto.replyDateHijri,
                    workingHoursNo = dto.workingHoursNo ?: 0,
                    workingMinutesNo = dto.workingMinutesNo ?: 0,
                    isDone = dto.isDone ?: false,
                    attachmentCount = dto.attachmentCount ?: 0,
                    taskReplyFormTemplatesCount = dto.taskReplyFormTemplatesCount ?: 0,
                    taskReplyReviewRequestsCount = dto.taskReplyReviewRequestsCount ?: 0,
                    taskReplyApproveRequestsCount = dto.taskReplyApproveRequestsCount ?: 0,
                    taskReplyNotes = dto.taskReplyNotes?.map { n ->
                        TaskReplyNote(
                            id = n.id ?: 0, message = n.message,
                            createdByUser = n.createdByUser?.let { TaskCreatedBy(it.id, it.name, it.picture) },
                            createdOn = n.createdOn, period = n.period,
                            periodType = n.periodType, attachmentCount = n.attachmentCount ?: 0,
                        )
                    } ?: emptyList(),
                )
            } ?: emptyList()
        }
        Result.Success(replies)
    }.getOrElse { Result.Error(it.message ?: "Network error") }

    override suspend fun getNewTaskNumber(): Result<Int> = runCatching {
        val r = api.getNewTaskNumber("${base()}/api/tasks/getNewTaskNumber")
        if (r.isSuccess == true) Result.Success(r.data ?: 0)
        else Result.Error(r.errorList?.firstOrNull() ?: "Error")
    }.getOrElse { Result.Error(it.message ?: "Network error") }

    override suspend fun getTaskCategories(): Result<List<TaskCategory>> = runCatching {
        val r = api.getTaskCategories("${base()}/api/taskCategories?sortBy=id&isSortAscending=false&page=1&pageSize=999999999")
        if (r.isSuccess == true) {
            Result.Success(r.data?.items?.map { TaskCategory(it.id ?: 0, it.name ?: "") } ?: emptyList())
        } else Result.Error(r.errorList?.firstOrNull() ?: "Error")
    }.getOrElse { Result.Error(it.message ?: "Network error") }

    override suspend fun getTaskPriorities(): Result<List<TaskPriority>> = runCatching {
        val list = api.getTaskPriorities("${base()}/api/enums/task-priorities")
        Result.Success(list.map { TaskPriority(it.id ?: 0, it.name ?: "") })
    }.getOrElse { Result.Error(it.message ?: "Network error") }

    override suspend fun getTaskCases(): Result<List<TaskCase>> = runCatching {
        val response = api.getTaskCases("${base()}/api/cases/for-select")
        val list = response.data ?: emptyList()
        Result.Success(list.map { TaskCase(it.id ?: 0, it.name ?: "") })
    }.getOrElse { Result.Error(it.message ?: "Network error") }

    override suspend fun getTaskConsultations(): Result<List<TaskConsultation>> = runCatching {
        val list = api.getTaskConsultations("${base()}/api/consultations/for-select")
        Result.Success(list.map { TaskConsultation(it.id ?: 0, it.name ?: "") })
    }.getOrElse { Result.Error(it.message ?: "Network error") }

    override suspend fun getTaskExecutiveCases(): Result<List<TaskExecutiveCase>> = runCatching {
        val list = api.getTaskExecutiveCases("${base()}/api/ExecutiveCases/for-select")
        Result.Success(list.map { TaskExecutiveCase(it.id ?: 0, it.name ?: "") })
    }.getOrElse { Result.Error(it.message ?: "Network error") }

    override suspend fun getTaskProjectGenerals(): Result<List<TaskProjectGeneral>> = runCatching {
        val list = api.getTaskProjectGenerals("${base()}/api/ProjectGenerals/for-select")
        Result.Success(list.map { TaskProjectGeneral(it.id ?: 0, it.name ?: "") })
    }.getOrElse { Result.Error(it.message ?: "Network error") }

    override suspend fun getTaskEmployees(): Result<List<TaskEmployee>> = runCatching {
        val response = api.getEmployees("${base()}/api/Employees/List")
        val list = response.data ?: emptyList()
        Result.Success(list.map { TaskEmployee(it.id ?: "", it.name ?: "") })
    }.getOrElse { Result.Error(it.message ?: "Network error") }

    override suspend fun addTask(request: AddTaskRequest): Result<Int> = runCatching {
        val dto = request.toDto()
        val r = api.addTask("${base()}/api/tasks", dto)
        if (r.isSuccess == true) Result.Success(r.data ?: 0)
        else Result.Error(r.errorList?.firstOrNull() ?: "Error")
    }.getOrElse { Result.Error(it.message ?: "Network error") }

    override suspend fun editTask(id: String, request: AddTaskRequest): Result<Int> = runCatching {
        val dto = request.toDto()
        val r = api.editTask("${base()}/api/tasks", dto)
        if (r.isSuccess == true) Result.Success(r.data ?: 0)
        else Result.Error(r.errorList?.firstOrNull() ?: "Error")
    }.getOrElse { Result.Error(it.message ?: "Network error") }

    private fun AddTaskRequest.toDto() = AddTaskDto(
        name = name, taskNumber = taskNumber, taskUsers = taskUsers,
        requestMessage = requestMessage, period = period, isSecret = isSecret,
        priority = priority, endDateType = endDateType, startDate = startDate,
        endDate = endDate, taskType = taskType, taskStatus = taskStatus,
        taskCategoryId = taskCategoryId, mainTaskId = mainTaskId, caseId = caseId,
        consultationId = consultationId, executiveCaseId = executiveCaseId,
        projectGeneralId = projectGeneralId,
    )
}