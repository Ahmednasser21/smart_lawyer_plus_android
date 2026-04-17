package com.smartfingers.smartlawyerplus.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TasksResponseDto(
    @SerializedName("totalItems") val totalItems: Int?,
    @SerializedName("items") val items: List<TaskItemDto>?,
)

data class TaskItemDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("taskStatusName") val taskStatusName: String?,
    @SerializedName("priorityName") val priorityName: String?,
    @SerializedName("taskNumber") val taskNumber: String?,
    @SerializedName("remainingTime") val remainingTime: RemainingTimeDto?,
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