package com.smartfingers.smartlawyerplus.domain.usecase.sessions

import com.smartfingers.smartlawyerplus.domain.model.HearingFilter
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.model.Session
import com.smartfingers.smartlawyerplus.domain.repository.SessionsRepository
import javax.inject.Inject

class GetSessionsUseCase @Inject constructor(
    private val repository: SessionsRepository,
) {
    suspend operator fun invoke(filter: HearingFilter): Result<Pair<List<Session>, Int>> =
        repository.getSessions(filter)
}