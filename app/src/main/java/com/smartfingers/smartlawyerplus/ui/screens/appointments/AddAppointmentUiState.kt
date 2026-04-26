package com.smartfingers.smartlawyerplus.ui.screens.appointments

import com.smartfingers.smartlawyerplus.domain.model.AppointmentFormTemplate
import com.smartfingers.smartlawyerplus.domain.model.AppointmentType
import com.smartfingers.smartlawyerplus.domain.model.Party
import com.smartfingers.smartlawyerplus.domain.model.ReportAttachment
import com.smartfingers.smartlawyerplus.domain.model.TaskCase
import com.smartfingers.smartlawyerplus.domain.model.TaskEmployee

data class AddAppointmentUiState(
    val isLoading: Boolean = false,
    val isUploadingAttachment: Boolean = false,
    val projectType: ProjectType = ProjectType.WITHOUT,
    val projectOptions: List<ProjectOption> = emptyList(),
    val selectedProjectId: String? = null,
    val selectedProjectName: String = "",
    val subject: String = "",
    val selectedType: AppointmentType? = null,
    val selectedCase: TaskCase? = null,
    val selectedEmployees: List<TaskEmployee> = emptyList(),
    val selectedParties: List<Party> = emptyList(),
    val startDate: String = "",
    val startDateHijri: String = "",
    val startTime: String = "",
    val types: List<AppointmentType> = emptyList(),
    val cases: List<TaskCase> = emptyList(),
    val employees: List<TaskEmployee> = emptyList(),
    val parties: List<Party> = emptyList(),
    val attachments: List<ReportAttachment> = emptyList(),
    val showAddClientDialog: Boolean = false,
    val showAddTypeDialog: Boolean = false,
    val success: Boolean = false,
    val error: String = "",
    val formTemplates: List<AppointmentFormTemplate> = emptyList(),
    val showAddTemplateDialog: Boolean = false,
    val editingTemplate:AppointmentFormTemplate? = null,
)

enum class ProjectType {
    WITHOUT, CASE, CONSULTATION, OTHER_PROJECTS, CUSTOMER_REQUESTS
}

data class ProjectOption(val id: Int, val name: String)