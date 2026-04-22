package com.smartfingers.smartlawyerplus.domain.repository

import com.smartfingers.smartlawyerplus.domain.model.*

interface AppointmentsAddRepository {
    suspend fun getAppointmentTypesForAdd(): Result<List<AppointmentType>>
    suspend fun getEmployeesForAdd(): Result<List<TaskEmployee>>
    suspend fun getPartiesForAdd(): Result<List<Party>>
    suspend fun getCasesForAdd(): Result<List<TaskCase>>
    suspend fun addAppointment(request: AddAppointmentRequest): Result<Int>
}