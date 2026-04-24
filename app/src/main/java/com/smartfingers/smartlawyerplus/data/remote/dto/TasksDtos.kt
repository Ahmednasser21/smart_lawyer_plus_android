package com.smartfingers.smartlawyerplus.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TasksResponseDto(
    @SerializedName("totalItems") val totalItems: Int?,
    @SerializedName("items") val items: List<TaskItemDto>?,
)

data class ApiResponse<T>(
    @SerializedName("isSuccess") val isSuccess: Boolean?,
    @SerializedName("statusCode") val statusCode: Int?,
    @SerializedName("data") val data: T?,
)

data class TaskItemDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("taskNumber") val taskNumber: String?,
    @SerializedName("taskStatusName") val taskStatusName: String?,
    @SerializedName("taskStatus") val taskStatus: Int?,
    @SerializedName("priorityName") val priorityName: String?,
    @SerializedName("remainingTime") val remainingTime: RemainingTimeDto?,
    @SerializedName("createdByUser") val createdByUser: String?,
    @SerializedName("taskReplyApproveRequestsCount") val taskReplyApproveRequestsCount: Int?,
    @SerializedName("taskReplyReviewRequestsCount") val taskReplyReviewRequestsCount: Int?,
    @SerializedName("isSecret") val isSecret: Boolean?,
)

data class RemainingTimeDto(
    @SerializedName("days") val days: Int?,
    @SerializedName("hours") val hours: Int?,
    @SerializedName("minutes") val minutes: Int?,
)

data class HearingsResponseDto(
    @SerializedName("totalItems") val totalItems: Int?,
    @SerializedName("items") val items: List<HearingItemDto>?,
)

data class HearingItemDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("hearingTypeName") val hearingTypeName: String?,
    @SerializedName("courtName") val courtName: String?,
    @SerializedName("startDate") val startDate: String?,
    @SerializedName("case") val caseName: String?,
)