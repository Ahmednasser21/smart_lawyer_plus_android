package com.smartfingers.smartlawyerplus.data.remote.api

import com.smartfingers.smartlawyerplus.data.remote.dto.CalendarEventDto
import com.smartfingers.smartlawyerplus.data.remote.dto.CalendarItemTypeDto
import com.smartfingers.smartlawyerplus.data.remote.dto.NotificationsResponseDto
import com.smartfingers.smartlawyerplus.data.remote.dto.AppResponseDto
import retrofit2.http.GET
import retrofit2.http.Url

interface CalendarApiService {

    // Mirrors: /api/Calendar/GetCalendarData?startDate=...&endDate=...&userId=...&typeId=...
    @GET
    suspend fun getCalendarData(@Url url: String): List<CalendarEventDto>

    // Mirrors: /api/Employees/List  (reuses EmployeeDto from SessionsDtos)
    // Already in SessionsApiService — shared via DI

    // Mirrors: /api/Enums/GetCalendarItemTypes
    @GET
    suspend fun getCalendarItemTypes(@Url url: String): List<CalendarItemTypeDto>
}

interface NotificationsApiService {

    // Mirrors: /api/notification?isMessages=false&isRead=false&Page=...&PageSize=...
    @GET
    suspend fun getNotifications(@Url url: String): AppResponseDto<NotificationsResponseDto>
}
