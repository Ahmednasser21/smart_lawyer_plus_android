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
class AddTaskViewModel @Inject constructor(
    private val getNewTaskNumber: GetNewTaskNumberUseCase,
    private val getCategories: GetTaskCategoriesUseCase,
    private val getPriorities: GetTaskPrioritiesUseCase,
    private val getCases: GetTaskCasesUseCase,
    private val getEmployees: GetTaskEmployeesUseCase,
    private val addTask: AddTaskUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AddTaskUiState())
    val state: StateFlow<AddTaskUiState> = _state

    init { loadInitialData() }

    private fun loadInitialData() {
        viewModelScope.launch {
            when (val r = getNewTaskNumber()) {
                is Result.Success -> _state.update { it.copy(taskNumber = "MAIN${r.data}") }
                else -> Unit
            }
        }
        viewModelScope.launch {
            when (val r = getPriorities()) {
                is Result.Success -> _state.update { it.copy(priorities = r.data) }
                else -> Unit
            }
        }
        viewModelScope.launch {
            when (val r = getCategories()) {
                is Result.Success -> _state.update { it.copy(categories = r.data) }
                else -> Unit
            }
        }
        viewModelScope.launch {
            when (val r = getEmployees()) {
                is Result.Success -> _state.update { it.copy(employees = r.data) }
                else -> Unit
            }
        }
        viewModelScope.launch {
            when (val r = getCases()) {
                is Result.Success -> _state.update { it.copy(cases = r.data) }
                else -> Unit
            }
        }
    }

    fun onNameChange(v: String) = _state.update { it.copy(taskName = v, taskNameError = "") }
    fun onSecretToggle() = _state.update { it.copy(isSecret = !it.isSecret) }
    fun onMessageChange(v: String) = _state.update { it.copy(requestMessage = v) }
    fun onPrioritySelected(p: TaskPriority) = _state.update { it.copy(selectedPriority = p) }
    fun onCategorySelected(c: TaskCategory) = _state.update { it.copy(selectedCategory = c) }
    fun onEmployeeToggled(e: TaskEmployee) = _state.update { s ->
        val list = s.selectedEmployees.toMutableList()
        if (list.any { it.id == e.id }) list.removeAll { it.id == e.id } else list.add(e)
        s.copy(selectedEmployees = list)
    }
    fun onCaseSelected(c: TaskCase?) = _state.update { it.copy(selectedCase = c) }
    fun onStartDateSelected(d: String) = _state.update { it.copy(startDate = d) }
    fun onEndDateSelected(d: String) = _state.update { it.copy(endDate = d) }

    fun save() {
        val s = _state.value
        if (s.taskName.isBlank()) {
            _state.update { it.copy(taskNameError = "اسم المهمة مطلوب") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = "") }
            val request = AddTaskRequest(
                name = s.taskName,
                taskNumber = s.taskNumber.replace("MAIN", ""),
                taskUsers = s.selectedEmployees.joinToString(",") { it.id },
                requestMessage = s.requestMessage.ifBlank { null },
                period = null,
                isSecret = s.isSecret,
                priority = s.selectedPriority?.id?.toString(),
                endDateType = "1",
                startDate = s.startDate.ifBlank { null },
                endDate = s.endDate.ifBlank { null },
                taskType = "1",
                taskStatus = null,
                taskCategoryId = s.selectedCategory?.id?.toString(),
                mainTaskId = null,
                caseId = s.selectedCase?.id?.toString(),
                consultationId = null,
                executiveCaseId = null,
                projectGeneralId = null,
            )
            when (val r = addTask(request)) {
                is Result.Success -> _state.update { it.copy(isLoading = false, success = true) }
                is Result.Error -> _state.update { it.copy(isLoading = false, error = r.message) }
                else -> Unit
            }
        }
    }
}