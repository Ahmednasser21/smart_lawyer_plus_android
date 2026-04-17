package com.smartfingers.smartlawyerplus.ui.screens.tasks

import com.smartfingers.smartlawyerplus.R
import com.smartfingers.smartlawyerplus.domain.model.Task
import com.smartfingers.smartlawyerplus.domain.model.TaskFilter

data class TasksUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val filters: List<TaskFilter> = emptyList(),
    val selectedFilter: TaskFilter? = null,
    val selectedScope: TaskScope = TaskScope.RESPONSIBLE,
    val hasMore: Boolean = false,
    val page: Int = 0,
    val userName: String = "",
    val error: String = "",
)

enum class TaskScope(val id: Int, val labelRes: Int) {
    RESPONSIBLE(2, R.string.tasks_responsible),
    ALL(1, R.string.tasks_all),
    CREATED(3, R.string.tasks_i_created),
}