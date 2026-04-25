package com.smartfingers.smartlawyerplus.domain.model

data class ReportDetails(
    val id: Int,
    val hearingId: Int?,
    val hearingTypeId: Int?,
    val status: Int?,
    val statement: String?,
    val report: String?,
    val judgmentReceiptDate: String?,
    val judgmentText: String?,
    val judgmentType: Int?,
    val formTemplateId: Int?,
    val printSettingsTemplateId: Int?,
    val isAddNextCaseEnabled: Boolean,
    val isAddNextCaseUrgent: Boolean,
    val attachments: List<ReportAttachment>,
)

data class ReportAttachment(
    val id: Int,
    val name: String?,
    val path: String?,
    val size: Int?,
    val createdOn: String?,
    val createdBy: String?,
    val isApproved: Boolean,
)

data class FormTemplate(
    val id: Int,
    val title: String,
)

data class JudgmentType(
    val id: Int,
    val name: String,
)

data class AddReportRequest(
    val id: Int? = null,
    val hearingId: Int,
    val hearingTypeId: String?,
    val statement: String?,
    val report: String?,
    val judgmentReceiptDate: String?,
    val judgmentText: String?,
    val judgmentType: String?,
    val formTemplateId: String?,
    val isAddNextCaseEnabled: Boolean,
    val isAddNextCaseUrgent: Boolean,
    val attachments: List<ReportAttachment>,
)