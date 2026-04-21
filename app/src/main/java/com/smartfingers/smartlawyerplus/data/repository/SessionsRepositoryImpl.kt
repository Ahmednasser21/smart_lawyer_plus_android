package com.smartfingers.smartlawyerplus.data.repository

import com.smartfingers.smartlawyerplus.data.local.AppPreferences
import com.smartfingers.smartlawyerplus.data.remote.api.SessionsApiService
import com.smartfingers.smartlawyerplus.domain.model.HearingFilter
import com.smartfingers.smartlawyerplus.domain.model.HearingPeriod
import com.smartfingers.smartlawyerplus.domain.model.HearingStatus
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.model.Session
import com.smartfingers.smartlawyerplus.domain.repository.SessionsRepository
import javax.inject.Inject

class SessionsRepositoryImpl @Inject constructor(
    private val prefs: AppPreferences,
    private val api: SessionsApiService,
) : SessionsRepository {

    private suspend fun baseUrl(): String =
        "${prefs.getBaseUrlOnce()}${prefs.getAppUrlOnce()}"

    override suspend fun getSessions(filter: HearingFilter): Result<Pair<List<Session>, Int>> =
        runCatching {
            val url = buildUrl(filter)
            val response = api.getHearings(url)
            if (response.isSuccess == true) {
                val sessions = response.data?.items?.map { dto ->
                    Session(
                        id = dto.id ?: 0,
                        hearingNumber = dto.hearingNumber,
                        startDate = dto.startDate,
                        startDateHijri = dto.startDateHijri,
                        startTime = dto.startTime,
                        endDate = dto.endDate,
                        status = dto.status,
                        hearingTypeName = dto.hearingTypeName,
                        subHearingTypeName = dto.subHearingTypeName,
                        courtName = dto.courtName,
                        assignedUsers = dto.assignedUsers ?: emptyList(),
                        caseName = dto.caseName,
                        caseNumberInSource = dto.caseNumberInSource,
                        caseId = dto.caseId,
                        remainingDays = dto.remainingDays,
                        hasReport = dto.hearingReportId != null,
                    )
                } ?: emptyList()
                Result.Success(sessions to (response.data?.totalItems ?: 0))
            } else {
                Result.Error(response.errorList?.firstOrNull() ?: "Failed to load sessions")
            }
        }.getOrElse { Result.Error(it.message ?: "Network error") }

    override suspend fun getHearingStatuses(): Result<List<HearingStatus>> = runCatching {
        val url = "${baseUrl()}/api/Enums/hearing-statuses"
        val response = api.getHearingStatuses(url)
        if (response.isSuccess == true) {
            Result.Success(
                response.data?.map { HearingStatus(id = it.id ?: 0, name = it.name ?: "") }
                    ?: emptyList()
            )
        } else {
            Result.Error(response.errorList?.firstOrNull() ?: "Failed to load statuses")
        }
    }.getOrElse { Result.Error(it.message ?: "Network error") }

    override suspend fun getHearingPeriods(): Result<List<HearingPeriod>> = runCatching {
        val url = "${baseUrl()}/api/Enums/dashboard-period-types"
        val response = api.getHearingPeriods(url)
        if (response.isSuccess == true) {
            Result.Success(
                response.data?.map { HearingPeriod(id = it.id ?: 0, name = it.name ?: "") }
                    ?: emptyList()
            )
        } else {
            Result.Error(response.errorList?.firstOrNull() ?: "Failed to load periods")
        }
    }.getOrElse { Result.Error(it.message ?: "Network error") }

    private suspend fun buildUrl(filter: HearingFilter): String {
        val base = "${baseUrl()}/api/Hearings"
        val params = buildList {
            add("sortBy=startDate")
            add("isSortAscending=true")
            add("Page=${filter.page}")
            add("PageSize=${filter.pageSize}")
            filter.status?.let { add("Status=$it") }
            filter.courtId?.let { add("CourtId=$it") }
            filter.assignedUserId?.let { add("AssignedUserId=$it") }
            filter.caseId?.let { add("CaseId=$it") }
            filter.hearingTypeId?.let { add("HearingTypeId=$it") }
            filter.dashboardPeriodType?.let { add("DashboardPeriodType=$it") }
            filter.judgeName?.let { add("JudgeName=$it") }
        }
        return "$base?${params.joinToString("&")}"
    }
}