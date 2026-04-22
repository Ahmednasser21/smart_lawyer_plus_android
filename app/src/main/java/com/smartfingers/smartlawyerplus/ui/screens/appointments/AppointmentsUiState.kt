package com.smartfingers.smartlawyerplus.ui.screens.appointments

import com.smartfingers.smartlawyerplus.domain.model.AppointmentDetails
import com.smartfingers.smartlawyerplus.domain.model.AppointmentListItem

// ── Appointments list filter tabs — mirrors iOS finished/scheduled ─────────────

data class AppointmentFilterTab(
    val id: Int,
    val name: String,
    val isFinished: Boolean,
    val isSelected: Boolean = false,
)

data class AppointmentsUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val appointments: List<AppointmentListItem> = emptyList(),
    val filterTabs: List<AppointmentFilterTab> = emptyList(),
    val selectedTab: AppointmentFilterTab? = null,
    val hasMore: Boolean = false,
    val page: Int = 0,
    val error: String = "",
)

// ── Appointment details state ─────────────────────────────────────────────────

data class AppointmentDetailsUiState(
    val isLoading: Boolean = false,
    val details: AppointmentDetails? = null,
    val error: String = "",
)