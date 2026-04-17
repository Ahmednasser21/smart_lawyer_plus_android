package com.smartfingers.smartlawyerplus.data.repository

import com.smartfingers.smartlawyerplus.data.local.AppPreferences
import com.smartfingers.smartlawyerplus.data.remote.api.HomeApiService
import com.smartfingers.smartlawyerplus.domain.model.HomeStats
import com.smartfingers.smartlawyerplus.domain.model.RecentHearing
import com.smartfingers.smartlawyerplus.domain.model.RecentTask
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.repository.HomeRepository
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val prefs: AppPreferences,
    private val api: HomeApiService,
) : HomeRepository {

    private suspend fun baseUrl(): String {
        val base = prefs.getBaseUrlOnce()
        val app = prefs.getAppUrlOnce()
        return "$base$app"
    }

    override suspend fun getHomeStats(): Result<HomeStats> = runCatching {
        val base = baseUrl()
        val tasksUrl = "$base/api/Tasks?Page=0&PageSize=1&taskFilter=2&sortBy=time&isSortAscending=true&showFilters=false&taskScope=2"
        val pendingUrl = "$base/api/Tasks?Page=0&PageSize=1&taskFilter=4&sortBy=time&isSortAscending=true&showFilters=false&taskScope=2"
        val hearingsUrl = "$base/api/Hearings?sortBy=startDate&isSortAscending=true&Page=0&PageSize=1&Status=1"

        val tasksResp = api.getTasks(tasksUrl)
        val pendingResp = api.getTasks(pendingUrl)
        val hearingsResp = api.getHearings(hearingsUrl)

        Result.Success(
            HomeStats(
                tasksCount = tasksResp.data?.totalItems ?: 0,
                sessionsCount = hearingsResp.data?.totalItems ?: 0,
                appointmentsCount = 0,
                casesCount = 0,
                pendingTasksCount = pendingResp.data?.totalItems ?: 0,
                upcomingSessionsCount = hearingsResp.data?.totalItems ?: 0,
            )
        )
    }.getOrElse { Result.Error(it.message ?: "Network error") }

    override suspend fun getRecentTasks(): Result<List<RecentTask>> = runCatching {
        val url = "${baseUrl()}/api/Tasks?Page=0&PageSize=5&taskFilter=2&sortBy=time&isSortAscending=true&showFilters=false&taskScope=2"
        val response = api.getTasks(url)
        if (response.isSuccess == true) {
            val tasks = response.data?.items?.map {
                RecentTask(
                    id = it.id ?: 0,
                    name = it.name ?: "",
                    statusName = it.taskStatusName ?: "",
                    priorityName = it.priorityName,
                    remainingDays = it.remainingTime?.days ?: 0,
                    taskNumber = it.taskNumber,
                )
            } ?: emptyList()
            Result.Success(tasks)
        } else {
            Result.Error(response.errorList?.firstOrNull() ?: "Failed")
        }
    }.getOrElse { Result.Error(it.message ?: "Network error") }

    override suspend fun getRecentHearings(): Result<List<RecentHearing>> = runCatching {
        val url = "${baseUrl()}/api/Hearings?sortBy=startDate&isSortAscending=true&Page=0&PageSize=5&Status=1"
        val response = api.getHearings(url)
        if (response.isSuccess == true) {
            val hearings = response.data?.items?.map {
                RecentHearing(
                    id = it.id ?: 0,
                    hearingTypeName = it.hearingTypeName,
                    courtName = it.courtName,
                    startDate = it.startDate,
                    caseName = it.caseName,
                )
            } ?: emptyList()
            Result.Success(hearings)
        } else {
            Result.Error(response.errorList?.firstOrNull() ?: "Failed")
        }
    }.getOrElse { Result.Error(it.message ?: "Network error") }
}