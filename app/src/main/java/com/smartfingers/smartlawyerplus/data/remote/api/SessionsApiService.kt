package com.smartfingers.smartlawyerplus.data.remote.api

import com.smartfingers.smartlawyerplus.data.remote.dto.AppResponseDto
import com.smartfingers.smartlawyerplus.data.remote.dto.HearingListItemDto
import com.smartfingers.smartlawyerplus.data.remote.dto.HearingPeriodDto
import com.smartfingers.smartlawyerplus.data.remote.dto.HearingStatusDto
import com.smartfingers.smartlawyerplus.data.remote.dto.HearingsListResponseDto
import retrofit2.http.GET
import retrofit2.http.Url

interface SessionsApiService {

    @GET
    suspend fun getHearings(@Url url: String): AppResponseDto<HearingsListResponseDto>

    @GET
    suspend fun getHearingStatuses(@Url url: String): AppResponseDto<List<HearingStatusDto>>

    @GET
    suspend fun getHearingPeriods(@Url url: String): AppResponseDto<List<HearingPeriodDto>>

    @GET
    suspend fun getHearingDetails(@Url url: String): AppResponseDto<HearingListItemDto>
}