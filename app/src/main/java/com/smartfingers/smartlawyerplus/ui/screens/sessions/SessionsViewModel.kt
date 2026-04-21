package com.smartfingers.smartlawyerplus.ui.screens.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfingers.smartlawyerplus.domain.model.HearingFilter
import com.smartfingers.smartlawyerplus.domain.model.HearingPeriod
import com.smartfingers.smartlawyerplus.domain.model.HearingStatus
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.usecase.sessions.GetHearingPeriodsUseCase
import com.smartfingers.smartlawyerplus.domain.usecase.sessions.GetHearingStatusesUseCase
import com.smartfingers.smartlawyerplus.domain.usecase.sessions.GetSessionsUseCase
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
                    val statuses = result.data.mapIndexed { index, status ->
                        status.copy(isSelected = index == 0)
                    }
                    val firstStatus = statuses.firstOrNull()
                    _uiState.update {
                        it.copy(
                            statuses = statuses,
                            selectedStatus = firstStatus,
                            filter = it.filter.copy(status = firstStatus?.id),
                        )
                    }
                    loadSessions(refresh = true)
                }
                is Result.Error -> {
                    loadSessions(refresh = true)
                }
                else -> Unit
            }

            when (val result = getHearingPeriodsUseCase()) {
                is Result.Success -> _uiState.update { it.copy(periods = result.data) }
                else -> Unit
            }
        }
    }

    fun selectStatus(status: HearingStatus) {
        val updated = _uiState.value.statuses.map {
            it.copy(isSelected = it.id == status.id)
        }
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
                filter = it.filter.copy(
                    dashboardPeriodType = period?.id?.toString(),
                    page = 0,
                ),
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
                    val allSessions =
                        if (refresh) newSessions else state.sessions + newSessions
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