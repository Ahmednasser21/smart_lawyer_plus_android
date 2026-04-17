package com.smartfingers.smartlawyerplus.domain.usecase.home

import com.smartfingers.smartlawyerplus.domain.model.HomeStats
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.repository.HomeRepository
import javax.inject.Inject

class GetHomeStatsUseCase @Inject constructor(
    private val repository: HomeRepository,
) {
    suspend operator fun invoke(): Result<HomeStats> = repository.getHomeStats()
}