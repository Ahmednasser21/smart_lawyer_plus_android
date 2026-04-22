package com.smartfingers.smartlawyerplus.data.remote.api

import com.smartfingers.smartlawyerplus.data.remote.dto.*
import retrofit2.http.*

interface TasksDetailApiService {

    @GET
    suspend fun getTaskDetails(@Url url: String): AppResponseDto<TaskDetailsDto>

    @GET
    suspend fun getTaskProjectInfo(@Url url: String): AppResponseDto<TaskProjectInfoDto>

    @GET
    suspend fun getTaskReplies(@Url url: String): List<TaskRepliesDto>

    @GET
    suspend fun getNewTaskNumber(@Url url: String): AppResponseDto<Int>

    @GET
    suspend fun getTaskCategories(@Url url: String): AppResponseDto<TaskCategoriesResponseDto>

    @GET
    suspend fun getTaskPriorities(@Url url: String): List<TaskPriorityDto>

    @GET
    suspend fun getTaskCases(@Url url: String): List<TaskItemDto>

    @GET
    suspend fun getTaskConsultations(@Url url: String): List<TaskItemDto>

    @GET
    suspend fun getTaskExecutiveCases(@Url url: String): List<TaskItemDto>

    @GET
    suspend fun getTaskProjectGenerals(@Url url: String): List<TaskItemDto>

    @GET
    suspend fun getEmployees(@Url url: String): List<EmployeeDto>

    @POST
    suspend fun addTask(@Url url: String, @Body body: AddTaskDto): AppResponseDto<Int>

    @PUT
    suspend fun editTask(@Url url: String, @Body body: AddTaskDto): AppResponseDto<Int>

    @GET
    suspend fun getHearingTypes(@Url url: String): AppResponseDto<HearingTypesResponseDto>

    @GET
    suspend fun getCourts(@Url url: String): AppResponseDto<CourtsResponseDto>

    @POST
    suspend fun addSession(@Url url: String, @Body body: AddSessionDto): AppResponseDto<Int>

    @GET
    suspend fun getAppointmentTypes(@Url url: String): AppResponseDto<AppointmentTypesResponseDto>

    @GET
    suspend fun getParties(@Url url: String): AppResponseDto<PartiesResponseDto>

    @POST
    suspend fun addAppointment(@Url url: String, @Body body: AddAppointmentDto): AppResponseDto<Int>
}