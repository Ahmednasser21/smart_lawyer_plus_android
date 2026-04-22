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
    fun onStartDateSelected(d: String) = _state.update { it.copy(startDate = d) }
    fun onStartTimeSelected(t: String) = _state.update { it.copy(startTime = t) }

    fun save() {
        val s = _state.value
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = "") }
            val request = AddAppointmentRequest(
                typeId = s.selectedType?.id?.toString(),
                assignedUserIds = s.selectedEmployees.joinToString(",") { it.id },
                partiesIds = s.selectedParties.joinToString(",") { it.id.toString() },
                startDate = s.startDate.ifBlank { null },
                startTime = s.startTime.ifBlank { null },
                subject = s.subject.ifBlank { null },
                caseId = s.selectedCase?.id?.toString(),
                consultationId = null,
                executiveCaseId = null,
                projectGeneralId = null,
                clientRequestId = null,
            )
            when (val r = addAppointment(request)) {
                is Result.Success -> _state.update { it.copy(isLoading = false, success = true) }
                is Result.Error -> _state.update { it.copy(isLoading = false, error = r.message) }
                else -> Unit
            }
        }
    }
}