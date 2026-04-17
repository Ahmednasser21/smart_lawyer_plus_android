package com.smartfingers.smartlawyerplus.domain.usecase.home

import com.smartfingers.smartlawyerplus.domain.model.RecentHearing
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.repository.HomeRepository
import javax.inject.Inject

class GetRecentHearingsUseCase @Inject constructor(
    private val repository: HomeRepository,
) {
    suspend operator fun invoke(): Result<List<RecentHearing>> = repository.getRecentHearings()
}