package com.smartfingers.smartlawyerplus.ui.screens.sessions

import com.smartfingers.smartlawyerplus.domain.model.HearingFilter
import com.smartfingers.smartlawyerplus.domain.model.HearingPeriod
import com.smartfingers.smartlawyerplus.domain.model.HearingStatus
import com.smartfingers.smartlawyerplus.domain.model.Session

data class SessionsUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val sessions: List<Session> = emptyList(),
    val statuses: List<HearingStatus> = emptyList(),
    val periods: List<HearingPeriod> = emptyList(),
    val selectedStatus: HearingStatus? = null,
    val selectedPeriod: HearingPeriod? = null,
    val hasMore: Boolean = false,
    val page: Int = 0,
    val error: String = "",
    val filter: HearingFilter = HearingFilter(),
    val showFilterSheet: Boolean = false,
    val pendingFilter: HearingFilter = HearingFilter(),
)