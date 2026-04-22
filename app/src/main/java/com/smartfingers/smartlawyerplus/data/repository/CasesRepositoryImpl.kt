package com.smartfingers.smartlawyerplus.data.repository

import com.smartfingers.smartlawyerplus.data.local.AppPreferences
import com.smartfingers.smartlawyerplus.data.remote.api.CasesApiService
import com.smartfingers.smartlawyerplus.domain.model.CaseAttachment
import com.smartfingers.smartlawyerplus.domain.model.CaseClient
import com.smartfingers.smartlawyerplus.domain.model.CaseDetails
import com.smartfingers.smartlawyerplus.domain.model.CaseListItem
import com.smartfingers.smartlawyerplus.domain.model.CaseStatusFilter
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.repository.CasesRepository
import javax.inject.Inject

class CasesRepositoryImpl @Inject constructor(
    private val prefs: AppPreferences,
    private val api: CasesApiService,
) : CasesRepository {

    private suspend fun baseUrl(): String =
        "${prefs.getBaseUrlOnce()}${prefs.getAppUrlOnce()}"

    // Mirrors iOS setCasesFilter() — status ids match backend enum
    override fun getCaseStatusFilters(): List<CaseStatusFilter> = listOf(
        CaseStatusFilter(id = 5, name = "قيد الانتظار"),
        CaseStatusFilter(id = 2, name = "مغلقة"),
        CaseStatusFilter(id = 4, name = "مسودة"),
        CaseStatusFilter(id = 1, name = "منظورة"),
        CaseStatusFilter(id = 6, name = "ما قبل التقاضي", isSelected = true),
    )

    override suspend fun getCases(
        page: Int,
        pageSize: Int,
        caseStatus: Int,
    ): Result<Pair<List<CaseListItem>, Int>> = runCatching {
        val url = "${baseUrl()}/api/cases" +
                "?sortBy=id&isSortAscending=false" +
                "&page=$page&pageSize=$pageSize" +
                "&searchText=&showFilters=false" +
                "&status=$caseStatus&hideRelatedCases=true"
        val response = api.getCases(url)
        if (response.isSuccess == true) {
            val items = response.data?.items?.map { dto ->
                CaseListItem(
                    id = dto.id ?: 0,
                    name = dto.name ?: "",
                    caseNumberInSource = dto.caseNumberInSource,
                    status = dto.status,
                    startDate = dto.startDate,
                    startDateHijri = dto.startDateHijri,
                    litigationTypeName = dto.litigationTypeName,
                    legalStatusName = dto.legalStatusName,
                    court = dto.court,
                    clients = dto.clients,
                    adversaries = dto.adversaries,
                    managers = dto.managers,
                    nextHearingStartDate = dto.nextHearingDate?.startDate,
                    nextHearingStartDateHijri = dto.nextHearingDate?.startDateHijri,
                )
            } ?: emptyList()
            Result.Success(items to (response.data?.totalItems ?: 0))
        } else {
            Result.Error(response.errorList?.firstOrNull() ?: "فشل تحميل القضايا")
        }
    }.getOrElse { Result.Error(it.message ?: "خطأ في الاتصال") }

    override suspend fun getCaseDetails(caseId: Int): Result<CaseDetails> = runCatching {
        val url = "${baseUrl()}/api/Cases/details/$caseId"
        val response = api.getCaseDetails(url)
        if (response.isSuccess == true && response.data != null) {
            val d = response.data
            Result.Success(
                CaseDetails(
                    id = d.id ?: caseId,
                    name = d.name ?: "",
                    caseNumberInSource = d.caseNumberInSource,
                    statusName = d.statusName,
                    litigationTypeName = d.litigationTypeName,
                    legalStatusName = d.legalStatusName,
                    court = d.court,
                    circleName = d.circleName,
                    startDate = d.startDate,
                    startDateHijri = d.startDateHijri,
                    clients = d.clients,
                    adversaries = d.adversaries,
                    managers = d.managers,
                    branch = d.branch,
                    caseSource = d.caseSource,
                    caseCategory = d.caseCategory,
                    caseKind = d.caseKind,
                    caseSubKind = d.caseSubKind,
                    hearingsCount = d.hearingsCount ?: 0,
                    restDaysToCreateNextCase = d.restDaysToCreateNextCase,
                    reformerName = d.reformerName,
                    nextHearingStartDate = d.nextHearingDate?.startDate,
                )
            )
        } else {
            Result.Error(response.errorList?.firstOrNull() ?: "فشل تحميل تفاصيل القضية")
        }
    }.getOrElse { Result.Error(it.message ?: "خطأ في الاتصال") }

    override suspend fun getCaseAttachments(
        caseId: Int,
        page: Int,
        pageSize: Int,
    ): Result<Pair<List<CaseAttachment>, Int>> = runCatching {
        val url = "${baseUrl()}/api/attachments?page=$page&pageSize=$pageSize&caseId=$caseId"
        val response = api.getCaseAttachments(url)
        if (response.isSuccess == true) {
            val items = response.data?.items?.map { dto ->
                CaseAttachment(
                    id = dto.id ?: 0,
                    name = dto.name,
                    path = dto.path,
                    type = dto.type,
                    createdOn = dto.createdOn,
                    createdBy = dto.createdBy,
                    isApproved = dto.isApproved ?: false,
                )
            } ?: emptyList()
            Result.Success(items to (response.data?.totalItems ?: 0))
        } else {
            Result.Error(response.errorList?.firstOrNull() ?: "فشل تحميل المرفقات")
        }
    }.getOrElse { Result.Error(it.message ?: "خطأ في الاتصال") }

    override suspend fun getCaseClients(
        caseId: Int,
        page: Int,
        pageSize: Int,
    ): Result<Pair<List<CaseClient>, Int>> = runCatching {
        val url = "${baseUrl()}/api/parties/forproject-list" +
                "?sortBy=id&isSortAscending=false" +
                "&page=$page&pageSize=$pageSize&projectId=$caseId"
        val response = api.getCaseClients(url)
        if (response.isSuccess == true) {
            val items = response.data?.items?.map { dto ->
                CaseClient(
                    id = dto.id ?: 0,
                    name = dto.name,
                    identityValue = dto.identityValue,
                    mobile = dto.mobile,
                    email = dto.email,
                    legalStatusName = dto.legalStatusName,
                )
            } ?: emptyList()
            Result.Success(items to (response.data?.totalItems ?: 0))
        } else {
            Result.Error(response.errorList?.firstOrNull() ?: "فشل تحميل العملاء")
        }
    }.getOrElse { Result.Error(it.message ?: "خطأ في الاتصال") }
}