package com.smartfingers.smartlawyerplus.data.repository

import com.smartfingers.smartlawyerplus.data.local.AppPreferences
import com.smartfingers.smartlawyerplus.data.remote.api.SessionsApiService
import com.smartfingers.smartlawyerplus.domain.model.FilterOption
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
        Result.Success(
            response.map { HearingStatus(id = it.id ?: 0, name = it.name ?: "") }
        )
    }.getOrElse { Result.Error(it.message ?: "Network error") }

    override suspend fun getHearingPeriods(): Result<List<HearingPeriod>> = runCatching {
        val url = "${baseUrl()}/api/Enums/dashboard-period-types"
        val response = api.getHearingPeriods(url)
        Result.Success(
            response.map { HearingPeriod(id = it.id ?: 0, name = it.name ?: "") }
        )
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

    override suspend fun getCourts(): Result<List<FilterOption>> = runCatching {
        val url = "${baseUrl()}/api/courts"
        val response = api.getCourts(url)
        if (response.isSuccess == true) {
            Result.Success(response.data?.items?.map {
                FilterOption(id = "${it.id ?: 0}", name = it.name ?: "")
            } ?: emptyList())
        } else Result.Error(response.errorList?.firstOrNull() ?: "Failed")
    }.getOrElse { Result.Error(it.message ?: "Network error") }

    override suspend fun getCases(): Result<List<FilterOption>> = runCatching {
        val url = "${baseUrl()}/api/cases/for-select"
        val response = api.getCases(url)
        if (response.isSuccess == true) {
            val items = response.data?.map {
                FilterOption(id = "${it.id ?: 0}", name = it.name ?: "")
            } ?: emptyList()
            Result.Success(items)
        } else Result.Error(response.errorList?.firstOrNull() ?: "Failed")
    }.getOrElse { Result.Error(it.message ?: "Network error") }

    override suspend fun getHearingTypes(): Result<List<FilterOption>> = runCatching {
        val url = "${baseUrl()}/api/hearingTypes"
        val response = api.getHearingTypes(url)
        if (response.isSuccess == true) {
            Result.Success(response.data?.items?.map {
                FilterOption(id = "${it.id ?: 0}", name = it.name ?: "")
            } ?: emptyList())
        } else Result.Error(response.errorList?.firstOrNull() ?: "Failed")
    }.getOrElse { Result.Error(it.message ?: "Network error") }

    override suspend fun getSubHearingTypes(): Result<List<FilterOption>> = runCatching {
        val url = "${baseUrl()}/api/subHearingTypes"
        val response = api.getSubHearingTypes(url)
        if (response.isSuccess == true) {
            Result.Success(response.data?.items?.map {
                FilterOption(id = "${it.id ?: 0}", name = it.name ?: "")
            } ?: emptyList())
        } else Result.Error(response.errorList?.firstOrNull() ?: "Failed")
    }.getOrElse { Result.Error(it.message ?: "Network error") }

    override suspend fun getEmployees(): Result<List<FilterOption>> = runCatching {
        val url = "${baseUrl()}/api/Employees/List"
        val response = api.getEmployees(url)
        if (response.isSuccess == true) {
            Result.Success(response.data?.map {
                FilterOption(id = it.id ?: "", name = it.name ?: "")
            } ?: emptyList())
        } else Result.Error(response.errorList?.firstOrNull() ?: "Failed")
    }.getOrElse { Result.Error(it.message ?: "Network error") }

    override suspend fun getBranches(): Result<List<FilterOption>> = runCatching {
        val url = "${baseUrl()}/api/branches"
        val response = api.getBranches(url)
        if (response.isSuccess == true) {
            Result.Success(response.data?.items?.map {
                FilterOption(id = "${it.id ?: 0}", name = it.name ?: "")
            } ?: emptyList())
        } else Result.Error(response.errorList?.firstOrNull() ?: "Failed")
    }.getOrElse { Result.Error(it.message ?: "Network error") }

    override suspend fun getParties(): Result<List<FilterOption>> = runCatching {
        val url = "${baseUrl()}/api/parties"
        val response = api.getParties(url)
        if (response.isSuccess == true) {
            Result.Success(response.data?.items?.map {
                FilterOption(id = "${it.id ?: 0}", name = it.name ?: "")
            } ?: emptyList())
        } else Result.Error(response.errorList?.firstOrNull() ?: "Failed")
    }.getOrElse { Result.Error(it.message ?: "Network error") }

    override suspend fun getResultCounts(): Result<List<FilterOption>> = runCatching {
        Result.Success(
            listOf(
                FilterOption("10", "10"),
                FilterOption("25", "25"),
                FilterOption("50", "50"),
                FilterOption("100", "100"),
            )
        )
    }.getOrElse { Result.Error(it.message ?: "Network error") }

    override suspend fun getDiscounts(): Result<List<FilterOption>> = runCatching {
        val url = "${baseUrl()}/api/discounts"
        Result.Success(emptyList<FilterOption>())
    }.getOrElse { Result.Error(it.message ?: "Network error") }
}