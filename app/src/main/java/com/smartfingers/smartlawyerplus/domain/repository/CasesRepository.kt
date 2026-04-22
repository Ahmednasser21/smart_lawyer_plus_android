package com.smartfingers.smartlawyerplus.domain.repository

import com.smartfingers.smartlawyerplus.domain.model.CaseAttachment
import com.smartfingers.smartlawyerplus.domain.model.CaseClient
import com.smartfingers.smartlawyerplus.domain.model.CaseDetails
import com.smartfingers.smartlawyerplus.domain.model.CaseListItem
import com.smartfingers.smartlawyerplus.domain.model.CaseStatusFilter
import com.smartfingers.smartlawyerplus.domain.model.Result

interface CasesRepository {

    fun getCaseStatusFilters(): List<CaseStatusFilter>

    suspend fun getCases(
        page: Int,
        pageSize: Int,
        caseStatus: Int,
    ): Result<Pair<List<CaseListItem>, Int>>

    suspend fun getCaseDetails(caseId: Int): Result<CaseDetails>

    suspend fun getCaseAttachments(
        caseId: Int,
        page: Int,
        pageSize: Int,
    ): Result<Pair<List<CaseAttachment>, Int>>

    suspend fun getCaseClients(
        caseId: Int,
        page: Int,
        pageSize: Int,
    ): Result<Pair<List<CaseClient>, Int>>
}