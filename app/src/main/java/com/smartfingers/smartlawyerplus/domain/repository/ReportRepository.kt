package com.smartfingers.smartlawyerplus.domain.repository

import com.smartfingers.smartlawyerplus.domain.model.*

interface ReportRepository {
    suspend fun getHearingTypes(): Result<List<HearingType>>
    suspend fun getFormTemplates(): Result<List<FormTemplate>>
    suspend fun getJudgmentTypes(): Result<List<JudgmentType>>
    suspend fun getReport(reportId: Int): Result<ReportDetails>
    suspend fun addReport(request: AddReportRequest): Result<Int>
    suspend fun updateReport(request: AddReportRequest): Result<Int>
    suspend fun uploadAttachment(filePath: String, mimeType: String): Result<ReportAttachment>
}