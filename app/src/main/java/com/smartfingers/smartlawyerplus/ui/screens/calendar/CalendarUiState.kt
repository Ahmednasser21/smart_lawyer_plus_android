package com.smartfingers.smartlawyerplus.ui.screens.calendar

import com.smartfingers.smartlawyerplus.domain.model.CalendarItemType
import com.smartfingers.smartlawyerplus.domain.model.CalendarMonth
import com.smartfingers.smartlawyerplus.domain.model.DayEvent
import com.smartfingers.smartlawyerplus.domain.model.FilterOption

data class CalendarUiState(
    // Months
    val months: List<CalendarMonth> = emptyList(),
    val currentMonthIndex: Int = 0,

    // Calendar type
    val isHijri: Boolean = false,

    // Selected day events
    val selectedDayEvents: List<DayEvent> = emptyList(),

    // Filter dropdowns
    val employees: List<FilterOption> = emptyList(),
    val itemTypes: List<CalendarItemType> = emptyList(),
    val selectedEmployee: FilterOption? = null,
    val selectedItemType: CalendarItemType? = null,
    val isLoadingEmployees: Boolean = false,
    val isLoadingItemTypes: Boolean = false,

    // Loading
    val isLoadingEvents: Boolean = false,

    // User info
    val userName: String = "",
    val userPicture: String = "",

    val error: String = "",
)