package com.smartfingers.smartlawyerplus.domain.usecase.sessions

import com.smartfingers.smartlawyerplus.domain.model.HearingActionSample
import com.smartfingers.smartlawyerplus.domain.model.HearingDetails
import com.smartfingers.smartlawyerplus.domain.model.LastHearing
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.repository.SessionsRepository
import javax.inject.Inject

class GetHearingDetailsUseCase @Inject constructor(
    private val repository: SessionsRepository,
) {
    suspend operator fun invoke(hearingId: Int): Result<HearingDetails> =
        repository.getHearingDetails(hearingId)
}

class GetLastHearingNumberByCaseIdUseCase @Inject constructor(
    private val repository: SessionsRepository,
) {
    suspend operator fun invoke(caseId: String): Result<LastHearing> =
        repository.getLastHearingNumberByCaseId(caseId)
}

class GetLastHearingByIdUseCase @Inject constructor(
    private val repository: SessionsRepository,
) {
    suspend operator fun invoke(hearingId: Int): Result<LastHearing> =
        repository.getLastHearingById(hearingId)
}

class GetHearingActionSamplesUseCase @Inject constructor(
    private val repository: SessionsRepository,
) {
    suspend operator fun invoke(): Result<List<HearingActionSample>> =
        repository.getHearingActionSamples()
}

class AddHearingActionSampleUseCase @Inject constructor(
    private val repository: SessionsRepository,
) {
    suspend operator fun invoke(name: String): Result<Int> =
        repository.addHearingActionSample(name)
}