package william.miranda.brutuscalendar.helper

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import william.miranda.brutuscalendar.model.MonthYear

/**
 * Helper to create MonthYear from a LocalDate
 */
fun LocalDate.toMonthYear(): MonthYear {
    return MonthYear(
        year = year,
        month = month
    )
}

/**
 * Get the LocalDate for Today
 */
fun LocalDate.Companion.today(): LocalDate {
    return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
}

/**
 * Check if the DayOfWeek is Weekend
 */
fun DayOfWeek.isWeekend(): Boolean {
    return this == DayOfWeek.SATURDAY || this == DayOfWeek.SUNDAY
}

/**
 * Check if the LocalDate is weekend
 */
fun LocalDate.isWeekend(): Boolean {
    return this.dayOfWeek.isWeekend()
}

/**
 * Helper to check if the LocalDate is today
 */
fun LocalDate.isToday(): Boolean {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    return today.date == this
}

/**
 * Return if the LocalDate belongs to the passed MonthYear
 */
fun LocalDate.belongsToMonthYear(monthYear: MonthYear): Boolean {
    return this.month == monthYear.month && this.year == monthYear.year
}

/**
 * Get the List of Days of week from a given starting day
 * This makes some calculations easier
 */
internal fun getWeekDays(startingWeekDay: DayOfWeek): List<DayOfWeek> {
    val result = mutableListOf<DayOfWeek>()

    //Start with the Desired day
    var dayNumber = startingWeekDay.isoDayNumber

    while (result.size < DayOfWeek.entries.size) {
        result.add(DayOfWeek(dayNumber++))
        //If we passed the last one (which is Sunday), go back to the start
        if (dayNumber > DayOfWeek.SUNDAY.isoDayNumber) {
            dayNumber = 1
        }
    }
    return result
}

/**
 * Create a List from a LocalDate range
 */
fun ClosedRange<LocalDate>.toList(increment: DateTimeUnit.DateBased): List<LocalDate> {
    val result = mutableListOf<LocalDate>()

    val rangeStart = this.start
    val rangeEnd = this.endInclusive

    var tmp = rangeStart
    while (tmp <= rangeEnd) {
        result.add(tmp)
        tmp = tmp.plus(1, increment)
    }

    return result
}

/**
 * Calculate the offset needed for the 1st day of month
 */
internal fun calculateOffset(
    firstDayOfMonth: DayOfWeek,
    weekDays: List<DayOfWeek>
): Int {
    return weekDays.indexOf(firstDayOfMonth)
}
