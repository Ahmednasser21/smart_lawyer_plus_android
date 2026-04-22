package com.smartfingers.smartlawyerplus.data.remote.api

import com.smartfingers.smartlawyerplus.data.remote.dto.AppResponseDto
import com.smartfingers.smartlawyerplus.data.remote.dto.CaseAttachmentsResponseDto
import com.smartfingers.smartlawyerplus.data.remote.dto.CaseClientsResponseDto
import com.smartfingers.smartlawyerplus.data.remote.dto.CaseDetailsResponseDto
import com.smartfingers.smartlawyerplus.data.remote.dto.CasesListResponseDto
import retrofit2.http.GET
import retrofit2.http.Url

interface CasesApiService {

    @GET
    suspend fun getCases(@Url url: String): AppResponseDto<CasesListResponseDto>

    @GET
    suspend fun getCaseDetails(@Url url: String): AppResponseDto<CaseDetailsResponseDto>

    @GET
    suspend fun getCaseAttachments(@Url url: String): AppResponseDto<CaseAttachmentsResponseDto>

    @GET
    suspend fun getCaseClients(@Url url: String): AppResponseDto<CaseClientsResponseDto>
}