package william.miranda.brutuscalendar.model

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.YearMonth
import kotlinx.datetime.monthsUntil
import kotlinx.datetime.plus
import kotlinx.datetime.yearMonth
import william.miranda.brutuscalendar.helper.today

public class YearMonthPagerState(
    public val startingYearMonth: YearMonth = LocalDate.today().yearMonth,
    public val firstYearMonth: YearMonth = YearMonth(year = 0, month = Month.JANUARY),
    public val lastYearMonth: YearMonth = YearMonth(year = 9999, month = Month.DECEMBER)
) : PagerState(
    currentPage = initialMonthPage(firstYearMonth, lastYearMonth, startingYearMonth),
    currentPageOffsetFraction = 0f
) {
    override val pageCount: Int = monthPageCount(firstYearMonth, lastYearMonth).toInt()

    /**
     * Gets the Page Number from a given YearMonth
     */
    public fun getPageNumberFromYearMonth(yearMonth: YearMonth): Int {
        require(yearMonth in firstYearMonth..lastYearMonth) {
            "yearMonth must be within firstYearMonth..lastYearMonth"
        }
        return firstYearMonth.monthsUntil(yearMonth)
    }

    /**
     * Gets the YearMonth from a given PageNumber
     */
    public fun getYearMonthFromPageNumber(pageNumber: Int): YearMonth {
        require(pageNumber in 0 until pageCount) { "pageNumber must be within 0 until pageCount" }
        return firstYearMonth.plus(pageNumber, DateTimeUnit.MONTH)
    }

    /**
     * Convenience to Scroll to YearMonth
     */
    public suspend fun scrollToYearMonth(yearMonth: YearMonth): Unit = scrollToPage(getPageNumberFromYearMonth(yearMonth))
    public suspend fun animateScrollToYearMonth(yearMonth: YearMonth): Unit = animateScrollToPage(getPageNumberFromYearMonth(yearMonth))

    /**
     * Convenience for Previous and Next Navigation
     */
    public suspend fun scrollToPreviousPage(): Unit = scrollToPage(currentPage - 1)
    public suspend fun animateScrollToPreviousPage(): Unit = animateScrollToPage(currentPage - 1)

    public suspend fun scrollToNextPage(): Unit = scrollToPage(currentPage + 1)
    public suspend fun animateScrollToNextPage(): Unit = animateScrollToPage(currentPage + 1)

    public companion object {
        /** Saves the settled month and bounds; does not save the fractional scroll offset. */
        public val Saver: Saver<YearMonthPagerState, *> =
            listSaver(
                save = {
                    listOf(
                        it.getYearMonthFromPageNumber(it.settledPage),
                        it.firstYearMonth,
                        it.lastYearMonth,
                    )
                },
                restore = {
                    YearMonthPagerState(
                        it[0],
                        it[1],
                        it[2],
                    )
                },
            )
    }
}

private fun monthPageCount(first: YearMonth, last: YearMonth): Long =
    (last.year.toLong() - first.year.toLong()) * 12 + last.month.ordinal - first.month.ordinal + 1

private fun initialMonthPage(first: YearMonth, last: YearMonth, starting: YearMonth): Int {
    require(first <= last) { "firstYearMonth must not be after lastYearMonth" }
    require(starting in first..last) { "startingYearMonth must be within firstYearMonth..lastYearMonth" }
    require(monthPageCount(first, last) <= Int.MAX_VALUE.toLong()) { "The month interval contains too many pages" }
    return first.monthsUntil(starting)
}
