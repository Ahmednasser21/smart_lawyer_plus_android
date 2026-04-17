package com.smartfingers.smartlawyerplus.data.remote.api

import com.smartfingers.smartlawyerplus.data.remote.dto.AppResponseDto
import com.smartfingers.smartlawyerplus.data.remote.dto.HearingsResponseDto
import com.smartfingers.smartlawyerplus.data.remote.dto.TasksResponseDto
import retrofit2.http.GET
import retrofit2.http.Url

interface HomeApiService {

    @GET
    suspend fun getTasks(@Url url: String): AppResponseDto<TasksResponseDto>

    @GET
    suspend fun getHearings(@Url url: String): AppResponseDto<HearingsResponseDto>
}