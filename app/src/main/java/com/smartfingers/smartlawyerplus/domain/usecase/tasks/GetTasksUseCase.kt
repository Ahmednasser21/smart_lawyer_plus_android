package com.smartfingers.smartlawyerplus.domain.usecase.tasks

import com.smartfingers.smartlawyerplus.domain.model.Task
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.repository.HomeRepository
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(
    private val repository: HomeRepository,
) {
    suspend operator fun invoke(
        page: Int = 0,
        pageSize: Int = 10,
        taskFilterId: Int = 2,
        taskScope: Int = 2,
    ): Result<Pair<List<Task>, Int>> =
        repository.getTasks(page, pageSize, taskFilterId, taskScope)
}