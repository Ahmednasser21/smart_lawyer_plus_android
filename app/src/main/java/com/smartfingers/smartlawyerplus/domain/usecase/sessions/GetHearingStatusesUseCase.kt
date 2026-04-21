package com.smartfingers.smartlawyerplus.domain.usecase.sessions

import com.smartfingers.smartlawyerplus.domain.model.HearingStatus
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.repository.SessionsRepository
import javax.inject.Inject

class GetHearingStatusesUseCase @Inject constructor(
    private val repository: SessionsRepository,
) {
    suspend operator fun invoke(): Result<List<HearingStatus>> =
        repository.getHearingStatuses()
}