package com.smartfingers.smartlawyerplus.domain.usecase.appointments

import com.smartfingers.smartlawyerplus.domain.model.AppointmentDetails
import com.smartfingers.smartlawyerplus.domain.model.AppointmentListItem
import com.smartfingers.smartlawyerplus.domain.model.AppointmentsFilter
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.repository.AppointmentsRepository
import javax.inject.Inject

class GetAppointmentsUseCase @Inject constructor(
    private val repository: AppointmentsRepository,
) {
    suspend operator fun invoke(filter: AppointmentsFilter): Result<Pair<List<AppointmentListItem>, Int>> =
        repository.getAppointments(filter)
}

class GetAppointmentDetailsUseCase @Inject constructor(
    private val repository: AppointmentsRepository,
) {
    suspend operator fun invoke(appointmentId: Int): Result<AppointmentDetails> =
        repository.getAppointmentDetails(appointmentId)
}