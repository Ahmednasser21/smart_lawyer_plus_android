package com.smartfingers.smartlawyerplus.data.repository

import com.smartfingers.smartlawyerplus.data.local.AppPreferences
import com.smartfingers.smartlawyerplus.data.remote.api.TasksDetailApiService
import com.smartfingers.smartlawyerplus.data.remote.dto.AddSessionDto
import com.smartfingers.smartlawyerplus.data.remote.dto.HearingDescDto
import com.smartfingers.smartlawyerplus.domain.model.*
import com.smartfingers.smartlawyerplus.domain.repository.SessionsAddRepository
import javax.inject.Inject

class SessionsAddRepositoryImpl @Inject constructor(
    private val prefs: AppPreferences,
    private val api: TasksDetailApiService,
) : SessionsAddRepository {

    private suspend fun base() = "${prefs.getBaseUrlOnce()}${prefs.getAppUrlOnce()}"

    override suspend fun getHearingTypesForAdd(): Result<List<HearingType>> = runCatching {
        val r = api.getHearingTypes("${base()}/api/hearingTypes")
        if (r.isSuccess == true)
            Result.Success(r.data?.items?.map { HearingType(it.id ?: 0, it.name ?: "") } ?: emptyList())
        else Result.Error(r.errorList?.firstOrNull() ?: "Error")
    }.getOrElse { Result.Error(it.message ?: "Network error") }

    override suspend fun getCourtsForAdd(): Result<List<Court>> = runCatching {
        val r = api.getCourts("${base()}/api/courts")
        if (r.isSuccess == true)
            Result.Success(r.data?.items?.map { Court(it.id ?: 0, it.name ?: "") } ?: emptyList())
        else Result.Error(r.errorList?.firstOrNull() ?: "Error")
    }.getOrElse { Result.Error(it.message ?: "Network error") }

    override suspend fun getCasesForAdd(): Result<List<TaskCase>> = runCatching {
        val response = api.getTaskCases("${base()}/api/cases/for-select")
        val list = response.data ?: emptyList()
        Result.Success(list.map { TaskCase(it.id ?: 0, it.name ?: "") })
    }.getOrElse { Result.Error(it.message ?: "Network error") }

    override suspend fun getEmployeesForAdd(): Result<List<TaskEmployee>> = runCatching {
        val response = api.getEmployees("${base()}/api/Employees/List")
        val list = response.data ?: emptyList()
        Result.Success(list.map { TaskEmployee(it.id ?: "", it.name ?: "") })
    }.getOrElse { Result.Error(it.message ?: "Network error") }

    override suspend fun addSession(request: AddSessionRequest): Result<Int> = runCatching {
        val dto = AddSessionDto(
            caseId = request.caseId,
            assignedUserIds = request.assignedUserIds,
            hearingNumber = request.hearingNumber,
            hearingTypeId = request.hearingTypeId,
            subHearingTypeId = request.subHearingTypeId,
            courtId = request.courtId,
            courtCircle = request.courtCircle,
            startDate = request.startDate,
            startTime = request.startTime,
            judgeName = request.judgeName,
            judgeOfficeNumber = request.judgeOfficeNumber,
            hearingDesc = request.hearingDesc,
            requiredDocs = request.requiredDocs,
            hearingDescs = request.hearingDescs.map {
                HearingDescDto(text = it.text, checked = it.checked)
            },
        )
        val r = api.addSession("${base()}/api/hearings", dto)
        if (r.isSuccess == true) Result.Success(r.data ?: 0)
        else Result.Error(r.errorList?.firstOrNull() ?: "Error")
    }.getOrElse { Result.Error(it.message ?: "Network error") }
    override suspend fun getSubHearingTypesForAdd(): Result<List<HearingType>> = runCatching {
        val r = api.getSubHearingTypes("${base()}/api/subHearingTypes")
        if (r.isSuccess == true)
            Result.Success(r.data?.items?.map { HearingType(it.id ?: 0, it.name ?: "") } ?: emptyList())
        else Result.Error(r.errorList?.firstOrNull() ?: "Error")
    }.getOrElse { Result.Error(it.message ?: "Network error") }
}