package com.smartfingers.smartlawyerplus.ui.screens.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfingers.smartlawyerplus.domain.model.*
import com.smartfingers.smartlawyerplus.domain.usecase.tasks.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class AddAppointmentViewModel @Inject constructor(
    private val getTypes: GetAppointmentTypesForAddUseCase,
    private val getEmployees: GetAppointmentEmployeesUseCase,
    private val getParties: GetPartiesForAddUseCase,
    private val getCases: GetAppointmentCasesUseCase,
    private val addAppointment: AddAppointmentUseCase2,
    private val uploadAttachmentUseCase: com.smartfingers.smartlawyerplus.domain.usecase.report.UploadReportAttachmentUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AddAppointmentUiState())
    val state: StateFlow<AddAppointmentUiState> = _state

    init { loadData() }

    private fun loadData() {
        viewModelScope.launch {
            when (val r = getTypes()) {
                is Result.Success -> _state.update { it.copy(types = r.data) }
                else -> Unit
            }
        }
        viewModelScope.launch {
            when (val r = getEmployees()) {
                is Result.Success -> _state.update { it.copy(employees = r.data) }
                else -> Unit
            }
        }
        viewModelScope.launch {
            when (val r = getParties()) {
                is Result.Success -> _state.update { it.copy(parties = r.data) }
                else -> Unit
            }
        }
        viewModelScope.launch {
            when (val r = getCases()) {
                is Result.Success -> _state.update { it.copy(cases = r.data) }
                else -> Unit
            }
        }
    }

    // ── Project type ──────────────────────────────────────────────────────────

    fun onProjectTypeSelected(type: ProjectType) {
        _state.update {
            it.copy(
                projectType = type,
                selectedProjectId = null,
                selectedProjectName = "",
                projectOptions = emptyList(),
                selectedCase = null,
            )
        }
        when (type) {
            ProjectType.CASE -> _state.update {
                it.copy(projectOptions = it.cases.map { c -> ProjectOption(c.id, c.name) })
            }
            else -> Unit // consultations/project generals need extra use cases — stub for now
        }
    }

    fun onProjectSelected(id: String?, name: String) =
        _state.update { it.copy(selectedProjectId = id, selectedProjectName = name) }

    // ── Field updates ─────────────────────────────────────────────────────────

    fun onSubjectChange(v: String) = _state.update { it.copy(subject = v) }

    fun onTypeSelected(t: AppointmentType?) = _state.update { it.copy(selectedType = t) }

    fun onCaseSelected(c: TaskCase?) = _state.update { it.copy(selectedCase = c) }

    fun onEmployeeToggled(e: TaskEmployee) = _state.update { s ->
        val list = s.selectedEmployees.toMutableList()
        if (list.any { it.id == e.id }) list.removeAll { it.id == e.id } else list.add(e)
        s.copy(selectedEmployees = list)
    }

    fun onPartyToggled(p: Party) = _state.update { s ->
        val list = s.selectedParties.toMutableList()
        if (list.any { it.id == p.id }) list.removeAll { it.id == p.id } else list.add(p)
        s.copy(selectedParties = list)
    }

    fun onDateSelected(d: String) = _state.update { it.copy(startDate = d, startDateHijri = toHijri(d)) }

    fun onStartTimeSelected(t: String) = _state.update { it.copy(startTime = t) }

    // ── Dialogs ───────────────────────────────────────────────────────────────

    fun openAddClientDialog() = _state.update { it.copy(showAddClientDialog = true) }
    fun dismissAddClientDialog() = _state.update { it.copy(showAddClientDialog = false) }

    fun addClient(name: String, phone: String, tax: String) {
        // Optimistically add to parties list as a local entry
        val tempParty = Party(id = -System.currentTimeMillis().toInt(), name = name)
        _state.update {
            it.copy(
                showAddClientDialog = false,
                parties = it.parties + tempParty,
                selectedParties = it.selectedParties + tempParty,
            )
        }
    }

    fun openAddTypeDialog() = _state.update { it.copy(showAddTypeDialog = true) }
    fun dismissAddTypeDialog() = _state.update { it.copy(showAddTypeDialog = false) }

    fun addAppointmentType(name: String) {
        val newType = AppointmentType(id = -System.currentTimeMillis().toInt(), name = name)
        _state.update {
            it.copy(
                showAddTypeDialog = false,
                types = it.types + newType,
                selectedType = newType,
            )
        }
    }

    // ── Attachments ───────────────────────────────────────────────────────────

    fun uploadAttachment(filePath: String, mimeType: String) {
        viewModelScope.launch {
            _state.update { it.copy(isUploadingAttachment = true) }
            when (val r = uploadAttachmentUseCase(filePath, mimeType)) {
                is Result.Success -> _state.update {
                    it.copy(isUploadingAttachment = false, attachments = it.attachments + r.data)
                }
                is Result.Error -> _state.update {
                    it.copy(isUploadingAttachment = false, error = r.message)
                }
                else -> Unit
            }
        }
    }

    fun removeAttachment(id: Int) =
        _state.update { it.copy(attachments = it.attachments.filter { a -> a.id != id }) }

    // ── Save ──────────────────────────────────────────────────────────────────

    fun save() {
        val s = _state.value
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = "") }
            val caseId = when (s.projectType) {
                ProjectType.CASE -> s.selectedProjectId
                else -> null
            }
            val consultationId = when (s.projectType) {
                ProjectType.CONSULTATION -> s.selectedProjectId
                else -> null
            }
            val clientRequestId = when (s.projectType) {
                ProjectType.CUSTOMER_REQUESTS -> s.selectedProjectId
                else -> null
            }
            val projectGeneralId = when (s.projectType) {
                ProjectType.OTHER_PROJECTS -> s.selectedProjectId
                else -> null
            }
            val request = AddAppointmentRequest(
                typeId = s.selectedType?.id?.toString(),
                assignedUserIds = s.selectedEmployees.joinToString(",") { it.id },
                partiesIds = s.selectedParties.joinToString(",") { it.id.toString() },
                startDate = s.startDate.ifBlank { null },
                startTime = s.startTime.ifBlank { null },
                subject = s.subject.ifBlank { null },
                caseId = caseId,
                consultationId = consultationId,
                executiveCaseId = null,
                projectGeneralId = projectGeneralId,
                clientRequestId = clientRequestId,
            )
            when (val r = addAppointment(request)) {
                is Result.Success -> _state.update { it.copy(isLoading = false, success = true) }
                is Result.Error -> _state.update { it.copy(isLoading = false, error = r.message) }
                else -> Unit
            }
        }
    }

    fun onTimeSelected(t: String) = _state.update { it.copy(startTime = t) }

    // ── Hijri helper ──────────────────────────────────────────────────────────

    private fun toHijri(gregorian: String): String {
        return try {
            val parts = gregorian.split("-")
            if (parts.size != 3) return ""
            val localDate = java.time.LocalDate.of(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
            val hijri = java.time.chrono.HijrahDate.from(localDate)
            "${hijri.get(java.time.temporal.ChronoField.YEAR)}-${
                hijri.get(java.time.temporal.ChronoField.MONTH_OF_YEAR).toString().padStart(2, '0')
            }-${hijri.get(java.time.temporal.ChronoField.DAY_OF_MONTH).toString().padStart(2, '0')}"
        } catch (_: Exception) { "" }
    }
}