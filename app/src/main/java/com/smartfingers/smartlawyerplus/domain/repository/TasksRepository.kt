package com.smartfingers.smartlawyerplus.domain.repository

import com.smartfingers.smartlawyerplus.domain.model.*

interface TasksRepository {
    suspend fun getTaskDetails(taskId: Int): Result<TaskDetails>
    suspend fun getTaskProjectInfo(taskId: Int): Result<TaskProjectInfo>
    suspend fun getTaskReplies(taskId: Int): Result<List<TaskReply>>
    suspend fun getNewTaskNumber(): Result<Int>
    suspend fun getTaskCategories(): Result<List<TaskCategory>>
    suspend fun getTaskPriorities(): Result<List<TaskPriority>>
    suspend fun getTaskCases(): Result<List<TaskCase>>
    suspend fun getTaskConsultations(): Result<List<TaskConsultation>>
    suspend fun getTaskExecutiveCases(): Result<List<TaskExecutiveCase>>
    suspend fun getTaskProjectGenerals(): Result<List<TaskProjectGeneral>>
    suspend fun getTaskEmployees(): Result<List<TaskEmployee>>
    suspend fun addTask(request: AddTaskRequest): Result<Int>
    suspend fun editTask(id: String, request: AddTaskRequest): Result<Int>
}