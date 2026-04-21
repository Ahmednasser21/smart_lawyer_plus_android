package com.smartfingers.smartlawyerplus.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CourtsResponseDto(
    @SerializedName("totalItems") val totalItems: Int?,
    @SerializedName("items") val items: List<FilterItemDto>?,
)

data class HearingTypesResponseDto(
    @SerializedName("totalItems") val totalItems: Int?,
    @SerializedName("items") val items: List<FilterItemDto>?,
)

data class BranchesResponseDto(
    @SerializedName("totalItems") val totalItems: Int?,
    @SerializedName("items") val items: List<FilterItemDto>?,
)

data class PartiesResponseDto(
    @SerializedName("totalItems") val totalItems: Int?,
    @SerializedName("items") val items: List<FilterItemDto>?,
)

data class FilterItemDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
)

data class EmployeeDto(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
)

data class CaseItemDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
)