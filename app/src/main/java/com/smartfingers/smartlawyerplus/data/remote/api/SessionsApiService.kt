package com.smartfingers.smartlawyerplus.data.remote.api

import com.smartfingers.smartlawyerplus.data.remote.dto.AppResponseDto
import com.smartfingers.smartlawyerplus.data.remote.dto.BranchesResponseDto
import com.smartfingers.smartlawyerplus.data.remote.dto.CaseItemDto
import com.smartfingers.smartlawyerplus.data.remote.dto.CourtsResponseDto
import com.smartfingers.smartlawyerplus.data.remote.dto.EmployeeDto
import com.smartfingers.smartlawyerplus.data.remote.dto.HearingListItemDto
import com.smartfingers.smartlawyerplus.data.remote.dto.HearingPeriodDto
import com.smartfingers.smartlawyerplus.data.remote.dto.HearingStatusDto
import com.smartfingers.smartlawyerplus.data.remote.dto.HearingTypesResponseDto
import com.smartfingers.smartlawyerplus.data.remote.dto.HearingsListResponseDto
import com.smartfingers.smartlawyerplus.data.remote.dto.PartiesResponseDto
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

    @GET
    suspend fun getCourts(@Url url: String): AppResponseDto<CourtsResponseDto>

    @GET
    suspend fun getCases(@Url url: String): AppResponseDto<List<CaseItemDto>>

    @GET
    suspend fun getHearingTypes(@Url url: String): AppResponseDto<HearingTypesResponseDto>

    @GET
    suspend fun getSubHearingTypes(@Url url: String): AppResponseDto<HearingTypesResponseDto>


    @GET
    suspend fun getEmployees(@Url url: String): AppResponseDto<List<EmployeeDto>>

    @GET
    suspend fun getBranches(@Url url: String): AppResponseDto<BranchesResponseDto>

    @GET
    suspend fun getParties(@Url url: String): AppResponseDto<PartiesResponseDto>
}