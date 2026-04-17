package com.smartfingers.smartlawyerplus.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.repository.AuthRepository
import com.smartfingers.smartlawyerplus.domain.usecase.home.GetHomeStatsUseCase
import com.smartfingers.smartlawyerplus.domain.usecase.home.GetRecentHearingsUseCase
import com.smartfingers.smartlawyerplus.domain.usecase.home.GetRecentTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeStats: GetHomeStatsUseCase,
    private val getRecentTasks: GetRecentTasksUseCase,
    private val getRecentHearings: GetRecentHearingsUseCase,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = "") }

            val user = authRepository.getCachedUser()
            val statsDeferred = async { getHomeStats() }
            val tasksDeferred = async { getRecentTasks() }
            val hearingsDeferred = async { getRecentHearings() }

            val stats = statsDeferred.await()
            val tasks = tasksDeferred.await()
            val hearings = hearingsDeferred.await()

            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    userName = user?.givenName ?: "",
                    stats = if (stats is Result.Success) stats.data else state.stats,
                    recentTasks = if (tasks is Result.Success) tasks.data else state.recentTasks,
                    recentHearings = if (hearings is Result.Success) hearings.data else state.recentHearings,
                    error = when {
                        stats is Result.Error -> stats.message
                        else -> ""
                    },
                )
            }
        }
    }
}