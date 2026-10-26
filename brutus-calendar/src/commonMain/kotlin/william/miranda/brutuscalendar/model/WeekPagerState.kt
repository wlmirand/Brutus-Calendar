package william.miranda.brutuscalendar.model

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.YearMonth
import kotlinx.datetime.until
import william.miranda.brutuscalendar.helper.today

public class WeekPagerState(
    public val startingDate: LocalDate = LocalDate.today(),
    public val firstDate: LocalDate = YearMonth(year = 0, month = Month.JANUARY).firstDay,
    public val lastDate: LocalDate = YearMonth(year = 9999, month = Month.DECEMBER).lastDay
) : PagerState(
    currentPage = firstDate.until(startingDate, DateTimeUnit.WEEK).toInt(),
    currentPageOffsetFraction = 0f
) {
    override val pageCount: Int
        get() = firstDate.until(lastDate, DateTimeUnit.WEEK).toInt()

    /**
     * Gets the Page Number for a given YearMonth
     */
    public fun getPageNumberFromDate(date: LocalDate): Int = firstDate.until(date, DateTimeUnit.WEEK).toInt()

    /**
     * Convenience to Scroll to YearMonth
     */
    public suspend fun scrollToDate(date: LocalDate): Unit = scrollToPage(getPageNumberFromDate(date))
    public suspend fun animateScrollToDate(date: LocalDate): Unit = animateScrollToPage(getPageNumberFromDate(date))

    public companion object {
        /** To keep current page and current page offset saved */
        public val Saver: Saver<WeekPagerState, *> =
            listSaver(
                save = {
                    listOf(
                        it.startingDate,
                        it.firstDate,
                        it.lastDate,
                    )
                },
                restore = {
                    WeekPagerState(
                        it[0],
                        it[1],
                        it[2],
                    )
                },
            )
    }
}
