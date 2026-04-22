package com.smartfingers.smartlawyerplus.ui.screens.tasks

import com.smartfingers.smartlawyerplus.domain.model.TaskCase
import com.smartfingers.smartlawyerplus.domain.model.TaskCategory
import com.smartfingers.smartlawyerplus.domain.model.TaskEmployee
import com.smartfingers.smartlawyerplus.domain.model.TaskPriority

data class AddTaskUiState(
    val isLoading: Boolean = false,
    val taskName: String = "",
    val taskNameError: String = "",
    val isSecret: Boolean = false,
    val requestMessage: String = "",
    val selectedPriority: TaskPriority? = null,
    val selectedCategory: TaskCategory? = null,
    val selectedEmployees: List<TaskEmployee> = emptyList(),
    val selectedCase: TaskCase? = null,
    val startDate: String = "",
    val endDate: String = "",
    val taskNumber: String = "",
    val priorities: List<TaskPriority> = emptyList(),
    val categories: List<TaskCategory> = emptyList(),
    val employees: List<TaskEmployee> = emptyList(),
    val cases: List<TaskCase> = emptyList(),
    val success: Boolean = false,
    val error: String = "",
)