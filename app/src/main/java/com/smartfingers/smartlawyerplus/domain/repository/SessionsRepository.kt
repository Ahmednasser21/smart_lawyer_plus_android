package com.smartfingers.smartlawyerplus.domain.repository

import com.smartfingers.smartlawyerplus.domain.model.FilterOption
import com.smartfingers.smartlawyerplus.domain.model.HearingActionSample
import com.smartfingers.smartlawyerplus.domain.model.HearingDetails
import com.smartfingers.smartlawyerplus.domain.model.HearingFilter
import com.smartfingers.smartlawyerplus.domain.model.HearingPeriod
import com.smartfingers.smartlawyerplus.domain.model.HearingStatus
import com.smartfingers.smartlawyerplus.domain.model.LastHearing
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.model.Session

interface SessionsRepository {
    suspend fun getSessions(filter: HearingFilter): Result<Pair<List<Session>, Int>>
    suspend fun getHearingStatuses(): Result<List<HearingStatus>>
    suspend fun getHearingPeriods(): Result<List<HearingPeriod>>
    suspend fun getCourts(): Result<List<FilterOption>>
    suspend fun getCases(): Result<List<FilterOption>>
    suspend fun getHearingTypes(): Result<List<FilterOption>>
    suspend fun getSubHearingTypes(): Result<List<FilterOption>>
    suspend fun getEmployees(): Result<List<FilterOption>>
    suspend fun getBranches(): Result<List<FilterOption>>
    suspend fun getParties(): Result<List<FilterOption>>
    suspend fun getResultCounts(): Result<List<FilterOption>>
    suspend fun getDiscounts(): Result<List<FilterOption>>
    suspend fun getHearingDetails(hearingId: Int): Result<HearingDetails>
    suspend fun getLastHearingNumberByCaseId(caseId: String): Result<LastHearing>
    suspend fun getLastHearingById(hearingId: Int): Result<LastHearing>
    suspend fun getHearingActionSamples(): Result<List<HearingActionSample>>
    suspend fun addHearingActionSample(name: String): Result<Int>
}