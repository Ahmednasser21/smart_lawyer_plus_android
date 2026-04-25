package com.smartfingers.smartlawyerplus.domain.repository

import com.smartfingers.smartlawyerplus.domain.model.*

interface SessionsAddRepository {
    suspend fun getHearingTypesForAdd(): Result<List<HearingType>>
    suspend fun getSubHearingTypesForAdd(): Result<List<HearingType>>
    suspend fun getCourtsForAdd(): Result<List<Court>>
    suspend fun getCasesForAdd(): Result<List<TaskCase>>
    suspend fun getEmployeesForAdd(): Result<List<TaskEmployee>>
    suspend fun addSession(request: AddSessionRequest): Result<Int>
}