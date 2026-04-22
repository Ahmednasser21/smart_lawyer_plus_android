package com.smartfingers.smartlawyerplus.domain.usecase.calendar

import com.smartfingers.smartlawyerplus.domain.model.CalendarEvent
import com.smartfingers.smartlawyerplus.domain.model.CalendarItemType
import com.smartfingers.smartlawyerplus.domain.model.FilterOption
import com.smartfingers.smartlawyerplus.domain.model.NotificationsPage
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.repository.CalendarRepository
import com.smartfingers.smartlawyerplus.domain.repository.NotificationsRepository
import javax.inject.Inject

class GetCalendarDataUseCase @Inject constructor(
    private val repository: CalendarRepository,
) {
    suspend operator fun invoke(
        startDate: String,
        endDate: String,
        userId: String? = null,
        typeId: String? = null,
    ): Result<List<CalendarEvent>> =
        repository.getCalendarData(startDate, endDate, userId, typeId)
}

class GetCalendarItemTypesUseCase @Inject constructor(
    private val repository: CalendarRepository,
) {
    suspend operator fun invoke(): Result<List<CalendarItemType>> =
        repository.getCalendarItemTypes()
}

class GetCalendarEmployeesUseCase @Inject constructor(
    private val repository: CalendarRepository,
) {
    suspend operator fun invoke(): Result<List<FilterOption>> =
        repository.getEmployees()
}

class GetNotificationsUseCase @Inject constructor(
    private val repository: NotificationsRepository,
) {
    suspend operator fun invoke(page: Int, pageSize: Int): Result<NotificationsPage> =
        repository.getNotifications(page, pageSize)
}
