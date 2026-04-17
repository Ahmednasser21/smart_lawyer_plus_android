package com.smartfingers.smartlawyerplus.domain.repository

import com.smartfingers.smartlawyerplus.domain.model.RecentHearing
import com.smartfingers.smartlawyerplus.domain.model.Task
import com.smartfingers.smartlawyerplus.domain.model.Result

interface HomeRepository {
    suspend fun getTasks(
        page: Int,
        pageSize: Int,
        taskFilterId: Int,
        taskScope: Int,
    ): Result<Pair<List<Task>, Int>>

    suspend fun getRecentHearings(): Result<List<RecentHearing>>
}