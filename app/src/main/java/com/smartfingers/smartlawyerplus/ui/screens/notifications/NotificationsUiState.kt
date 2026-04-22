package com.smartfingers.smartlawyerplus.ui.screens.notifications

import com.smartfingers.smartlawyerplus.domain.model.AppNotification

data class NotificationsUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val notifications: List<AppNotification> = emptyList(),
    val hasMore: Boolean = false,
    val page: Int = 0,
    val totalUnread: Int = 0,
    val error: String = "",
)
