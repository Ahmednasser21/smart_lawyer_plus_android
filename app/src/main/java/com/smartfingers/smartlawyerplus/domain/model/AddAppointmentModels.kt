package com.smartfingers.smartlawyerplus.domain.model

data class AddAppointmentRequest(
    val typeId: String?,
    val assignedUserIds: String?,
    val partiesIds: String?,
    val startDate: String?,
    val startTime: String?,
    val subject: String?,
    val caseId: String?,
    val consultationId: String?,
    val executiveCaseId: String?,
    val projectGeneralId: String?,
    val clientRequestId: String?,
    val appointmentFormTemplate: List<AppointmentFormTemplate>? = null,
)

data class Party(val id: Int, val name: String)