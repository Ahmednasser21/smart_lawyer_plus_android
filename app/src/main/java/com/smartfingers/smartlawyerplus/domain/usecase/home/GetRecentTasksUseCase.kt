package com.smartfingers.smartlawyerplus.domain.usecase.home

import com.smartfingers.smartlawyerplus.domain.model.RecentTask
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.repository.HomeRepository
import javax.inject.Inject

class GetRecentTasksUseCase @Inject constructor(
    private val repository: HomeRepository,
) {
    suspend operator fun invoke(): Result<List<RecentTask>> = repository.getRecentTasks()
}