package com.smartfingers.smartlawyerplus.data.repository

import com.smartfingers.smartlawyerplus.data.local.AppPreferences
import com.smartfingers.smartlawyerplus.data.remote.api.ReportApiService
import com.smartfingers.smartlawyerplus.data.remote.dto.ReportAttachmentDto
import com.smartfingers.smartlawyerplus.data.remote.dto.ReportBodyDto
import com.smartfingers.smartlawyerplus.domain.model.*
import com.smartfingers.smartlawyerplus.domain.repository.ReportRepository
import com.smartfingers.smartlawyerplus.util.AppErrorState
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val prefs: AppPreferences,
    private val api: ReportApiService,
    appErrorState: AppErrorState
) : ReportRepository, BaseRepository(appErrorState) {

    private suspend fun base() = "${prefs.getBaseUrlOnce()}${prefs.getAppUrlOnce()}"

    override suspend fun getHearingTypes(): Result<List<HearingType>> = safeApiCall {
        val r = api.getHearingTypes("${base()}/api/hearingTypes")
        if (r.isSuccess == true)
            Result.Success(r.data?.items?.map { HearingType(it.id ?: 0, it.name ?: "") } ?: emptyList())
        else Result.Error(r.errorList?.firstOrNull() ?: "فشل تحميل أنواع الجلسات")
    }

    override suspend fun getFormTemplates(): Result<List<FormTemplate>> = safeApiCall {
        val r = api.getFormTemplates("${base()}/api/formTemplates")
        if (r.isSuccess == true)
            Result.Success(
                r.data?.items?.map {
                    FormTemplate(id = it.id ?: 0, title = it.title ?: it.name ?: "")
                } ?: emptyList()
            )
        else Result.Error(r.errorList?.firstOrNull() ?: "فشل تحميل القوالب")
    }

    override suspend fun getJudgmentTypes(): Result<List<JudgmentType>> = safeApiCall {
        val list = api.getJudgmentTypes("${base()}/api/enums/judgment-types")
        Result.Success(list.map { JudgmentType(id = it.id ?: 0, name = it.name ?: "") })
    }

    override suspend fun getReport(reportId: Int): Result<ReportDetails> = safeApiCall {
        val r = api.getReport("${base()}/api/hearingReport/$reportId")
        if (r.isSuccess == true && r.data != null) {
            val d = r.data
            Result.Success(
                ReportDetails(
                    id = d.id ?: reportId,
                    hearingId = d.hearingId,
                    hearingTypeId = d.hearingTypeId,
                    status = d.status,
                    statement = d.statement,
                    report = d.report,
                    judgmentReceiptDate = d.judgmentReceiptDate,
                    judgmentText = d.judgmentText,
                    judgmentType = d.judgmentType,
                    formTemplateId = d.formTemplateId,
                    printSettingsTemplateId = d.printSettingsTemplateId,
                    isAddNextCaseEnabled = d.isAddNextCaseEnabled ?: true,
                    isAddNextCaseUrgent = d.isAddNextCaseUrgent ?: false,
                    attachments = d.attachments?.map { att ->
                        ReportAttachment(
                            id = att.id ?: 0,
                            name = att.name,
                            path = att.path,
                            size = att.size,
                            createdOn = att.createdOn,
                            createdBy = att.createdBy,
                            isApproved = att.isApproved ?: false,
                        )
                    } ?: emptyList(),
                )
            )
        } else Result.Error(r.errorList?.firstOrNull() ?: "فشل تحميل التقرير")
    }

    override suspend fun addReport(request: AddReportRequest): Result<Int> = safeApiCall {
        val r = api.addReport("${base()}/api/hearingReport", request.toDto())
        if (r.isSuccess == true) Result.Success(r.data ?: 0)
        else Result.Error(r.errorList?.firstOrNull() ?: "فشل إضافة التقرير")
    }

    override suspend fun updateReport(request: AddReportRequest): Result<Int> = safeApiCall {
        val r = api.updateReport("${base()}/api/hearingReport", request.toDto())
        if (r.isSuccess == true) Result.Success(r.data ?: 0)
        else Result.Error(r.errorList?.firstOrNull() ?: "فشل تعديل التقرير")
    }

    override suspend fun uploadAttachment(
        filePath: String,
        mimeType: String,
    ): Result<ReportAttachment> = safeApiCall {
        val file = File(filePath)
        val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
        val r = api.uploadAttachment("${base()}/api/Attachments", part)
        if (r.isSuccess == true && r.data != null) {
            val d = r.data
            Result.Success(
                ReportAttachment(
                    id = d.id ?: 0,
                    name = d.name,
                    path = d.path,
                    size = d.size,
                    createdOn = d.createdOn,
                    createdBy = d.createdBy,
                    isApproved = d.isApproved ?: false,
                )
            )
        } else Result.Error(r.errorList?.firstOrNull() ?: "فشل رفع الملف")
    }

    private fun AddReportRequest.toDto() = ReportBodyDto(
        id = id,
        hearingId = hearingId.toString(),
        hearingTypeId = hearingTypeId,
        statement = statement,
        report = report,
        judgmentReceiptDate = judgmentReceiptDate,
        judgmentText = judgmentText,
        judgmentType = judgmentType,
        formTemplateId = formTemplateId,
        isAddNextCaseEnabled = isAddNextCaseEnabled,
        isAddNextCaseUrgent = isAddNextCaseUrgent,
        attachments = attachments.map { att ->
            ReportAttachmentDto(
                id = att.id,
                name = att.name,
                path = att.path,
                size = att.size,
                isDraft = false,
                createdOn = att.createdOn,
                createdBy = att.createdBy,
                isApproved = att.isApproved,
                projectName = null,
            )
        },
        status = "",
        prStringSettingsTemplateId = "",
    )
}