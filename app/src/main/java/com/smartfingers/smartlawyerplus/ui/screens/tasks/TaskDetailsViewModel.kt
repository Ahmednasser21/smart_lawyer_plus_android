package com.smartfingers.smartlawyerplus.ui.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfingers.smartlawyerplus.domain.model.*
import com.smartfingers.smartlawyerplus.domain.usecase.tasks.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailsViewModel @Inject constructor(
    private val getTaskDetails: GetTaskDetailsUseCase,
    private val getTaskProjectInfo: GetTaskProjectInfoUseCase,
    private val getTaskReplies: GetTaskRepliesUseCase,
    private val deleteTask: DeleteTaskUseCase,
    private val closeTask: CloseTaskUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(TaskDetailsUiState())
    val state: StateFlow<TaskDetailsUiState> = _state

    private val _navigateBack = MutableStateFlow(false)
    val navigateBack: StateFlow<Boolean> = _navigateBack

    fun load(taskId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val r = getTaskDetails(taskId)) {
                is Result.Success -> {
                    _state.update { it.copy(isLoading = false, details = r.data) }
                    loadProjectInfo(taskId)
                }
                is Result.Error -> _state.update { it.copy(isLoading = false, error = r.message) }
                else -> Unit
            }
        }
    }

    private fun loadProjectInfo(taskId: Int) {
        viewModelScope.launch {
            when (val r = getTaskProjectInfo(taskId)) {
                is Result.Success -> _state.update { it.copy(projectInfo = r.data) }
                else -> Unit
            }
        }
    }

    fun loadReplies(taskId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingReplies = true) }
            when (val r = getTaskReplies(taskId)) {
                is Result.Success -> _state.update { it.copy(isLoadingReplies = false, replies = r.data) }
                is Result.Error -> _state.update { it.copy(isLoadingReplies = false) }
                else -> Unit
            }
        }
    }

    fun selectTab(tab: TaskDetailsTab) = _state.update { it.copy(selectedTab = tab) }

    fun showDeleteTaskConfirm(taskId: Int) =
        _state.update { it.copy(showDeleteReplyConfirm = true, pendingTaskId = taskId) }

    fun showCloseTaskConfirm(taskId: Int) =
        _state.update { it.copy(showCloseTaskConfirm = true, pendingTaskId = taskId) }

    fun dismissConfirm() =
        _state.update { it.copy(showDeleteReplyConfirm = false, showCloseTaskConfirm = false, pendingTaskId = null) }

    fun confirmDeleteTask() {
        val taskId = _state.value.pendingTaskId ?: return
        viewModelScope.launch {
            when (deleteTask(taskId)) {
                is Result.Success -> {
                    _state.update { it.copy(showDeleteReplyConfirm = false, pendingTaskId = null) }
                    _navigateBack.value = true
                }
                is Result.Error -> _state.update { it.copy(showDeleteReplyConfirm = false) }
                else -> Unit
            }
        }
    }

    fun confirmCloseTask() {
        val taskId = _state.value.pendingTaskId ?: return
        viewModelScope.launch {
            closeTask(taskId)
            _state.update { it.copy(showCloseTaskConfirm = false, pendingTaskId = null) }
        }
    }

    fun onNavigateBackHandled() { _navigateBack.value = false }
}