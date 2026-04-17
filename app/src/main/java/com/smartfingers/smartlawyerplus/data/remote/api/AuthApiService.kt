package com.smartfingers.smartlawyerplus.data.remote.api

import com.smartfingers.smartlawyerplus.data.remote.dto.AppResponseDto
import com.smartfingers.smartlawyerplus.data.remote.dto.InitAppResponseDto
import com.smartfingers.smartlawyerplus.data.remote.dto.LoginRequestDto
import com.smartfingers.smartlawyerplus.data.remote.dto.LoginResponseDto
import com.smartfingers.smartlawyerplus.data.remote.dto.PasswordResetRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface AuthApiService {

    @POST
    suspend fun login(
        @Url url: String,
        @Body body: LoginRequestDto,
    ): AppResponseDto<LoginResponseDto>

    @GET
    suspend fun initApp(
        @Url url: String,
    ): AppResponseDto<InitAppResponseDto>

    @POST
    suspend fun sendOtp(
        @Url url: String,
        @Body body: PasswordResetRequestDto,
    ): AppResponseDto<Boolean>

    @POST
    suspend fun verifyOtp(
        @Url url: String,
        @Body body: PasswordResetRequestDto,
    ): AppResponseDto<Boolean>

    @POST
    suspend fun changePassword(
        @Url url: String,
        @Body body: PasswordResetRequestDto,
    ): AppResponseDto<Boolean>
}