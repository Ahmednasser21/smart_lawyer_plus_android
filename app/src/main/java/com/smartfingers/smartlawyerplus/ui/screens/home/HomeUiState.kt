package com.smartfingers.smartlawyerplus.ui.screens.home

import com.smartfingers.smartlawyerplus.domain.model.HomeStats
import com.smartfingers.smartlawyerplus.domain.model.RecentHearing
import com.smartfingers.smartlawyerplus.domain.model.RecentTask

data class HomeUiState(
    val isLoading: Boolean = false,
    val stats: HomeStats? = null,
    val recentTasks: List<RecentTask> = emptyList(),
    val recentHearings: List<RecentHearing> = emptyList(),
    val userName: String = "",
    val error: String = "",
)