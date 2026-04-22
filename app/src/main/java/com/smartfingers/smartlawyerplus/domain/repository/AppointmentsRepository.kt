package com.smartfingers.smartlawyerplus.domain.repository

import com.smartfingers.smartlawyerplus.domain.model.AppointmentDetails
import com.smartfingers.smartlawyerplus.domain.model.AppointmentListItem
import com.smartfingers.smartlawyerplus.domain.model.AppointmentsFilter
import com.smartfingers.smartlawyerplus.domain.model.Result

interface AppointmentsRepository {

    suspend fun getAppointments(filter: AppointmentsFilter): Result<Pair<List<AppointmentListItem>, Int>>

    suspend fun getAppointmentDetails(appointmentId: Int): Result<AppointmentDetails>
}