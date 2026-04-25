package com.smartfingers.smartlawyerplus.data.remote.api

import com.smartfingers.smartlawyerplus.data.remote.dto.AppResponseDto
import com.smartfingers.smartlawyerplus.data.remote.dto.BranchesResponseDto
import com.smartfingers.smartlawyerplus.data.remote.dto.CaseItemDto
import com.smartfingers.smartlawyerplus.data.remote.dto.CourtsResponseDto
import com.smartfingers.smartlawyerplus.data.remote.dto.EmployeeDto
import com.smartfingers.smartlawyerplus.data.remote.dto.HearingActionSampleBodyDto
import com.smartfingers.smartlawyerplus.data.remote.dto.HearingActionSamplesResponseDto
import com.smartfingers.smartlawyerplus.data.remote.dto.HearingDetailsDto
import com.smartfingers.smartlawyerplus.data.remote.dto.HearingListItemDto
import com.smartfingers.smartlawyerplus.data.remote.dto.HearingPeriodDto
import com.smartfingers.smartlawyerplus.data.remote.dto.HearingStatusDto
import com.smartfingers.smartlawyerplus.data.remote.dto.HearingTypesResponseDto
import com.smartfingers.smartlawyerplus.data.remote.dto.HearingsListResponseDto
import com.smartfingers.smartlawyerplus.data.remote.dto.LastHearingDto
import com.smartfingers.smartlawyerplus.data.remote.dto.LastHearingNumberDto
import com.smartfingers.smartlawyerplus.data.remote.dto.PartiesResponseDto
import com.smartfingers.smartlawyerplus.data.remote.dto.SubHearingTypesResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface SessionsApiService {

    @GET
    suspend fun getHearings(@Url url: String): AppResponseDto<HearingsListResponseDto>

    @GET
    suspend fun getHearingStatuses(@Url url: String): List<HearingStatusDto>

    @GET
    suspend fun getHearingPeriods(@Url url: String): List<HearingPeriodDto>

    @GET
    suspend fun getCourts(@Url url: String): AppResponseDto<CourtsResponseDto>

    @GET
    suspend fun getCases(@Url url: String): AppResponseDto<List<CaseItemDto>>

    @GET
    suspend fun getHearingTypes(@Url url: String): AppResponseDto<HearingTypesResponseDto>


    @GET
    suspend fun getEmployees(@Url url: String): AppResponseDto<List<EmployeeDto>>

    @GET
    suspend fun getBranches(@Url url: String): AppResponseDto<BranchesResponseDto>

    @GET
    suspend fun getParties(@Url url: String): AppResponseDto<PartiesResponseDto>

    @GET
    suspend fun getHearingDetails(@Url url: String): AppResponseDto<HearingDetailsDto>

    @GET
    suspend fun getLastHearingNumberByCaseId(@Url url: String): AppResponseDto<LastHearingNumberDto>

    @GET
    suspend fun getLastHearingById(@Url url: String): AppResponseDto<LastHearingDto>

    @GET
    suspend fun getHearingActionSamples(@Url url: String): AppResponseDto<HearingActionSamplesResponseDto>

    @POST
    suspend fun addHearingActionSample(
        @Url url: String,
        @Body body: HearingActionSampleBodyDto,
    ): AppResponseDto<Int>

    @GET
    suspend fun getSubHearingTypes(@Url url: String): AppResponseDto<SubHearingTypesResponseDto>
}