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
import kotlinx.datetime.plus
import kotlinx.datetime.until
import william.miranda.brutuscalendar.helper.NUM_DAYS_WEEK
import william.miranda.brutuscalendar.helper.today
import kotlin.math.ceil

public class WeekPagerState(
    public val startingDate: LocalDate = LocalDate.today(),
    public val startingDayOfWeek: DayOfWeek,
    public val firstDate: LocalDate = YearMonth(year = 0, month = Month.JANUARY).firstDay,
    public val lastDate: LocalDate = YearMonth(year = 9999, month = Month.DECEMBER).lastDay
) : PagerState(
    currentPage = firstDate.until(startingDate, DateTimeUnit.WEEK).toInt(),
    currentPageOffsetFraction = 0f
) {
    override val pageCount: Int
        get() {
            val offset = (firstDate.dayOfWeek.isoDayNumber - startingDayOfWeek.isoDayNumber).mod(NUM_DAYS_WEEK)
            val numOfDays = firstDate.until(lastDate, DateTimeUnit.DAY).plus(1).plus(offset).toFloat()
            val numOfWeeks = numOfDays / NUM_DAYS_WEEK
            return ceil(numOfWeeks).toInt()
        }

    /**
     * Gets the Page Number for a given Date
     */
    public fun getPageNumberFromDate(date: LocalDate): Int = firstDate.until(date, DateTimeUnit.WEEK).toInt()

    /**
     * Gets the First Date of Week from a given PageNumber
     */
    public fun getFirstDateFromPageNumber(pageNumber: Int): LocalDate = firstDate.plus(pageNumber, DateTimeUnit.WEEK)

    /**
     * Convenience to Scroll to YearMonth
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
        /** To keep current page and current page offset saved */
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
