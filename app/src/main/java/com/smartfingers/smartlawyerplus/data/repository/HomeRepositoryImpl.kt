package com.smartfingers.smartlawyerplus.data.repository

import com.smartfingers.smartlawyerplus.data.local.AppPreferences
import com.smartfingers.smartlawyerplus.data.remote.api.TasksApiService
import com.smartfingers.smartlawyerplus.domain.model.RecentHearing
import com.smartfingers.smartlawyerplus.domain.model.Task
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.repository.HomeRepository
import com.smartfingers.smartlawyerplus.util.AppErrorState
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val prefs: AppPreferences,
    private val api: TasksApiService,
    appErrorState: AppErrorState,
) : HomeRepository, BaseRepository(appErrorState) {

    private suspend fun baseUrl(): String =
        "${prefs.getBaseUrlOnce()}${prefs.getAppUrlOnce()}"

    override suspend fun getTasks(
        page: Int,
        pageSize: Int,
        taskFilterId: Int,
        taskScope: Int,
    ): Result<Pair<List<Task>, Int>> = safeApiCall {
        val url = "${baseUrl()}/api/Tasks?Page=$page&PageSize=$pageSize" +
                "&taskFilter=$taskFilterId&sortBy=time&isSortAscending=true" +
                "&showFilters=false&taskScope=$taskScope"
        val response = api.getTasks(url)
        if (response.isSuccess == true) {
            val tasks = response.data?.items?.map { dto ->
                Task(
                    id = dto.id ?: 0,
                    name = dto.name ?: "",
                    taskNumber = dto.taskNumber,
                    statusName = dto.taskStatusName,
                    taskStatus = dto.taskStatus,
                    priorityName = dto.priorityName,
                    remainingDays = dto.remainingTime?.days ?: 0,
                    remainingHours = dto.remainingTime?.hours ?: 0,
                    createdByUser = dto.createdByUser,
                    taskReplyApproveRequestsCount = dto.taskReplyApproveRequestsCount ?: 0,
                    taskReplyReviewRequestsCount = dto.taskReplyReviewRequestsCount ?: 0,
                    isSecret = dto.isSecret ?: false,
                )
            } ?: emptyList()
            Result.Success(tasks to (response.data?.totalItems ?: 0))
        } else {
            Result.Error(response.errorList?.firstOrNull() ?: "Failed to load tasks")
        }
    }

    override suspend fun getRecentHearings(): Result<List<RecentHearing>> = safeApiCall {
        val url = "${baseUrl()}/api/Hearings?sortBy=startDate&isSortAscending=true&Page=0&PageSize=5&Status=1"
        val response = api.getHearings(url)
        if (response.isSuccess == true) {
            Result.Success(response.data?.items?.map {
                RecentHearing(it.id ?: 0, it.hearingTypeName, it.courtName, it.startDate, it.caseName)
            } ?: emptyList())
        } else {
            Result.Error(response.errorList?.firstOrNull() ?: "Failed")
        }
    }
}