package com.smartfingers.smartlawyerplus.ui.screens.appointments

import com.smartfingers.smartlawyerplus.domain.model.AppointmentType
import com.smartfingers.smartlawyerplus.domain.model.Party
import com.smartfingers.smartlawyerplus.domain.model.TaskCase
import com.smartfingers.smartlawyerplus.domain.model.TaskEmployee

data class AddAppointmentUiState(
    val isLoading: Boolean = false,
    val subject: String = "",
    val selectedType: AppointmentType? = null,
    val selectedCase: TaskCase? = null,
    val selectedEmployees: List<TaskEmployee> = emptyList(),
    val selectedParties: List<Party> = emptyList(),
    val startDate: String = "",
    val startTime: String = "",
    val types: List<AppointmentType> = emptyList(),
    val cases: List<TaskCase> = emptyList(),
    val employees: List<TaskEmployee> = emptyList(),
    val parties: List<Party> = emptyList(),
    val success: Boolean = false,
    val error: String = "",
)