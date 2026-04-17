package com.smartfingers.smartlawyerplus.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequestDto(
    @SerializedName("userName") val userName: String,
    @SerializedName("password") val password: String,
)

data class LoginResponseDto(
    @SerializedName("auth_token") val authToken: String?,
    @SerializedName("expires_in") val expiresIn: Int?,
    @SerializedName("refresh_token") val refreshToken: String?,
    @SerializedName("picture") val picture: String?,
)

data class InitAppResponseDto(
    @SerializedName("logo") val logo: String?,
    @SerializedName("systemType") val systemType: Int?,
)

data class PasswordResetRequestDto(
    @SerializedName("email") val email: String?,
    @SerializedName("resetCode") val resetCode: String?,
    @SerializedName("newPassword") val newPassword: String?,
)

data class AppResponseDto<T>(
    @SerializedName("isSuccess") val isSuccess: Boolean?,
    @SerializedName("statusCode") val statusCode: Int?,
    @SerializedName("data") val data: T?,
    @SerializedName("ErrorList") val errorList: List<String>?,
)