package william.miranda.brutuscalendar.model

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.YearMonth
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.until
import william.miranda.brutuscalendar.helper.NUM_DAYS_WEEK
import william.miranda.brutuscalendar.helper.today

public class WeekPagerState(
    public val startingDate: LocalDate = LocalDate.today(),
    public val startingDayOfWeek: DayOfWeek,
    public val firstDate: LocalDate = YearMonth(year = 0, month = Month.JANUARY).firstDay,
    public val lastDate: LocalDate = YearMonth(year = 9999, month = Month.DECEMBER).lastDay
) : PagerState(
    currentPage = initialWeekPage(firstDate, lastDate, startingDate, startingDayOfWeek),
    currentPageOffsetFraction = 0f
) {
    private val firstWeekStart: LocalDate = firstDate.startOfWeek(startingDayOfWeek)

    override val pageCount: Int =
        (firstWeekStart.until(lastDate, DateTimeUnit.DAY) / NUM_DAYS_WEEK + 1).toInt()

    /**
     * Gets the Page Number for a given Date
     */
    public fun getPageNumberFromDate(date: LocalDate): Int {
        require(date in firstDate..lastDate) { "date must be within firstDate..lastDate" }
        return (firstWeekStart.until(date, DateTimeUnit.DAY) / NUM_DAYS_WEEK).toInt()
    }

    /**
     * Gets the first weekday of a page. The first page may start before [firstDate].
     */
    public fun getFirstDateFromPageNumber(pageNumber: Int): LocalDate {
        require(pageNumber in 0 until pageCount) { "pageNumber must be within 0 until pageCount" }
        return firstWeekStart.plus(pageNumber, DateTimeUnit.WEEK)
    }

    /**
     * Convenience to scroll to the week containing a date within the configured interval.
     */
    public suspend fun scrollToDate(date: LocalDate): Unit = scrollToPage(getPageNumberFromDate(date))
    public suspend fun animateScrollToDate(date: LocalDate): Unit = animateScrollToPage(getPageNumberFromDate(date))

    /**
     * Convenience for Previous and Next Navigation
     */
    public suspend fun scrollToPreviousPage(): Unit = scrollToPage(currentPage - 1)
    public suspend fun animateScrollToPreviousPage(): Unit = animateScrollToPage(currentPage - 1)

    public suspend fun scrollToNextPage(): Unit = scrollToPage(currentPage + 1)
    public suspend fun animateScrollToNextPage(): Unit = animateScrollToPage(currentPage + 1)

    public companion object {
        /** Saves the initial date, first weekday, and bounds; does not save the current scroll position. */
        public val Saver: Saver<WeekPagerState, Any> =
            listSaver(
                save = {
                    listOf(
                        it.startingDate,
                        it.startingDayOfWeek,
                        it.firstDate,
                        it.lastDate,
                    )
                },
                restore = {
                    WeekPagerState(
                        it[0] as LocalDate,
                        it[1] as DayOfWeek,
                        it[2] as LocalDate,
                        it[3] as LocalDate,
                    )
                },
            )
    }
}

private fun LocalDate.startOfWeek(startingDayOfWeek: DayOfWeek): LocalDate =
    minus((dayOfWeek.isoDayNumber - startingDayOfWeek.isoDayNumber).mod(NUM_DAYS_WEEK), DateTimeUnit.DAY)

private fun initialWeekPage(
    firstDate: LocalDate,
    lastDate: LocalDate,
    startingDate: LocalDate,
    startingDayOfWeek: DayOfWeek
): Int {
    require(firstDate <= lastDate) { "firstDate must not be after lastDate" }
    require(startingDate in firstDate..lastDate) { "startingDate must be within firstDate..lastDate" }
    val firstWeekStart = firstDate.startOfWeek(startingDayOfWeek)
    val count = firstWeekStart.until(lastDate, DateTimeUnit.DAY) / NUM_DAYS_WEEK + 1
    require(count <= Int.MAX_VALUE.toLong()) { "The date interval contains too many pages" }
    return (firstWeekStart.until(startingDate, DateTimeUnit.DAY) / NUM_DAYS_WEEK).toInt()
}
