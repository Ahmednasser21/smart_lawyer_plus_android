package com.smartfingers.smartlawyerplus.data.repository

import com.smartfingers.smartlawyerplus.data.local.AppPreferences
import com.smartfingers.smartlawyerplus.data.remote.api.SessionsApiService
import com.smartfingers.smartlawyerplus.data.remote.dto.HearingActionSampleBodyDto
import com.smartfingers.smartlawyerplus.domain.model.FilterOption
import com.smartfingers.smartlawyerplus.domain.model.HearingActionSample
import com.smartfingers.smartlawyerplus.domain.model.HearingAttachment
import com.smartfingers.smartlawyerplus.domain.model.HearingDetails
import com.smartfingers.smartlawyerplus.domain.model.HearingFilter
import com.smartfingers.smartlawyerplus.domain.model.HearingPeriod
import com.smartfingers.smartlawyerplus.domain.model.HearingStatus
import com.smartfingers.smartlawyerplus.domain.model.LastHearing
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.model.Session
import com.smartfingers.smartlawyerplus.domain.repository.SessionsRepository
import com.smartfingers.smartlawyerplus.util.AppErrorState
import javax.inject.Inject

class SessionsRepositoryImpl @Inject constructor(
    private val prefs: AppPreferences,
    private val api: SessionsApiService,
    appErrorState: AppErrorState,
) : SessionsRepository, BaseRepository(appErrorState) {

    private suspend fun baseUrl(): String =
        "${prefs.getBaseUrlOnce()}${prefs.getAppUrlOnce()}"

    override suspend fun getSessions(filter: HearingFilter): Result<Pair<List<Session>, Int>> =
        safeApiCall {
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
        }

    override suspend fun getHearingStatuses(): Result<List<HearingStatus>> = safeApiCall {
        val url = "${baseUrl()}/api/Enums/hearing-statuses"
        val response = api.getHearingStatuses(url)
        Result.Success(
            response.map { HearingStatus(id = it.id ?: 0, name = it.name ?: "") }
        )
    }

    override suspend fun getHearingPeriods(): Result<List<HearingPeriod>> = safeApiCall {
        val url = "${baseUrl()}/api/Enums/dashboard-period-types"
        val response = api.getHearingPeriods(url)
        Result.Success(
            response.map { HearingPeriod(id = it.id ?: 0, name = it.name ?: "") }
        )
    }

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

    override suspend fun getCourts(): Result<List<FilterOption>> = safeApiCall{
        val url = "${baseUrl()}/api/courts"
        val response = api.getCourts(url)
        if (response.isSuccess == true) {
            Result.Success(response.data?.items?.map {
                FilterOption(id = "${it.id ?: 0}", name = it.name ?: "")
            } ?: emptyList())
        } else Result.Error(response.errorList?.firstOrNull() ?: "Failed")
    }

    override suspend fun getCases(): Result<List<FilterOption>> = safeApiCall {
        val url = "${baseUrl()}/api/cases/for-select"
        val response = api.getCases(url)
        if (response.isSuccess == true) {
            val items = response.data?.map {
                FilterOption(id = "${it.id ?: 0}", name = it.name ?: "")
            } ?: emptyList()
            Result.Success(items)
        } else Result.Error(response.errorList?.firstOrNull() ?: "Failed")
    }

    override suspend fun getHearingTypes(): Result<List<FilterOption>> = safeApiCall {
        val url = "${baseUrl()}/api/hearingTypes"
        val response = api.getHearingTypes(url)
        if (response.isSuccess == true) {
            Result.Success(response.data?.items?.map {
                FilterOption(id = "${it.id ?: 0}", name = it.name ?: "")
            } ?: emptyList())
        } else Result.Error(response.errorList?.firstOrNull() ?: "Failed")
    }

    override suspend fun getSubHearingTypes(): Result<List<FilterOption>> = safeApiCall{
        val url = "${baseUrl()}/api/subHearingTypes"
        val response = api.getSubHearingTypes(url)
        if (response.isSuccess == true) {
            Result.Success(response.data?.items?.map {
                FilterOption(id = "${it.id ?: 0}", name = it.name ?: "")
            } ?: emptyList())
        } else Result.Error(response.errorList?.firstOrNull() ?: "Failed")
    }

    override suspend fun getEmployees(): Result<List<FilterOption>> = safeApiCall {
        val url = "${baseUrl()}/api/Employees/List"
        val response = api.getEmployees(url)
        if (response.isSuccess == true) {
            Result.Success(response.data?.map {
                FilterOption(id = it.id ?: "", name = it.name ?: "")
            } ?: emptyList())
        } else Result.Error(response.errorList?.firstOrNull() ?: "Failed")
    }

    override suspend fun getBranches(): Result<List<FilterOption>> = safeApiCall {
        val url = "${baseUrl()}/api/branches"
        val response = api.getBranches(url)
        if (response.isSuccess == true) {
            Result.Success(response.data?.items?.map {
                FilterOption(id = "${it.id ?: 0}", name = it.name ?: "")
            } ?: emptyList())
        } else Result.Error(response.errorList?.firstOrNull() ?: "Failed")
    }
    override suspend fun getParties(): Result<List<FilterOption>> = safeApiCall {
        val url = "${baseUrl()}/api/parties"
        val response = api.getParties(url)
        if (response.isSuccess == true) {
            Result.Success(response.data?.items?.map {
                FilterOption(id = "${it.id ?: 0}", name = it.name ?: "")
            } ?: emptyList())
        } else Result.Error(response.errorList?.firstOrNull() ?: "Failed")
    }

    override suspend fun getResultCounts(): Result<List<FilterOption>> = safeApiCall {
        Result.Success(
            listOf(
                FilterOption("10", "10"),
                FilterOption("25", "25"),
                FilterOption("50", "50"),
                FilterOption("100", "100"),
            )
        )
    }

    override suspend fun getDiscounts(): Result<List<FilterOption>> = safeApiCall {
        val url = "${baseUrl()}/api/discounts"
        Result.Success(emptyList<FilterOption>())
    }

    override suspend fun getHearingDetails(hearingId: Int): Result<HearingDetails> = safeApiCall {
        val url = "${baseUrl()}/api/Hearings/details/$hearingId"
        val response = api.getHearingDetails(url)
        if (response.isSuccess == true && response.data != null) {
            val d = response.data
            Result.Success(
                HearingDetails(
                    id = d.id ?: hearingId,
                    hearingNumber = d.hearingNumber,
                    courtCircle = d.courtCircle,
                    hearingDesc = d.hearingDesc,
                    hearingDescs = d.hearingDescs ?: emptyList(),
                    requiredDocs = d.requiredDocs,
                    startDate = d.startDate,
                    startDateHijri = d.startDateHijri,
                    startTime = d.startTime,
                    endDate = d.endDate,
                    endDateHijri = d.endDateHijri,
                    endTime = d.endTime,
                    judgeName = d.judgeName,
                    judgeOfficeNumber = d.judgeOfficeNumber,
                    status = d.status,
                    statusName = d.statusName,
                    hearingTypeName = d.hearingTypeName,
                    subHearingTypeName = d.subHearingTypeName,
                    courtName = d.courtName,
                    assignedUsers = d.assignedUsers ?: emptyList(),
                    caseName = d.caseName,
                    caseId = d.caseId,
                    createdBy = d.createdBy,
                    createdOn = d.createdOn,
                    updatedOn = d.updatedOn,
                    hearingReportId = d.hearingReportId,
                    hearingReportStatus = d.hearingReportStatus,
                    remainingDays = d.remainingDays,
                    attachments = d.attachments?.map { att ->
                        HearingAttachment(
                            id = att.id ?: 0,
                            name = att.name,
                            path = att.path,
                            size = att.size,
                            createdOn = att.createdOn,
                            createdBy = att.createdBy,
                            isApproved = att.isApproved ?: false,
                            projectName = att.projectName,
                        )
                    } ?: emptyList(),
                )
            )
        } else {
            Result.Error(response.errorList?.firstOrNull() ?: "Failed to load hearing details")
        }
    }

    override suspend fun getLastHearingNumberByCaseId(caseId: String): Result<LastHearing> =
        safeApiCall {
            val url = "${baseUrl()}/api/Hearings/lastHearingNumberByCaseId?CaseId=$caseId"
            val response = api.getLastHearingNumberByCaseId(url)
            if (response.isSuccess == true && response.data != null) {
                val d = response.data
                Result.Success(
                    LastHearing(
                        id = d.id,
                        hearingNumber = d.hearingNumber,
                        courtId = d.courtId,
                        hearingTypeId = null,
                        subHearingTypeId = null,
                        courtCircle = d.circleName,
                        judgeName = null,
                        judgeOfficeNumber = null,
                        assignedUserIds = d.assignedUserIds?.joinToString(","),
                    )
                )
            } else {
                Result.Error(response.errorList?.firstOrNull() ?: "Failed")
            }
        }

    override suspend fun getLastHearingById(hearingId: Int): Result<LastHearing> = safeApiCall {
        val url = "${baseUrl()}/api/hearings/$hearingId"
        val response = api.getLastHearingById(url)
        if (response.isSuccess == true && response.data != null) {
            val d = response.data
            Result.Success(
                LastHearing(
                    id = d.id,
                    hearingNumber = d.hearingNumber,
                    courtId = d.courtId,
                    hearingTypeId = d.hearingTypeId,
                    subHearingTypeId = d.subHearingTypeId,
                    courtCircle = d.courtCircle,
                    judgeName = d.judgeName,
                    judgeOfficeNumber = d.judgeOfficeNumber,
                    assignedUserIds = d.assignedUserIds,
                )
            )
        } else {
            Result.Error(response.errorList?.firstOrNull() ?: "Failed")
        }
    }

    override suspend fun getHearingActionSamples(): Result<List<HearingActionSample>> = safeApiCall {
        val url = "${baseUrl()}/api/HearingActionSamples?page=1&pageSize=999999999"
        val response = api.getHearingActionSamples(url)
        if (response.isSuccess == true) {
            Result.Success(
                response.data?.items?.map {
                    HearingActionSample(id = it.id ?: 0, name = it.name ?: "")
                } ?: emptyList()
            )
        } else Result.Error(response.errorList?.firstOrNull() ?: "Failed")
    }

    override suspend fun addHearingActionSample(name: String): Result<Int> = safeApiCall {
        val url = "${baseUrl()}/api/HearingActionSamples"
        val response = api.addHearingActionSample(url, HearingActionSampleBodyDto(name = name))
        if (response.isSuccess == true) Result.Success(response.data ?: 0)
        else Result.Error(response.errorList?.firstOrNull() ?: "Failed")
    }

}