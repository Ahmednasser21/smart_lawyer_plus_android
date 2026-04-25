package com.smartfingers.smartlawyerplus.ui.screens.sessions

import com.smartfingers.smartlawyerplus.domain.model.Court
import com.smartfingers.smartlawyerplus.domain.model.HearingActionSample
import com.smartfingers.smartlawyerplus.domain.model.HearingType
import com.smartfingers.smartlawyerplus.domain.model.SessionActionRequired
import com.smartfingers.smartlawyerplus.domain.model.TaskCase
import com.smartfingers.smartlawyerplus.domain.model.TaskEmployee

data class AddSessionUiState(
    val isLoading: Boolean = false,
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
    val startTime: String = "",
    val hearingDesc: String = "",
    val requiredDocs: String = "",
    val actionsRequired: List<SessionActionRequired> = emptyList(),
    val cases: List<TaskCase> = emptyList(),
    val hearingTypes: List<HearingType> = emptyList(),
    val subHearingTypes: List<HearingType> = emptyList(),
    val courts: List<Court> = emptyList(),
    val employees: List<TaskEmployee> = emptyList(),
    val actionSamples: List<HearingActionSample> = emptyList(),
    val isLoadingActionSamples: Boolean = false,
    val showActionSamplesDialog: Boolean = false,
    val actionSamplesTargetIndex: Int = -1,
    val showAddActionDialog: Boolean = false,
    val addActionTargetIndex: Int = -1,
    val isAutoFilling: Boolean = false,
    val success: Boolean = false,
    val error: String = "",
)