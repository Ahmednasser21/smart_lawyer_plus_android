package com.smartfingers.smartlawyerplus.ui.screens.sessions

import com.smartfingers.smartlawyerplus.domain.model.Court
import com.smartfingers.smartlawyerplus.domain.model.HearingActionSample
import com.smartfingers.smartlawyerplus.domain.model.HearingType
import com.smartfingers.smartlawyerplus.domain.model.ReportAttachment
import com.smartfingers.smartlawyerplus.domain.model.SessionActionRequired
import com.smartfingers.smartlawyerplus.domain.model.TaskCase
import com.smartfingers.smartlawyerplus.domain.model.TaskEmployee

data class AddSessionUiState(
    val isLoading: Boolean = false,
    val isUploadingAttachment: Boolean = false,
    val selectedCase: TaskCase? = null,
    val selectedHearingType: HearingType? = null,
    val selectedSubHearingType: HearingType? = null,
    val selectedCourt: Court? = null,
    val selectedEmployees: List<TaskEmployee> = emptyList(),
    val hearingNumber: String = "",
    val courtCircle: String = "",
    val judgeName: String = "",
    val judgeOfficeNumber: String = "",
    val startDate: String = "",
    val startDateHijri: String = "",
    val startTime: String = "",
    val hearingDesc: String = "",
    val requiredDocs: String = "",
    val actionsRequired: List<SessionActionRequired> = emptyList(),
    val attachments: List<ReportAttachment> = emptyList(),
    val cases: List<TaskCase> = emptyList(),
    val hearingTypes: List<HearingType> = emptyList(),
    val subHearingTypes: List<HearingType> = emptyList(),
    val courts: List<Court> = emptyList(),
    val employees: List<TaskEmployee> = emptyList(),
    // Action samples dialog
    val actionSamples: List<HearingActionSample> = emptyList(),
    val isLoadingActionSamples: Boolean = false,
    val showActionSamplesDialog: Boolean = false,
    val actionSamplesTargetIndex: Int = -1,
    // Add new action dialog
    val showAddActionDialog: Boolean = false,
    val addActionTargetIndex: Int = -1,
    // Add new hearing type dialog
    val showAddHearingTypeDialog: Boolean = false,
    val showAddSubHearingTypeDialog: Boolean = false,
    val showAddCourtDialog: Boolean = false,
    // Misc
    val isAutoFilling: Boolean = false,
    val success: Boolean = false,
    val error: String = "",

    val showAddToTaskScreen: Boolean = false,
    val taskCategories: List<com.smartfingers.smartlawyerplus.domain.model.TaskCategory> = emptyList(),
    val isLoadingTaskCategories: Boolean = false,
)