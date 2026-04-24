package com.smartfingers.smartlawyerplus.util

import com.smartfingers.smartlawyerplus.domain.model.CalendarDay
import com.smartfingers.smartlawyerplus.domain.model.CalendarMonth
import java.time.LocalDate
import java.time.YearMonth
import java.time.chrono.HijrahChronology
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.util.Locale

object CalendarManager {

    fun getMonths(
        start: LocalDate = LocalDate.now(),
        count: Int,
        isHijri: Boolean = false,
    ): List<CalendarMonth> = if (isHijri) getHijriMonths(start, count) else getGregorianMonths(start, count)

    // ─────────────────────────────── Gregorian ────────────────────────────────

    private fun getGregorianMonths(start: LocalDate, count: Int): List<CalendarMonth> {
        val months = mutableListOf<CalendarMonth>()
        var current = start.withDayOfMonth(1)
        repeat(count) {
            months.add(buildGregorianMonth(current))
            current = current.plusMonths(1)
        }
        return months
    }

    private fun buildGregorianMonth(firstOfMonth: LocalDate): CalendarMonth {
        val yearMonth = YearMonth.from(firstOfMonth)
        val title = firstOfMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("ar")))
        return CalendarMonth(days = generateGregorianDays(yearMonth), monthTitle = title, date = firstOfMonth)
    }

    private fun generateGregorianDays(yearMonth: YearMonth): List<CalendarDay> {
        val firstDay = yearMonth.atDay(1)
        val today = LocalDate.now()
        // How many leading cells before day 1 (column 0 = Saturday)
        val firstDayColumn = satFirstCol(firstDay.dayOfWeek.value)
        val days = mutableListOf<CalendarDay>()

        // Leading: previous month tail
        if (firstDayColumn > 0) {
            val prev = yearMonth.minusMonths(1)
            val daysInPrev = prev.lengthOfMonth()
            // We need `firstDayColumn` days from the end of the previous month
            for (i in firstDayColumn - 1 downTo 0) {
                val date = prev.atDay(daysInPrev - i)
                days += CalendarDay(date, date.dayOfMonth.toString(), isWithinDisplayedMonth = false, isSelected = false)
            }
        }

        // Current month
        for (d in 1..yearMonth.lengthOfMonth()) {
            val date = yearMonth.atDay(d)
            days += CalendarDay(date, d.toString(), isWithinDisplayedMonth = true, isSelected = date == today)
        }

        // Trailing: next month head
        val rem = days.size % 7
        if (rem != 0) {
            val next = yearMonth.plusMonths(1)
            repeat(7 - rem) { i ->
                val date = next.atDay(i + 1)
                days += CalendarDay(date, (i + 1).toString(), isWithinDisplayedMonth = false, isSelected = false)
            }
        }
        return days
    }

    // ──────────────────────────────── Hijri ───────────────────────────────────

    private fun getHijriMonths(start: LocalDate, count: Int): List<CalendarMonth> {
        val months = mutableListOf<CalendarMonth>()
        val hijriStart = HijrahDate.from(start)
        var hYear  = hijriStart.get(ChronoField.YEAR)
        var hMonth = hijriStart.get(ChronoField.MONTH_OF_YEAR)
        repeat(count) {
            months.add(buildHijriMonth(hYear, hMonth))
            hMonth++
            if (hMonth > 12) { hMonth = 1; hYear++ }
        }
        return months
    }

    private fun buildHijriMonth(hYear: Int, hMonth: Int): CalendarMonth {
        val firstHijri = HijrahDate.of(hYear, hMonth, 1)
        val gregorianDate = LocalDate.from(HijrahChronology.INSTANCE.date(firstHijri))
        val title = "${arabicHijriMonth(hMonth)} $hYear هـ"
        return CalendarMonth(days = generateHijriDays(hYear, hMonth), monthTitle = title, date = gregorianDate)
    }

    private fun generateHijriDays(hYear: Int, hMonth: Int): List<CalendarDay> {
        val firstHijri   = HijrahDate.of(hYear, hMonth, 1)
        val daysInMonth  = firstHijri.lengthOfMonth()
        val today        = LocalDate.now()
        val todayHijri   = HijrahDate.from(today)
        val firstGreg    = LocalDate.from(HijrahChronology.INSTANCE.date(firstHijri))
        val firstCol     = satFirstCol(firstGreg.dayOfWeek.value)
        val days         = mutableListOf<CalendarDay>()

        // Leading
        if (firstCol > 0) {
            var pm = hMonth - 1; var py = hYear
            if (pm < 1) { pm = 12; py-- }
            val prevLen = HijrahDate.of(py, pm, 1).lengthOfMonth()
            // We need `firstCol` days from end of previous Hijri month
            for (i in firstCol - 1 downTo 0) {
                val dn = prevLen - i
                val gDate = LocalDate.from(HijrahChronology.INSTANCE.date(HijrahDate.of(py, pm, dn)))
                days += CalendarDay(gDate, dn.toString(), isWithinDisplayedMonth = false, isSelected = false)
            }
        }

        // Current
        for (d in 1..daysInMonth) {
            val hd    = HijrahDate.of(hYear, hMonth, d)
            val gDate = LocalDate.from(HijrahChronology.INSTANCE.date(hd))
            days += CalendarDay(gDate, d.toString(), isWithinDisplayedMonth = true, isSelected = hd == todayHijri)
        }

        // Trailing
        val rem = days.size % 7
        if (rem != 0) {
            var nm = hMonth + 1; var ny = hYear
            if (nm > 12) { nm = 1; ny++ }
            repeat(7 - rem) { i ->
                val dn    = i + 1
                val gDate = LocalDate.from(HijrahChronology.INSTANCE.date(HijrahDate.of(ny, nm, dn)))
                days += CalendarDay(gDate, dn.toString(), isWithinDisplayedMonth = false, isSelected = false)
            }
        }
        return days
    }

    // ─────────────────────────────── Helpers ─────────────────────────────────

    // Grid columns: 0=Sat, 1=Fri, 2=Thu, 3=Wed, 4=Tue, 5=Mon, 6=Sun
    // Java DayOfWeek: Mon=1, Tue=2, Wed=3, Thu=4, Fri=5, Sat=6, Sun=7
    private fun satFirstCol(dayOfWeekValue: Int): Int = when (dayOfWeekValue) {
        7 -> 0 // Sunday    → column 0 (leftmost)
        1 -> 1 // Monday    → column 1
        2 -> 2 // Tuesday   → column 2
        3 -> 3 // Wednesday → column 3
        4 -> 4 // Thursday  → column 4
        5 -> 5 // Friday    → column 5
        6 -> 6 // Saturday  → column 6 (rightmost)
        else -> 0
    }

    private fun arabicHijriMonth(m: Int): String = listOf(
        "محرم", "صفر", "ربيع الأول", "ربيع الثاني",
        "جمادى الأولى", "جمادى الآخرة", "رجب", "شعبان",
        "رمضان", "شوال", "ذو القعدة", "ذو الحجة"
    ).getOrElse(m - 1) { "" }

    fun formatDateForApi(date: LocalDate): String =
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}