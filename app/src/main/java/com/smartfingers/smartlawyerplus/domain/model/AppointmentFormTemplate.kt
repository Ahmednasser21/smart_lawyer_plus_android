package com.smartfingers.smartlawyerplus.domain.model

data class AppointmentFormTemplate(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String = "",
    val body: String = "",
    val printSettingsTemplateId: String? = null,
)