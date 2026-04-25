package com.smartfingers.smartlawyerplus.data.repository

import com.smartfingers.smartlawyerplus.data.local.AppPreferences
import com.smartfingers.smartlawyerplus.data.remote.api.CalendarApiService
import com.smartfingers.smartlawyerplus.data.remote.api.NotificationsApiService
import com.smartfingers.smartlawyerplus.data.remote.api.SessionsApiService
import com.smartfingers.smartlawyerplus.domain.model.AppNotification
import com.smartfingers.smartlawyerplus.domain.model.CalendarEvent
import com.smartfingers.smartlawyerplus.domain.model.CalendarEventExtendedProps
import com.smartfingers.smartlawyerplus.domain.model.CalendarItemType
import com.smartfingers.smartlawyerplus.domain.model.FilterOption
import com.smartfingers.smartlawyerplus.domain.model.NotificationsPage
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.repository.CalendarRepository
import com.smartfingers.smartlawyerplus.domain.repository.NotificationsRepository
import com.smartfingers.smartlawyerplus.util.AppErrorState
import javax.inject.Inject

class CalendarRepositoryImpl @Inject constructor(
    private val prefs: AppPreferences,
    private val calendarApi: CalendarApiService,
    private val sessionsApi: SessionsApiService,
    appErrorState: AppErrorState
) : CalendarRepository, BaseRepository(appErrorState) {

    private suspend fun baseUrl(): String =
        "${prefs.getBaseUrlOnce()}${prefs.getAppUrlOnce()}"

    override suspend fun getCalendarData(
        startDate: String,
        endDate: String,
        userId: String?,
        typeId: String?,
    ): Result<List<CalendarEvent>> = safeApiCall {
        val userParam = userId ?: ""
        val typeParam = typeId ?: ""
        val url = "${baseUrl()}/api/Calendar/GetCalendarData" +
                "?startDate=$startDate&endDate=$endDate" +
                "&userId=$userParam&typeId=$typeParam"
        val response = calendarApi.getCalendarData(url)
        Result.Success(response.map { dto ->
            CalendarEvent(
                id = dto.id ?: 0,
                title = dto.title,
                start = dto.start,
                end = dto.end,
                url = dto.url,
                color = dto.color,
                type = dto.type,
                extendedProperties = dto.extendedProperties?.let { ep ->
                    CalendarEventExtendedProps(
                        executiveCaseName = ep.executiveCaseName,
                        caseName = ep.caseName,
                        consultationName = ep.consultationName,
                        projectGeneralName = ep.projectGeneralName,
                        startDate = ep.startDate,
                        startTime = ep.startTime,
                        startDateHijri = ep.startDateHijri,
                        assignedUsers = ep.assignedUsers,
                        hearingNumber = ep.hearingNumber,
                        courtName = ep.courtName,
                    )
                },
            )
        })
    }

    override suspend fun getCalendarItemTypes(): Result<List<CalendarItemType>> = safeApiCall {
        val url = "${baseUrl()}/api/Enums/GetCalendarItemTypes"
        val response = calendarApi.getCalendarItemTypes(url)
        Result.Success(response.map { CalendarItemType(id = it.id ?: 0, name = it.name ?: "") })
    }

    override suspend fun getEmployees(): Result<List<FilterOption>> = safeApiCall {
        val url = "${baseUrl()}/api/Employees/List"
        val response = sessionsApi.getEmployees(url)
        if (response.isSuccess == true) {
            Result.Success(response.data?.map {
                FilterOption(id = it.id ?: "", name = it.name ?: "")
            } ?: emptyList())
        } else Result.Error(response.errorList?.firstOrNull() ?: "Failed")
    }
}

class NotificationsRepositoryImpl @Inject constructor(
    private val prefs: AppPreferences,
    private val api: NotificationsApiService,
) : NotificationsRepository {

    private suspend fun baseUrl(): String =
        "${prefs.getBaseUrlOnce()}${prefs.getAppUrlOnce()}"

    override suspend fun getNotifications(
        page: Int,
        pageSize: Int,
    ): Result<NotificationsPage> = runCatching {
        val url = "${baseUrl()}/api/notification" +
                "?isMessages=false&isRead=false" +
                "&Page=$page&PageSize=$pageSize" +
                "&sortBy=createdOn&isSortAscending=false"
        val response = api.getNotifications(url)
        if (response.isSuccess == true && response.data != null) {
            val data = response.data
            Result.Success(
                NotificationsPage(
                    totalNotReadItems = data.totalNotReadItems ?: 0,
                    totalItems = data.totalItems ?: 0,
                    items = data.items?.map { dto ->
                        AppNotification(
                            id = dto.id ?: 0,
                            content = dto.content,
                            itemType = dto.itemType,
                            itemTypeName = dto.itemTypeName,
                            itemId = dto.itemId,
                            createdOnHijri = dto.createdOnHijri,
                            isRead = dto.isRead ?: false,
                            createdOn = dto.createdOn,
                            isDeleted = dto.isDeleted ?: false,
                        )
                    } ?: emptyList(),
                )
            )
        } else {
            Result.Error(response.errorList?.firstOrNull() ?: "Failed to load notifications")
        }
    }.getOrElse { Result.Error(it.message ?: "Failed to load notifications") }
}
