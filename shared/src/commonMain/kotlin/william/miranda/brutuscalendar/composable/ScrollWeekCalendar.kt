package william.miranda.brutuscalendar.composable

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.plus
import kotlinx.datetime.until
import william.miranda.brutuscalendar.helper.DefaultDayRenderer
import william.miranda.brutuscalendar.helper.DefaultHeaderRenderer
import william.miranda.brutuscalendar.helper.DefaultWeekDayRenderer
import william.miranda.brutuscalendar.model.DayRendererModel
import william.miranda.brutuscalendar.model.MonthYear

/**
 * Pager that inflates Week Calendars for each Week
 * We use a LocalDate as a reference to show the Week
 */
@Composable
fun ScrollWeekCalendar(
    day: LocalDate,
    startDate: LocalDate = LocalDate(0, Month.JANUARY, 1),
    endDate: LocalDate = LocalDate(9999, Month.DECEMBER, 31),
    displayHeader: Boolean = true,
    displayWeekDaysName: Boolean = true,
    startingWeekDay: DayOfWeek = DayOfWeek.SUNDAY,
    displayExtraDays: Boolean = false,
    dayOnClick: ((LocalDate) -> Unit)? = null,
    dayRenderer: @Composable (DayRendererModel) -> Unit = { DefaultDayRenderer(it, dayOnClick) },
    headerRenderer: @Composable (MonthYear) -> Unit = { DefaultHeaderRenderer(it) },
    weekDayRenderer: @Composable (DayOfWeek) -> Unit = { DefaultWeekDayRenderer(it) },
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    onWeekChangedListener: ((LocalDate) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    //Each page is a week, so we calculate how many we have
    val numberOfPages = remember { startDate.until(endDate, DateTimeUnit.WEEK) + 1 }

    //Calculate the starting page that should be displayed
    val startingPage = remember { startDate.until(day, DateTimeUnit.WEEK) }

    val pagerState = rememberPagerState(
        initialPage = startingPage,
        pageCount = { numberOfPages }
    )

    //SideEffect for the PageChanged callback
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { pageNumber ->
            val date = day.plus(pageNumber - startingPage, DateTimeUnit.WEEK)
            onWeekChangedListener?.invoke(date)
        }
    }

    HorizontalPager(
        pageSize = PageSize.Fill,
        state = pagerState,
        verticalAlignment = verticalAlignment,
        modifier = modifier
    ) { pageNumber ->
        val date = day.plus(pageNumber - startingPage, DateTimeUnit.WEEK)

        WeekCalendar(
            day = date,
            displayHeader = displayHeader,
            displayWeekDaysName = displayWeekDaysName,
            startingWeekDay = startingWeekDay,
            displayExtraDays = displayExtraDays,
            dayOnClick = dayOnClick,
            dayRenderer = dayRenderer,
            headerRenderer = headerRenderer,
            weekDayRenderer = weekDayRenderer
        )
    }
}