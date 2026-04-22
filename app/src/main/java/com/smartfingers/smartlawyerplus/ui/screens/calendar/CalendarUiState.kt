package com.smartfingers.smartlawyerplus.ui.screens.calendar

import com.smartfingers.smartlawyerplus.domain.model.CalendarItemType
import com.smartfingers.smartlawyerplus.domain.model.CalendarMonth
import com.smartfingers.smartlawyerplus.domain.model.DayEvent
import com.smartfingers.smartlawyerplus.domain.model.FilterOption

data class CalendarUiState(
    // Months
    val months: List<CalendarMonth> = emptyList(),
    val currentMonthIndex: Int = 0,

    // Selected day events (shown in list below calendar)
    val selectedDayEvents: List<DayEvent> = emptyList(),

    // Filter dropdowns
    val employees: List<FilterOption> = emptyList(),
    val itemTypes: List<CalendarItemType> = emptyList(),
    val selectedEmployee: FilterOption? = null,
    val selectedItemType: CalendarItemType? = null,
    val isLoadingEmployees: Boolean = false,
    val isLoadingItemTypes: Boolean = false,

    // Loading state per month
    val isLoadingEvents: Boolean = false,

    val error: String = "",
)
