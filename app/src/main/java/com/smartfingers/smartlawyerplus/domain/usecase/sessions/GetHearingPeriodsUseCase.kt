package com.smartfingers.smartlawyerplus.domain.usecase.sessions

import com.smartfingers.smartlawyerplus.domain.model.HearingPeriod
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.repository.SessionsRepository
import javax.inject.Inject

class GetHearingPeriodsUseCase @Inject constructor(
    private val repository: SessionsRepository,
) {
    suspend operator fun invoke(): Result<List<HearingPeriod>> =
        repository.getHearingPeriods()
}