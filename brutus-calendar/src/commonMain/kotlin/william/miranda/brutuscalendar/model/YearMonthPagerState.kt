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
    currentPage = firstYearMonth.monthsUntil(startingYearMonth),
    currentPageOffsetFraction = 0f
) {
    override val pageCount: Int
        get() = firstYearMonth.monthsUntil(lastYearMonth) + 1

    /**
     * Gets the Page Number from a given YearMonth
     */
    public fun getPageNumberFromYearMonth(yearMonth: YearMonth): Int = firstYearMonth.monthsUntil(yearMonth)

    /**
     * Gets the YearMonth from a given PageNumber
     */
    public fun getYearMonthFromPageNumber(pageNumber: Int): YearMonth = firstYearMonth.plus(pageNumber, DateTimeUnit.MONTH)

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
        /** To keep current page and current page offset saved */
        public val Saver: Saver<YearMonthPagerState, *> =
            listSaver(
                save = {
                    listOf(
                        it.startingYearMonth,
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
