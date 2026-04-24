package com.smartfingers.smartlawyerplus.data.repository

import com.smartfingers.smartlawyerplus.data.local.AppPreferences
import com.smartfingers.smartlawyerplus.data.remote.api.TasksDetailApiService
import com.smartfingers.smartlawyerplus.data.remote.dto.AddAppointmentDto
import com.smartfingers.smartlawyerplus.domain.model.*
import com.smartfingers.smartlawyerplus.domain.repository.AppointmentsAddRepository
import javax.inject.Inject

class AppointmentsAddRepositoryImpl @Inject constructor(
    private val prefs: AppPreferences,
    private val api: TasksDetailApiService,
) : AppointmentsAddRepository {

    private suspend fun base() = "${prefs.getBaseUrlOnce()}${prefs.getAppUrlOnce()}"

    override suspend fun getAppointmentTypesForAdd(): Result<List<AppointmentType>> = runCatching {
        val r = api.getAppointmentTypes("${base()}/api/AppointmentTypes")
        if (r.isSuccess == true)
            Result.Success(r.data?.items?.map { AppointmentType(it.id ?: 0, it.name ?: "") } ?: emptyList())
        else Result.Error(r.errorList?.firstOrNull() ?: "Error")
    }.getOrElse { Result.Error(it.message ?: "Network error") }

    override suspend fun getEmployeesForAdd(): Result<List<TaskEmployee>> = runCatching {
        val response = api.getEmployees("${base()}/api/Employees/List")
        val list = response.data ?: emptyList()
        Result.Success(list.map { TaskEmployee(it.id ?: "", it.name ?: "") })
    }.getOrElse { Result.Error(it.message ?: "Network error") }

    override suspend fun getPartiesForAdd(): Result<List<Party>> = runCatching {
        val r = api.getParties("${base()}/api/parties")
        if (r.isSuccess == true)
            Result.Success(r.data?.items?.map { Party(it.id ?: 0, it.name ?: "") } ?: emptyList())
        else Result.Error(r.errorList?.firstOrNull() ?: "Error")
    }.getOrElse { Result.Error(it.message ?: "Network error") }

    override suspend fun getCasesForAdd(): Result<List<TaskCase>> = runCatching {
        val response = api.getTaskCases("${base()}/api/cases/for-select")
        val list = response.data ?: emptyList()
        Result.Success(list.map { TaskCase(it.id ?: 0, it.name ?: "") })
    }.getOrElse { Result.Error(it.message ?: "Network error") }

    override suspend fun addAppointment(request: AddAppointmentRequest): Result<Int> = runCatching {
        val dto = AddAppointmentDto(
            typeId = request.typeId, assignedUserIds = request.assignedUserIds,
            partiesIds = request.partiesIds, startDate = request.startDate,
            startTime = request.startTime, subject = request.subject,
            caseId = request.caseId, consultationId = request.consultationId,
            executiveCaseId = request.executiveCaseId, projectGeneralId = request.projectGeneralId,
            clientRequestId = request.clientRequestId,
        )
        val r = api.addAppointment("${base()}/api/Appointments", dto)
        if (r.isSuccess == true) Result.Success(r.data ?: 0)
        else Result.Error(r.errorList?.firstOrNull() ?: "Error")
    }.getOrElse { Result.Error(it.message ?: "Network error") }
}