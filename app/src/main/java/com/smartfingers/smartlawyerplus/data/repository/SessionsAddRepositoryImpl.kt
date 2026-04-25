package com.smartfingers.smartlawyerplus.data.repository

import com.smartfingers.smartlawyerplus.data.local.AppPreferences
import com.smartfingers.smartlawyerplus.data.remote.api.TasksDetailApiService
import com.smartfingers.smartlawyerplus.data.remote.dto.AddSessionDto
import com.smartfingers.smartlawyerplus.data.remote.dto.HearingDescDto
import com.smartfingers.smartlawyerplus.domain.model.*
import com.smartfingers.smartlawyerplus.domain.repository.SessionsAddRepository
import com.smartfingers.smartlawyerplus.util.AppErrorState
import javax.inject.Inject

class SessionsAddRepositoryImpl @Inject constructor(
    private val prefs: AppPreferences,
    private val api: TasksDetailApiService,
    appErrorState: AppErrorState
) : SessionsAddRepository, BaseRepository(appErrorState) {

    private suspend fun base() = "${prefs.getBaseUrlOnce()}${prefs.getAppUrlOnce()}"

    override suspend fun getHearingTypesForAdd(): Result<List<HearingType>> = safeApiCall {
        val r = api.getHearingTypes("${base()}/api/hearingTypes")
        if (r.isSuccess == true)
            Result.Success(r.data?.items?.map { HearingType(it.id ?: 0, it.name ?: "") } ?: emptyList())
        else Result.Error(r.errorList?.firstOrNull() ?: "Error")
    }

    override suspend fun getCourtsForAdd(): Result<List<Court>> = safeApiCall {
        val r = api.getCourts("${base()}/api/courts")
        if (r.isSuccess == true)
            Result.Success(r.data?.items?.map { Court(it.id ?: 0, it.name ?: "") } ?: emptyList())
        else Result.Error(r.errorList?.firstOrNull() ?: "Error")
    }

    override suspend fun getCasesForAdd(): Result<List<TaskCase>> = safeApiCall {
        val response = api.getTaskCases("${base()}/api/cases/for-select")
        val list = response.data ?: emptyList()
        Result.Success(list.map { TaskCase(it.id ?: 0, it.name ?: "") })
    }

    override suspend fun getEmployeesForAdd(): Result<List<TaskEmployee>> = safeApiCall {
        val response = api.getEmployees("${base()}/api/Employees/List")
        val list = response.data ?: emptyList()
        Result.Success(list.map { TaskEmployee(it.id ?: "", it.name ?: "") })
    }

    override suspend fun addSession(request: AddSessionRequest): Result<Int> = safeApiCall {
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
    }
    override suspend fun getSubHearingTypesForAdd(): Result<List<HearingType>> = safeApiCall {
        val r = api.getSubHearingTypes("${base()}/api/subHearingTypes")
        if (r.isSuccess == true)
            Result.Success(r.data?.items?.map { HearingType(it.id ?: 0, it.name ?: "") } ?: emptyList())
        else Result.Error(r.errorList?.firstOrNull() ?: "Error")
    }
}