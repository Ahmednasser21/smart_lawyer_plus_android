package com.smartfingers.smartlawyerplus.domain.usecase.cases

import com.smartfingers.smartlawyerplus.domain.model.CaseAttachment
import com.smartfingers.smartlawyerplus.domain.model.CaseClient
import com.smartfingers.smartlawyerplus.domain.model.CaseDetails
import com.smartfingers.smartlawyerplus.domain.model.CaseListItem
import com.smartfingers.smartlawyerplus.domain.model.CaseStatusFilter
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.repository.CasesRepository
import javax.inject.Inject

class GetCaseStatusFiltersUseCase @Inject constructor(
    private val repository: CasesRepository,
) {
    operator fun invoke(): List<CaseStatusFilter> = repository.getCaseStatusFilters()
}

class GetCasesUseCase @Inject constructor(
    private val repository: CasesRepository,
) {
    suspend operator fun invoke(
        page: Int = 0,
        pageSize: Int = 10,
        caseStatus: Int = 6,
    ): Result<Pair<List<CaseListItem>, Int>> =
        repository.getCases(page, pageSize, caseStatus)
}

class GetCaseDetailsUseCase @Inject constructor(
    private val repository: CasesRepository,
) {
    suspend operator fun invoke(caseId: Int): Result<CaseDetails> =
        repository.getCaseDetails(caseId)
}

class GetCaseAttachmentsUseCase @Inject constructor(
    private val repository: CasesRepository,
) {
    suspend operator fun invoke(
        caseId: Int,
        page: Int = 0,
        pageSize: Int = 10,
    ): Result<Pair<List<CaseAttachment>, Int>> =
        repository.getCaseAttachments(caseId, page, pageSize)
}

class GetCaseClientsUseCase @Inject constructor(
    private val repository: CasesRepository,
) {
    suspend operator fun invoke(
        caseId: Int,
        page: Int = 0,
        pageSize: Int = 10,
    ): Result<Pair<List<CaseClient>, Int>> =
        repository.getCaseClients(caseId, page, pageSize)
}