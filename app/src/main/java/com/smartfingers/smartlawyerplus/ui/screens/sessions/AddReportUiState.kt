package com.smartfingers.smartlawyerplus.ui.screens.sessions

import com.smartfingers.smartlawyerplus.domain.model.*

data class AddReportUiState(
    // Loading states
    val isLoading: Boolean = false,
    val isLoadingReport: Boolean = false,
    val isUploadingFile: Boolean = false,

    // Dropdowns data
    val hearingTypes: List<HearingType> = emptyList(),
    val formTemplates: List<FormTemplate> = emptyList(),
    val judgmentTypes: List<JudgmentType> = emptyList(),

    // Selected values
    val selectedHearingType: HearingType? = null,
    val selectedFormTemplate: FormTemplate? = null,
    val selectedJudgmentType: JudgmentType? = null,

    // Form fields
    val sessionSummary: String = "",       // ملخص إجراءات الجلسة (report field)
    val courtDecision: String = "",        // قرار المحكمة (statement field)
    val judgmentDate: String = "",         // تاريخ استلام الحكم
    val judgmentSummary: String = "",      // ملخص نطق الحكم
    val isAppealable: Boolean = true,      // قابل للاستئناف
    val isUrgentAppeal: Boolean = false,   // استئناف مستعجل

    // Decision section visibility (shown when hearing type = "نطق بالحكم")
    val showDecisionSection: Boolean = false,

    // Attachments
    val attachments: List<ReportAttachment> = emptyList(),

    // State flags
    val isEditMode: Boolean = false,
    val success: Boolean = false,
    val error: String = "",
    val successMessage: String = "",

    // Add new hearing type dialog
    val showAddHearingTypeDialog: Boolean = false,
)