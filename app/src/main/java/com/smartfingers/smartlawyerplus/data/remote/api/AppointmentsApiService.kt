package com.smartfingers.smartlawyerplus.data.remote.api

import com.smartfingers.smartlawyerplus.data.remote.dto.AppResponseDto
import com.smartfingers.smartlawyerplus.data.remote.dto.AppointmentDetailsResponseDto
import com.smartfingers.smartlawyerplus.data.remote.dto.AppointmentsListResponseDto
import com.smartfingers.smartlawyerplus.data.remote.dto.AppointmentTypesResponseDto
import retrofit2.http.GET
import retrofit2.http.Url

interface AppointmentsApiService {

    @GET
    suspend fun getAppointments(@Url url: String): AppResponseDto<AppointmentsListResponseDto>

    @GET
    suspend fun getAppointmentDetails(@Url url: String): AppResponseDto<AppointmentDetailsResponseDto>

    @GET
    suspend fun getAppointmentTypes(@Url url: String): AppResponseDto<AppointmentTypesResponseDto>
}