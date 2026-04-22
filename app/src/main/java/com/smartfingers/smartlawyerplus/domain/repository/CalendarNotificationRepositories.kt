package com.smartfingers.smartlawyerplus.domain.repository

import com.smartfingers.smartlawyerplus.domain.model.AppNotification
import com.smartfingers.smartlawyerplus.domain.model.CalendarEvent
import com.smartfingers.smartlawyerplus.domain.model.CalendarItemType
import com.smartfingers.smartlawyerplus.domain.model.FilterOption
import com.smartfingers.smartlawyerplus.domain.model.NotificationsPage
import com.smartfingers.smartlawyerplus.domain.model.Result

interface CalendarRepository {

    suspend fun getCalendarData(
        startDate: String,
        endDate: String,
        userId: String? = null,
        typeId: String? = null,
    ): Result<List<CalendarEvent>>

    suspend fun getCalendarItemTypes(): Result<List<CalendarItemType>>

    // Employees reused from SessionsRepository (same endpoint)
    suspend fun getEmployees(): Result<List<FilterOption>>
}

interface NotificationsRepository {

    suspend fun getNotifications(
        page: Int,
        pageSize: Int,
    ): Result<NotificationsPage>
}
