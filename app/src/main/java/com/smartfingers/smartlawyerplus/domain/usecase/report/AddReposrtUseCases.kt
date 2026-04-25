package com.smartfingers.smartlawyerplus.domain.usecase.report

import com.smartfingers.smartlawyerplus.domain.model.*
import com.smartfingers.smartlawyerplus.domain.repository.ReportRepository
import javax.inject.Inject

class GetReportHearingTypesUseCase @Inject constructor(private val repo: ReportRepository) {
    suspend operator fun invoke(): Result<List<HearingType>> = repo.getHearingTypes()
}

class GetFormTemplatesUseCase @Inject constructor(private val repo: ReportRepository) {
    suspend operator fun invoke(): Result<List<FormTemplate>> = repo.getFormTemplates()
}

class GetJudgmentTypesUseCase @Inject constructor(private val repo: ReportRepository) {
    suspend operator fun invoke(): Result<List<JudgmentType>> = repo.getJudgmentTypes()
}

class GetReportUseCase @Inject constructor(private val repo: ReportRepository) {
    suspend operator fun invoke(reportId: Int): Result<ReportDetails> = repo.getReport(reportId)
}

class AddReportUseCase @Inject constructor(private val repo: ReportRepository) {
    suspend operator fun invoke(request: AddReportRequest): Result<Int> = repo.addReport(request)
}

class UpdateReportUseCase @Inject constructor(private val repo: ReportRepository) {
    suspend operator fun invoke(request: AddReportRequest): Result<Int> = repo.updateReport(request)
}

class UploadReportAttachmentUseCase @Inject constructor(private val repo: ReportRepository) {
    suspend operator fun invoke(filePath: String, mimeType: String): Result<ReportAttachment> =
        repo.uploadAttachment(filePath, mimeType)
}