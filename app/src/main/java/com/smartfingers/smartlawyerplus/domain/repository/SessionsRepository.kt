package com.smartfingers.smartlawyerplus.domain.repository

import com.smartfingers.smartlawyerplus.domain.model.HearingFilter
import com.smartfingers.smartlawyerplus.domain.model.HearingPeriod
import com.smartfingers.smartlawyerplus.domain.model.HearingStatus
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.model.Session

interface SessionsRepository {
    suspend fun getSessions(filter: HearingFilter): Result<Pair<List<Session>, Int>>
    suspend fun getHearingStatuses(): Result<List<HearingStatus>>
    suspend fun getHearingPeriods(): Result<List<HearingPeriod>>
}