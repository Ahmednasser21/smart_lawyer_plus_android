package com.smartfingers.smartlawyerplus.domain.usecase.tasks

import com.smartfingers.smartlawyerplus.domain.model.*
import com.smartfingers.smartlawyerplus.domain.repository.SessionsAddRepository
import com.smartfingers.smartlawyerplus.domain.repository.AppointmentsAddRepository
import com.smartfingers.smartlawyerplus.domain.repository.TasksRepository
import javax.inject.Inject

class GetTaskDetailsUseCase @Inject constructor(private val repo: TasksRepository) {
    suspend operator fun invoke(id: Int) = repo.getTaskDetails(id)
}

class GetTaskProjectInfoUseCase @Inject constructor(private val repo: TasksRepository) {
    suspend operator fun invoke(id: Int) = repo.getTaskProjectInfo(id)
}

class GetTaskRepliesUseCase @Inject constructor(private val repo: TasksRepository) {
    suspend operator fun invoke(id: Int) = repo.getTaskReplies(id)
}

class GetNewTaskNumberUseCase @Inject constructor(private val repo: TasksRepository) {
    suspend operator fun invoke() = repo.getNewTaskNumber()
}

class GetTaskCategoriesUseCase @Inject constructor(private val repo: TasksRepository) {
    suspend operator fun invoke() = repo.getTaskCategories()
}

class GetTaskPrioritiesUseCase @Inject constructor(private val repo: TasksRepository) {
    suspend operator fun invoke() = repo.getTaskPriorities()
}

class GetTaskCasesUseCase @Inject constructor(private val repo: TasksRepository) {
    suspend operator fun invoke() = repo.getTaskCases()
}

class GetTaskConsultationsUseCase @Inject constructor(private val repo: TasksRepository) {
    suspend operator fun invoke() = repo.getTaskConsultations()
}

class GetTaskExecutiveCasesUseCase @Inject constructor(private val repo: TasksRepository) {
    suspend operator fun invoke() = repo.getTaskExecutiveCases()
}

class GetTaskProjectGeneralsUseCase @Inject constructor(private val repo: TasksRepository) {
    suspend operator fun invoke() = repo.getTaskProjectGenerals()
}

class GetTaskEmployeesUseCase @Inject constructor(private val repo: TasksRepository) {
    suspend operator fun invoke() = repo.getTaskEmployees()
}

class AddTaskUseCase @Inject constructor(private val repo: TasksRepository) {
    suspend operator fun invoke(request: AddTaskRequest) = repo.addTask(request)
}

class AddSessionUseCase @Inject constructor(private val repo: SessionsAddRepository) {
    suspend operator fun invoke(request: AddSessionRequest) = repo.addSession(request)
}

class GetHearingTypesForAddUseCase @Inject constructor(private val repo: SessionsAddRepository) {
    suspend operator fun invoke() = repo.getHearingTypesForAdd()
}

class GetCourtsForAddUseCase @Inject constructor(private val repo: SessionsAddRepository) {
    suspend operator fun invoke() = repo.getCourtsForAdd()
}

class GetSessionCasesUseCase @Inject constructor(private val repo: SessionsAddRepository) {
    suspend operator fun invoke() = repo.getCasesForAdd()
}

class GetSessionEmployeesUseCase @Inject constructor(private val repo: SessionsAddRepository) {
    suspend operator fun invoke() = repo.getEmployeesForAdd()
}

class AddAppointmentUseCase2 @Inject constructor(private val repo: AppointmentsAddRepository) {
    suspend operator fun invoke(request: AddAppointmentRequest) = repo.addAppointment(request)
}

class GetAppointmentTypesForAddUseCase @Inject constructor(private val repo: AppointmentsAddRepository) {
    suspend operator fun invoke() = repo.getAppointmentTypesForAdd()
}

class GetAppointmentEmployeesUseCase @Inject constructor(private val repo: AppointmentsAddRepository) {
    suspend operator fun invoke() = repo.getEmployeesForAdd()
}

class GetPartiesForAddUseCase @Inject constructor(private val repo: AppointmentsAddRepository) {
    suspend operator fun invoke() = repo.getPartiesForAdd()
}

class GetAppointmentCasesUseCase @Inject constructor(private val repo: AppointmentsAddRepository) {
    suspend operator fun invoke() = repo.getCasesForAdd()
}

class DeleteTaskReplyUseCase @Inject constructor(private val repo: TasksRepository) {
    suspend operator fun invoke(replyId: Int) = repo.deleteTaskReply(replyId)
}

class CloseTaskReplyUseCase @Inject constructor(private val repo: TasksRepository) {
    suspend operator fun invoke(replyId: Int) = repo.closeTaskReply(replyId)
}

class DeleteTaskUseCase @Inject constructor(private val repo: TasksRepository) {
    suspend operator fun invoke(taskId: Int) = repo.deleteTask(taskId)
}

class CloseTaskUseCase @Inject constructor(private val repo: TasksRepository) {
    suspend operator fun invoke(taskId: Int) = repo.updateTaskStatus(taskId, 5)
}
class GetSubHearingTypesForAddUseCase @Inject constructor(private val repo: SessionsAddRepository) {
    suspend operator fun invoke() = repo.getSubHearingTypesForAdd()
}