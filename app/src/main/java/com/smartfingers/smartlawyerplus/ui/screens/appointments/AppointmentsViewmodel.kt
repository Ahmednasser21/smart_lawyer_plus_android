package com.smartfingers.smartlawyerplus.ui.screens.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfingers.smartlawyerplus.domain.model.AppointmentsFilter
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.usecase.appointments.GetAppointmentDetailsUseCase
import com.smartfingers.smartlawyerplus.domain.usecase.appointments.GetAppointmentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── Appointments list ViewModel ───────────────────────────────────────────────

@HiltViewModel
class AppointmentsViewModel @Inject constructor(
    private val getAppointmentsUseCase: GetAppointmentsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppointmentsUiState())
    val uiState: StateFlow<AppointmentsUiState> = _uiState

    // Mirrors iOS: finished tab + scheduled tab (isFinished = false is "Scheduled")
    private val defaultTabs = listOf(
        AppointmentFilterTab(id = 1, name = "منجزة", isFinished = true),
        AppointmentFilterTab(id = 2, name = "مجدولة", isFinished = false, isSelected = true),
    )

    init {
        val selected = defaultTabs.first { it.isSelected }
        _uiState.update { it.copy(filterTabs = defaultTabs, selectedTab = selected) }
        loadAppointments(refresh = true)
    }

    fun selectTab(tab: AppointmentFilterTab) {
        val updated = defaultTabs.map { it.copy(isSelected = it.id == tab.id) }
        _uiState.update {
            it.copy(
                filterTabs = updated,
                selectedTab = tab,
                appointments = emptyList(),
                page = 0,
                hasMore = false,
            )
        }
        loadAppointments(refresh = true)
    }

    fun refresh() {
        _uiState.update { it.copy(appointments = emptyList(), page = 0, hasMore = false) }
        loadAppointments(refresh = true)
    }

    fun loadMore() {
        if (_uiState.value.isLoadingMore || !_uiState.value.hasMore) return
        loadAppointments(refresh = false)
    }

    private fun loadAppointments(refresh: Boolean) {
        val state = _uiState.value
        val page = if (refresh) 0 else state.page
        val isFinished = state.selectedTab?.isFinished ?: false

        viewModelScope.launch {
            _uiState.update {
                if (refresh) it.copy(isLoading = true, error = "")
                else it.copy(isLoadingMore = true)
            }
            when (val result = getAppointmentsUseCase(
                AppointmentsFilter(isFinished = isFinished, page = page, pageSize = 10),
            )) {
                is Result.Success -> {
                    val (newItems, total) = result.data
                    val all = if (refresh) newItems else state.appointments + newItems
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            appointments = all,
                            page = page + 1,
                            hasMore = total > all.size,
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
}

// ── Appointment details ViewModel ─────────────────────────────────────────────

@HiltViewModel
class AppointmentDetailsViewModel @Inject constructor(
    private val getAppointmentDetailsUseCase: GetAppointmentDetailsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppointmentDetailsUiState())
    val uiState: StateFlow<AppointmentDetailsUiState> = _uiState

    fun loadDetails(appointmentId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = "") }
            when (val result = getAppointmentDetailsUseCase(appointmentId)) {
                is Result.Success -> _uiState.update {
                    it.copy(isLoading = false, details = result.data)
                }

                is Result.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }

                else -> Unit
            }
        }
    }
}