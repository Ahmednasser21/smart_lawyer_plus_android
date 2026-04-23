package com.smartfingers.smartlawyerplus.ui.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.model.TaskFilter
import com.smartfingers.smartlawyerplus.domain.model.TaskFilterType
import com.smartfingers.smartlawyerplus.domain.repository.AuthRepository
import com.smartfingers.smartlawyerplus.domain.usecase.tasks.GetTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState

    private val defaultFilters = listOf(
        TaskFilter(4, "بانتظار الاعتماد", TaskFilterType.REQUIRES_APPROVAL),
        TaskFilter(1, "مهام منجزة", TaskFilterType.FINISHED),
        TaskFilter(2, "مهام غير منجزة", TaskFilterType.UNFINISHED, isSelected = true),
    )

    init {
        viewModelScope.launch {
            val user = authRepository.getCachedUser()
            val picture = authRepository.getUserPictureOnce()
            _uiState.update {
                it.copy(
                    userName = user?.givenName ?: "",
                    userPicture = picture,
                    filters = defaultFilters,
                    selectedFilter = defaultFilters.first { f -> f.isSelected },
                )
            }
        }
        loadTasks(refresh = true)
    }

    fun selectFilter(filter: TaskFilter) {
        _uiState.update { state ->
            state.copy(
                filters = state.filters.map { it.copy(isSelected = it.id == filter.id) },
                selectedFilter = filter,
                tasks = emptyList(),
                page = 0,
                hasMore = false,
            )
        }
        loadTasks(refresh = true)
    }

    fun selectScope(scope: TaskScope) {
        _uiState.update { it.copy(selectedScope = scope, tasks = emptyList(), page = 0, hasMore = false) }
        loadTasks(refresh = true)
    }

    fun loadMore() {
        if (_uiState.value.isLoadingMore || !_uiState.value.hasMore) return
        loadTasks(refresh = false)
    }

    fun refresh() {
        _uiState.update { it.copy(tasks = emptyList(), page = 0, hasMore = false) }
        loadTasks(refresh = true)
    }

    private fun loadTasks(refresh: Boolean) {
        val state = _uiState.value
        val page = if (refresh) 0 else state.page
        val filterId = state.selectedFilter?.id ?: 2
        val scopeId = state.selectedScope.id

        viewModelScope.launch {
            _uiState.update {
                if (refresh) it.copy(isLoading = true, error = "")
                else it.copy(isLoadingMore = true)
            }

            when (val result = getTasksUseCase(page, 10, filterId, scopeId)) {
                is Result.Success -> {
                    val (newTasks, total) = result.data
                    val allTasks = if (refresh) newTasks else state.tasks + newTasks
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            tasks = allTasks,
                            page = page + 1,
                            hasMore = total > allTasks.size,
                        )
                    }
                }
                is Result.Error -> _uiState.update {
                    it.copy(isLoading = false, isLoadingMore = false, error = result.message)
                }
                is Result.Loading -> Unit
            }
        }
    }
}