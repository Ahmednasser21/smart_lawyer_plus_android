package com.smartfingers.smartlawyerplus.data.repository

import com.smartfingers.smartlawyerplus.data.local.AppPreferences
import com.smartfingers.smartlawyerplus.data.remote.api.AppointmentsApiService
import com.smartfingers.smartlawyerplus.domain.model.AppointmentAttachment
import com.smartfingers.smartlawyerplus.domain.model.AppointmentDetails
import com.smartfingers.smartlawyerplus.domain.model.AppointmentListItem
import com.smartfingers.smartlawyerplus.domain.model.AppointmentsFilter
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.repository.AppointmentsRepository
import javax.inject.Inject

class AppointmentsRepositoryImpl @Inject constructor(
    private val prefs: AppPreferences,
    private val api: AppointmentsApiService,
) : AppointmentsRepository {

    private suspend fun baseUrl(): String =
        "${prefs.getBaseUrlOnce()}${prefs.getAppUrlOnce()}"

    override suspend fun getAppointments(
        filter: AppointmentsFilter,
    ): Result<Pair<List<AppointmentListItem>, Int>> = runCatching {
        val url = "${baseUrl()}/api/Appointments" +
                "?sortBy=startTime&isSortAscending=true" +
                "&page=${filter.page}&pageSize=${filter.pageSize}" +
                "&searchText=&showFilters=false" +
                "&isFinished=${filter.isFinished}"
        val response = api.getAppointments(url)
        if (response.isSuccess == true) {
            val items = response.data?.items?.map { dto ->
                AppointmentListItem(
                    id = dto.id ?: 0,
                    typeName = dto.typeName,
                    startDate = dto.startDate,
                    startDateHijri = dto.startDateHijri,
                    startTime = dto.startTime,
                    createdByUser = dto.createdByUser,
                    assignedUsers = dto.assignedUsers ?: emptyList(),
                    parties = dto.parties ?: emptyList(),
                    subject = dto.subject,
                    caseName = dto.caseName,
                    remainingDays = dto.remainingTime?.days ?: 0,
                    remainingHours = dto.remainingTime?.hours ?: 0,
                    isFinished = dto.isFinished ?: false,
                )
            } ?: emptyList()
            Result.Success(items to (response.data?.totalItems ?: 0))
        } else {
            Result.Error(response.errorList?.firstOrNull() ?: "فشل تحميل المواعيد")
        }
    }.getOrElse { Result.Error(it.message ?: "خطأ في الاتصال") }

    override suspend fun getAppointmentDetails(appointmentId: Int): Result<AppointmentDetails> =
        runCatching {
            val url = "${baseUrl()}/api/Appointments/details/$appointmentId"
            val response = api.getAppointmentDetails(url)
            if (response.isSuccess == true && response.data != null) {
                val d = response.data
                Result.Success(
                    AppointmentDetails(
                        id = d.id ?: appointmentId,
                        typeName = d.typeName,
                        startDate = d.startDate,
                        startDateHijri = d.startDateHijri,
                        startTime = d.startTime,
                        createdByUser = d.createdByUser,
                        assignedUsers = d.assignedUsers ?: emptyList(),
                        parties = d.parties ?: emptyList(),
                        subject = d.subject,
                        caseName = d.caseName,
                        remainingDays = d.remainingTime?.days ?: 0,
                        remainingHours = d.remainingTime?.hours ?: 0,
                        isFinished = d.isFinished ?: false,
                        attachments = d.attachments?.map { att ->
                            AppointmentAttachment(
                                id = att.id ?: 0,
                                name = att.name,
                                path = att.path,
                                createdOn = att.createdOn,
                                createdBy = att.createdBy,
                                isApproved = att.isApproved ?: false,
                            )
                        } ?: emptyList(),
                        createdOn = d.createdOn,
                        updatedOn = d.updatedOn,
                        clientRequest = d.clientRequest,
                        executiveCase = d.executiveCase,
                        consultation = d.consultation,
                        projectGeneral = d.projectGeneral,
                    )
                )
            } else {
                Result.Error(response.errorList?.firstOrNull() ?: "فشل تحميل تفاصيل الموعد")
            }
        }.getOrElse { Result.Error(it.message ?: "خطأ في الاتصال") }
}