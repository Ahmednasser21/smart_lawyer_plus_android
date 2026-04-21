package com.smartfingers.smartlawyerplus.ui.screens.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfingers.smartlawyerplus.domain.model.HearingFilter
import com.smartfingers.smartlawyerplus.domain.model.HearingPeriod
import com.smartfingers.smartlawyerplus.domain.model.HearingStatus
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.usecase.sessions.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionsViewModel @Inject constructor(
    private val getSessionsUseCase: GetSessionsUseCase,
    private val getHearingStatusesUseCase: GetHearingStatusesUseCase,
    private val getHearingPeriodsUseCase: GetHearingPeriodsUseCase,
    private val getCourtsUseCase: GetCourtsUseCase,
    private val getCasesUseCase: GetCasesUseCase,
    private val getHearingTypesUseCase: GetHearingTypesUseCase,
    private val getSubHearingTypesUseCase: GetSubHearingTypesUseCase,
    private val getEmployeesUseCase: GetEmployeesUseCase,
    private val getBranchesUseCase: GetBranchesUseCase,
    private val getPartiesUseCase: GetPartiesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionsUiState())
    val uiState: StateFlow<SessionsUiState> = _uiState

    init {
        loadMetadata()
    }

    private fun loadMetadata() {
        viewModelScope.launch {
            when (val result = getHearingStatusesUseCase()) {
                is Result.Success -> {
                    val rawStatuses = result.data
                    // Prefer id==1 (waiting/بالانتظار) as default, fallback to first
                    val defaultId = rawStatuses.firstOrNull { it.id == 1 }?.id
                        ?: rawStatuses.firstOrNull()?.id
                    val statuses = rawStatuses.map { it.copy(isSelected = it.id == defaultId) }
                    val firstStatus = statuses.firstOrNull { it.isSelected }
                    _uiState.update {
                        it.copy(
                            statuses = statuses,
                            selectedStatus = firstStatus,
                            filter = it.filter.copy(status = firstStatus?.id),
                        )
                    }
                    loadSessions(refresh = true)
                }
                is Result.Error -> loadSessions(refresh = true)
                else -> Unit
            }

            // Pre-load periods so they are ready for both bar and filter sheet
            when (val result = getHearingPeriodsUseCase()) {
                is Result.Success -> _uiState.update { it.copy(periods = result.data) }
                else -> Unit
            }
        }
    }

    // ── Lazy loaders for filter dropdowns ──

    fun loadCourtsIfNeeded() {
        if (_uiState.value.courts.isNotEmpty() || _uiState.value.isLoadingCourts) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingCourts = true) }
            if (getCourtsUseCase() is Result.Success) {
                val result = getCourtsUseCase()
                if (result is Result.Success)
                    _uiState.update { it.copy(courts = result.data, isLoadingCourts = false) }
                else _uiState.update { it.copy(isLoadingCourts = false) }
            } else _uiState.update { it.copy(isLoadingCourts = false) }
        }
    }

    fun loadCasesIfNeeded() {
        if (_uiState.value.cases.isNotEmpty() || _uiState.value.isLoadingCases) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingCases = true) }
            val result = getCasesUseCase()
            if (result is Result.Success)
                _uiState.update { it.copy(cases = result.data, isLoadingCases = false) }
            else _uiState.update { it.copy(isLoadingCases = false) }
        }
    }

    fun loadHearingTypesIfNeeded() {
        if (_uiState.value.hearingTypes.isNotEmpty() || _uiState.value.isLoadingHearingTypes) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingHearingTypes = true) }
            val result = getHearingTypesUseCase()
            if (result is Result.Success)
                _uiState.update { it.copy(hearingTypes = result.data, isLoadingHearingTypes = false) }
            else _uiState.update { it.copy(isLoadingHearingTypes = false) }
        }
    }

    fun loadSubHearingTypesIfNeeded() {
        if (_uiState.value.subHearingTypes.isNotEmpty() || _uiState.value.isLoadingSubHearingTypes) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingSubHearingTypes = true) }
            val result = getSubHearingTypesUseCase()
            if (result is Result.Success)
                _uiState.update { it.copy(subHearingTypes = result.data, isLoadingSubHearingTypes = false) }
            else _uiState.update { it.copy(isLoadingSubHearingTypes = false) }
        }
    }

    fun loadEmployeesIfNeeded() {
        if (_uiState.value.employees.isNotEmpty() || _uiState.value.isLoadingEmployees) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingEmployees = true) }
            val result = getEmployeesUseCase()
            if (result is Result.Success)
                _uiState.update { it.copy(employees = result.data, isLoadingEmployees = false) }
            else _uiState.update { it.copy(isLoadingEmployees = false) }
        }
    }

    fun loadBranchesIfNeeded() {
        if (_uiState.value.branches.isNotEmpty() || _uiState.value.isLoadingBranches) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingBranches = true) }
            val result = getBranchesUseCase()
            if (result is Result.Success)
                _uiState.update { it.copy(branches = result.data, isLoadingBranches = false) }
            else _uiState.update { it.copy(isLoadingBranches = false) }
        }
    }

    fun loadPartiesIfNeeded() {
        if (_uiState.value.parties.isNotEmpty() || _uiState.value.isLoadingParties) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingParties = true) }
            val result = getPartiesUseCase()
            if (result is Result.Success)
                _uiState.update { it.copy(parties = result.data, isLoadingParties = false) }
            else _uiState.update { it.copy(isLoadingParties = false) }
        }
    }

    fun selectStatus(status: HearingStatus) {
        val updated = _uiState.value.statuses.map { it.copy(isSelected = it.id == status.id) }
        _uiState.update {
            it.copy(
                statuses = updated,
                selectedStatus = status,
                filter = it.filter.copy(status = status.id, page = 0),
                sessions = emptyList(),
                page = 0,
                hasMore = false,
            )
        }
        loadSessions(refresh = true)
    }

    fun selectPeriod(period: HearingPeriod?) {
        _uiState.update {
            it.copy(
                selectedPeriod = period,
                filter = it.filter.copy(dashboardPeriodType = period?.id?.toString(), page = 0),
                sessions = emptyList(),
                page = 0,
                hasMore = false,
            )
        }
        loadSessions(refresh = true)
    }

    fun refresh() {
        _uiState.update { it.copy(sessions = emptyList(), page = 0, hasMore = false) }
        loadSessions(refresh = true)
    }

    fun loadMore() {
        if (_uiState.value.isLoadingMore || !_uiState.value.hasMore) return
        loadSessions(refresh = false)
    }

    private fun loadSessions(refresh: Boolean) {
        val state = _uiState.value
        val page = if (refresh) 0 else state.page
        val filter = state.filter.copy(page = page)

        viewModelScope.launch {
            _uiState.update {
                if (refresh) it.copy(isLoading = true, error = "")
                else it.copy(isLoadingMore = true)
            }
            when (val result = getSessionsUseCase(filter)) {
                is Result.Success -> {
                    val (newSessions, total) = result.data
                    val allSessions = if (refresh) newSessions else state.sessions + newSessions
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            sessions = allSessions,
                            page = page + 1,
                            hasMore = total > allSessions.size,
                        )
                    }
                }
                is Result.Error -> _uiState.update {
                    it.copy(isLoading = false, isLoadingMore = false, error = result.message)
                }
                else -> Unit
            }
        }
    }

    fun openFilterSheet() {
        _uiState.update { it.copy(showFilterSheet = true, pendingFilter = it.filter) }
    }

    fun dismissFilterSheet() {
        _uiState.update { it.copy(showFilterSheet = false) }
    }

    fun applyFilter(filter: HearingFilter) {
        _uiState.update {
            it.copy(
                showFilterSheet = false,
                filter = filter.copy(page = 0),
                sessions = emptyList(),
                page = 0,
                hasMore = false,
            )
        }
        loadSessions(refresh = true)
    }
}