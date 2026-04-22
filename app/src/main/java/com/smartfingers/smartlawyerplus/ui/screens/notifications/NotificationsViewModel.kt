package com.smartfingers.smartlawyerplus.ui.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.usecase.calendar.GetNotificationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState

    init {
        loadNotifications(refresh = true)
    }

    fun refresh() {
        _uiState.update { it.copy(notifications = emptyList(), page = 0, hasMore = false) }
        loadNotifications(refresh = true)
    }

    fun loadMore() {
        if (_uiState.value.isLoadingMore || !_uiState.value.hasMore) return
        loadNotifications(refresh = false)
    }

    private fun loadNotifications(refresh: Boolean) {
        val state = _uiState.value
        val page = if (refresh) 1 else state.page + 1   // iOS uses 1-based page

        viewModelScope.launch {
            _uiState.update {
                if (refresh) it.copy(isLoading = true, error = "")
                else it.copy(isLoadingMore = true)
            }

            when (val result = getNotificationsUseCase(page = page, pageSize = 10)) {
                is Result.Success -> {
                    val data = result.data
                    val all = if (refresh) data.items else state.notifications + data.items
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            notifications = all,
                            page = page,
                            hasMore = data.totalItems > all.size,
                            totalUnread = data.totalNotReadItems,
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
