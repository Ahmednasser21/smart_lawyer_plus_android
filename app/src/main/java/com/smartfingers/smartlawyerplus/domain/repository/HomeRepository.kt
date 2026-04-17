package com.smartfingers.smartlawyerplus.domain.repository

import com.smartfingers.smartlawyerplus.domain.model.HomeStats
import com.smartfingers.smartlawyerplus.domain.model.RecentHearing
import com.smartfingers.smartlawyerplus.domain.model.RecentTask
import com.smartfingers.smartlawyerplus.domain.model.Result

interface HomeRepository {
    suspend fun getHomeStats(): Result<HomeStats>
    suspend fun getRecentTasks(): Result<List<RecentTask>>
    suspend fun getRecentHearings(): Result<List<RecentHearing>>
}