package com.smartfingers.smartlawyerplus.ui.screens.sessions

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfingers.smartlawyerplus.domain.model.*
import com.smartfingers.smartlawyerplus.domain.usecase.tasks.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddSessionViewModel @Inject constructor(
    private val getHearingTypes: GetHearingTypesForAddUseCase,
    private val getCourts: GetCourtsForAddUseCase,
    private val getCases: GetSessionCasesUseCase,
    private val getEmployees: GetSessionEmployeesUseCase,
    private val addSession: AddSessionUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AddSessionUiState())
    val state: StateFlow<AddSessionUiState> = _state

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            when (val r = getCases()) {
                is Result.Success -> _state.update { it.copy(cases = r.data) }
                is Result.Error -> {
                    _state.update { it.copy(error = r.message) }
                    Log.e("Cases Error", "loadData: ${r.message}", )
                }
                else -> Unit
            }
        }
        viewModelScope.launch {
            when (val r = getHearingTypes()) {
                is Result.Success -> _state.update { it.copy(hearingTypes = r.data) }
                else -> Unit
            }
        }
        viewModelScope.launch {
            when (val r = getCourts()) {
                is Result.Success -> _state.update { it.copy(courts = r.data) }
                else -> Unit
            }
        }
        viewModelScope.launch {
            when (val r = getEmployees()) {
                is Result.Success -> _state.update { it.copy(employees = r.data) }
                is Result.Error -> {
                    _state.update { it.copy(error = r.message) }
                    Log.e("GetEmployees", "loadData: ${r.message}" )
                }
                else -> Unit
            }
        }
    }

    fun onCaseSelected(c: TaskCase?) = _state.update { it.copy(selectedCase = c) }
    fun onHearingTypeSelected(h: HearingType?) = _state.update { it.copy(selectedHearingType = h) }
    fun onCourtSelected(c: Court?) = _state.update { it.copy(selectedCourt = c) }
    fun onEmployeeToggled(e: TaskEmployee) = _state.update { s ->
        val list = s.selectedEmployees.toMutableList()
        if (list.any { it.id == e.id }) list.removeAll { it.id == e.id } else list.add(e)
        s.copy(selectedEmployees = list)
    }

    fun onHearingNumberChange(v: String) = _state.update { it.copy(hearingNumber = v) }
    fun onCourtCircleChange(v: String) = _state.update { it.copy(courtCircle = v) }
    fun onJudgeNameChange(v: String) = _state.update { it.copy(judgeName = v) }
    fun onStartDateSelected(d: String) = _state.update { it.copy(startDate = d) }
    fun onStartTimeSelected(t: String) = _state.update { it.copy(startTime = t) }
    fun onHearingDescChange(v: String) = _state.update { it.copy(hearingDesc = v) }

    fun save() {
        val s = _state.value
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = "") }
            val request = AddSessionRequest(
                caseId = s.selectedCase?.id?.toString(),
                assignedUserIds = s.selectedEmployees.joinToString(",") { it.id },
                hearingNumber = s.hearingNumber.ifBlank { null },
                hearingTypeId = s.selectedHearingType?.id?.toString(),
                subHearingTypeId = null,
                courtId = s.selectedCourt?.id?.toString(),
                courtCircle = s.courtCircle.ifBlank { null },
                startDate = s.startDate.ifBlank { null },
                startTime = s.startTime.ifBlank { null },
                judgeName = s.judgeName.ifBlank { null },
                judgeOfficeNumber = null,
                hearingDesc = s.hearingDesc.ifBlank { null },
                requiredDocs = null,
            )
            when (val r = addSession(request)) {
                is Result.Success -> _state.update { it.copy(isLoading = false, success = true) }
                is Result.Error -> _state.update { it.copy(isLoading = false, error = r.message) }
                else -> Unit
            }
        }
    }
}