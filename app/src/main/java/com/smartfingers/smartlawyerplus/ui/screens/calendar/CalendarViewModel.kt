package com.smartfingers.smartlawyerplus.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfingers.smartlawyerplus.domain.model.CalendarDay
import com.smartfingers.smartlawyerplus.domain.model.CalendarItemType
import com.smartfingers.smartlawyerplus.domain.model.DayEvent
import com.smartfingers.smartlawyerplus.domain.model.FilterOption
import com.smartfingers.smartlawyerplus.domain.model.Result
import com.smartfingers.smartlawyerplus.domain.usecase.calendar.GetCalendarDataUseCase
import com.smartfingers.smartlawyerplus.domain.usecase.calendar.GetCalendarEmployeesUseCase
import com.smartfingers.smartlawyerplus.domain.usecase.calendar.GetCalendarItemTypesUseCase
import com.smartfingers.smartlawyerplus.util.CalendarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getCalendarDataUseCase: GetCalendarDataUseCase,
    private val getCalendarItemTypesUseCase: GetCalendarItemTypesUseCase,
    private val getCalendarEmployeesUseCase: GetCalendarEmployeesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState

    init {
        val months = CalendarManager.getMonths(LocalDate.now(), count = 12)
        _uiState.update { it.copy(months = months) }
        loadEventsForMonth(0)
        loadDropdownData()
    }

    // ── Month navigation ──────────────────────────────────────────────────────

    fun onMonthChanged(index: Int) {
        _uiState.update { it.copy(currentMonthIndex = index, selectedDayEvents = emptyList()) }
        if (!_uiState.value.months[index].isLoaded) {
            loadEventsForMonth(index)
        }
    }

    // ── Day selection ─────────────────────────────────────────────────────────

    fun onDaySelected(day: CalendarDay) {
        val months = _uiState.value.months.toMutableList()
        val monthIndex = _uiState.value.currentMonthIndex
        val currentMonth = months[monthIndex]

        // Deselect all days across all months, then select the tapped one
        val updatedMonths = months.mapIndexed { mi, month ->
            month.copy(
                days = month.days.map { d ->
                    d.copy(isSelected = mi == monthIndex && d.date == day.date)
                }
            )
        }

        val events = updatedMonths[monthIndex].days
            .firstOrNull { it.date == day.date }?.events ?: emptyList()

        _uiState.update {
            it.copy(months = updatedMonths, selectedDayEvents = events)
        }
    }

    // ── Filter selections ─────────────────────────────────────────────────────

    fun onEmployeeSelected(employee: FilterOption?) {
        _uiState.update { it.copy(selectedEmployee = employee) }
        reloadAllMonths()
    }

    fun onItemTypeSelected(type: CalendarItemType?) {
        _uiState.update { it.copy(selectedItemType = type) }
        reloadAllMonths()
    }

    // ── Load events for a specific month ─────────────────────────────────────

    private fun loadEventsForMonth(monthIndex: Int) {
        val months = _uiState.value.months
        if (monthIndex >= months.size) return
        val month = months[monthIndex]
        val firstDay = month.days.firstOrNull()?.date ?: return
        val lastDay = month.days.lastOrNull()?.date ?: return

        val userId = _uiState.value.selectedEmployee?.id
        val typeId = _uiState.value.selectedItemType?.id?.toString()

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingEvents = true) }

            val result = getCalendarDataUseCase(
                startDate = CalendarManager.formatDateForApi(firstDay),
                endDate = CalendarManager.formatDateForApi(lastDay),
                userId = userId,
                typeId = typeId,
            )

            when (result) {
                is Result.Success -> {
                    val events = result.data
                    val updatedMonths = _uiState.value.months.toMutableList()
                    val updatedDays = updatedMonths[monthIndex].days.map { day ->
                        val dayEvents = events.filter { event ->
                            isEventOnDay(event.start, event.end, day.date)
                        }.map { event ->
                            DayEvent(event = event, eventType = event.getEventType())
                        }
                        day.copy(events = dayEvents)
                    }
                    updatedMonths[monthIndex] = updatedMonths[monthIndex].copy(
                        days = updatedDays,
                        isLoaded = true,
                    )
                    _uiState.update { state ->
                        val selectedDay = updatedMonths[monthIndex].days.firstOrNull { it.isSelected }
                        state.copy(
                            months = updatedMonths,
                            isLoadingEvents = false,
                            selectedDayEvents = selectedDay?.events ?: state.selectedDayEvents,
                        )
                    }
                }
                is Result.Error -> _uiState.update {
                    it.copy(isLoadingEvents = false, error = result.message)
                }
                else -> _uiState.update { it.copy(isLoadingEvents = false) }
            }
        }
    }

    private fun reloadAllMonths() {
        val months = _uiState.value.months.map { it.copy(isLoaded = false) }
        _uiState.update { it.copy(months = months) }
        loadEventsForMonth(_uiState.value.currentMonthIndex)
    }

    // ── Dropdown data ─────────────────────────────────────────────────────────

    private fun loadDropdownData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingEmployees = true) }
            when (val result = getCalendarEmployeesUseCase()) {
                is Result.Success -> _uiState.update {
                    it.copy(employees = result.data, isLoadingEmployees = false)
                }
                else -> _uiState.update { it.copy(isLoadingEmployees = false) }
            }
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingItemTypes = true) }
            when (val result = getCalendarItemTypesUseCase()) {
                is Result.Success -> _uiState.update {
                    it.copy(itemTypes = result.data, isLoadingItemTypes = false)
                }
                else -> _uiState.update { it.copy(isLoadingItemTypes = false) }
            }
        }
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private fun isEventOnDay(start: String?, end: String?, day: LocalDate): Boolean {
        val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val fmtDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return try {
            val startDate = start?.let {
                runCatching { LocalDate.parse(it.take(10), fmtDate) }.getOrNull()
            } ?: return false
            val endDate = end?.let {
                runCatching { LocalDate.parse(it.take(10), fmtDate) }.getOrNull()
            }
            if (endDate != null) {
                !day.isBefore(startDate) && !day.isAfter(endDate)
            } else {
                day == startDate
            }
        } catch (_: Exception) {
            false
        }
    }
}
