package william.miranda.brutuscalendar.helper

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Get the LocalDateTime for Now
 */
public fun LocalDateTime.Companion.now(
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): LocalDateTime = Clock.System.now().toLocalDateTime(timeZone = timeZone)

/**
 * Get the LocalDate for Today
 */
public fun LocalDate.Companion.today(
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): LocalDate = Clock.System.now().toLocalDateTime(timeZone = timeZone).date

/**
 * Create LocalDateTime from Timestamp
 */
public fun LocalDateTime.Companion.fromMillis(
    millis: Long,
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): LocalDateTime {
    val instant = Instant.fromEpochMilliseconds(millis)
    return instant.toLocalDateTime(timeZone = timeZone)
}

/**
 * Create LocalDate from Timestamp
 */
public fun LocalDate.Companion.fromMillis(
    millis: Long,
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): LocalDate {
    val instant = Instant.fromEpochMilliseconds(millis)
    return instant.toLocalDateTime(timeZone = timeZone).date
}

/**
 * Create Timestamp from LocalDateTime
 */
public fun LocalDateTime.toMillis(
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): Long = this.toInstant(timeZone = timeZone).toEpochMilliseconds()

/**
 * Create Timestamp from LocalDate
 */
public fun LocalDate.toMillis(
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): Long = this.atStartOfDayIn(timeZone = timeZone).toEpochMilliseconds()

/**
 * Check if the DayOfWeek is Weekend
 */
public fun DayOfWeek.isWeekend(): Boolean = this == DayOfWeek.SATURDAY || this == DayOfWeek.SUNDAY

/**
 * Check if the LocalDate is weekend
 */
public fun LocalDate.isWeekend(): Boolean = this.dayOfWeek.isWeekend()

/**
 * Helper to check if the LocalDate is today
 */
public fun LocalDate.isToday(
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): Boolean {
    val today = Clock.System.now().toLocalDateTime(timeZone = timeZone)
    return today.date == this
}

/**
 * Return if the LocalDate belongs to the passed MonthYear
 */
public fun LocalDate.belongsToYearMonth(yearMonth: YearMonth): Boolean =
    this.month == yearMonth.month && this.year == yearMonth.year

/**
 * Create a List from a LocalDate range
 */
public fun ClosedRange<LocalDate>.toList(increment: DateTimeUnit.DateBased): List<LocalDate> {
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
