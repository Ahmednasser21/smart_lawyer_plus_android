package com.smartfingers.smartlawyerplus.ui.screens.sessions

import com.smartfingers.smartlawyerplus.domain.model.HearingDetails
import com.smartfingers.smartlawyerplus.domain.model.Session

data class SessionDetailsUiState(
    val session: Session? = null,
    val hearingDetails: HearingDetails? = null,
    val isLoading: Boolean = false,
    val error: String = "",
    val selectedTab: Int = 1,
)