package com.smartfingers.smartlawyerplus.data.remote.api

import com.smartfingers.smartlawyerplus.data.remote.dto.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface ReportApiService {

    @GET
    suspend fun getHearingTypes(@Url url: String): AppResponseDto<HearingTypesResponseDto>

    @GET
    suspend fun getFormTemplates(@Url url: String): AppResponseDto<FormTemplatesResponseDto>

    @GET
    suspend fun getJudgmentTypes(@Url url: String): List<JudgmentTypeDto>

    @GET
    suspend fun getReport(@Url url: String): AppResponseDto<ReportResponseDto>

    @POST
    suspend fun addReport(
        @Url url: String,
        @Body body: ReportBodyDto,
    ): AppResponseDto<Int>

    @PUT
    suspend fun updateReport(
        @Url url: String,
        @Body body: ReportBodyDto,
    ): AppResponseDto<Int>

    @Multipart
    @POST
    suspend fun uploadAttachment(
        @Url url: String,
        @Part file: MultipartBody.Part,
    ): AppResponseDto<UploadedAttachmentDto>
}