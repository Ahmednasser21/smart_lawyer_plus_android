package com.smartfingers.smartlawyerplus.util

import com.smartfingers.smartlawyerplus.domain.model.CalendarDay
import com.smartfingers.smartlawyerplus.domain.model.CalendarMonth
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

object CalendarManager {

    private val gregorianHeaderFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("ar"))
    private val dayFormatter = DateTimeFormatter.ofPattern("d", Locale.ENGLISH)

    /**
     * Generates [count] months starting from [start], mirroring iOS CalendarManager.getMonths().
     */
    fun getMonths(start: LocalDate = LocalDate.now(), count: Int): List<CalendarMonth> {
        val months = mutableListOf<CalendarMonth>()
        var current = start.withDayOfMonth(1)
        repeat(count) {
            months.add(buildMonth(current))
            current = current.plusMonths(1)
        }
        return months
    }

    private fun buildMonth(firstOfMonth: LocalDate): CalendarMonth {
        val yearMonth = YearMonth.from(firstOfMonth)
        val days = generateDaysForMonth(yearMonth)
        val title = firstOfMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("ar")))
        return CalendarMonth(
            days = days,
            monthTitle = title,
            date = firstOfMonth,
        )
    }

    /**
     * Mirrors iOS generateDaysInMonth — fills leading empty days so the grid always
     * starts on Sunday (weekday 1 in iOS Calendar).
     * Java DayOfWeek: MONDAY=1 … SUNDAY=7. We map to Sunday-first grid:
     * Sunday→0, Monday→1 … Saturday→6
     */
    private fun generateDaysForMonth(yearMonth: YearMonth): List<CalendarDay> {
        val firstDay = yearMonth.atDay(1)
        // Convert to Sunday-first offset (0=Sun,1=Mon,...,6=Sat)
        val firstDayOfWeek = (firstDay.dayOfWeek.value % 7) // Sun=0, Mon=1 … Sat=6
        val today = LocalDate.now()

        val days = mutableListOf<CalendarDay>()

        // Leading days from previous month
        val prevMonth = yearMonth.minusMonths(1)
        val daysInPrevMonth = prevMonth.lengthOfMonth()
        for (i in firstDayOfWeek - 1 downTo 0) {
            val date = prevMonth.atDay(daysInPrevMonth - i)
            days.add(
                CalendarDay(
                    date = date,
                    number = date.dayOfMonth.toString(),
                    isWithinDisplayedMonth = false,
                    isSelected = date == today,
                )
            )
        }

        // Current month days
        for (d in 1..yearMonth.lengthOfMonth()) {
            val date = yearMonth.atDay(d)
            days.add(
                CalendarDay(
                    date = date,
                    number = d.toString(),
                    isWithinDisplayedMonth = true,
                    isSelected = date == today,
                )
            )
        }

        // Trailing days from next month to complete the last row
        val remaining = (7 - (days.size % 7)) % 7
        val nextMonth = yearMonth.plusMonths(1)
        for (d in 1..remaining) {
            val date = nextMonth.atDay(d)
            days.add(
                CalendarDay(
                    date = date,
                    number = d.toString(),
                    isWithinDisplayedMonth = false,
                    isSelected = false,
                )
            )
        }

        return days
    }

    fun formatDateForApi(date: LocalDate): String =
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}
