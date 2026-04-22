package com.smartfingers.smartlawyerplus.ui.screens.tasks

import com.smartfingers.smartlawyerplus.domain.model.TaskDetails
import com.smartfingers.smartlawyerplus.domain.model.TaskProjectInfo
import com.smartfingers.smartlawyerplus.domain.model.TaskReply

data class TaskDetailsUiState(
    val isLoading: Boolean = false,
    val details: TaskDetails? = null,
    val projectInfo: TaskProjectInfo? = null,
    val replies: List<TaskReply> = emptyList(),
    val selectedTab: TaskDetailsTab = TaskDetailsTab.DATA,
    val isLoadingReplies: Boolean = false,
    val error: String = "",
)

enum class TaskDetailsTab { DATA, RESPONSES }