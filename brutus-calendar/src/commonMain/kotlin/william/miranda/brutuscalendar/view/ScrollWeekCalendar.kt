package william.miranda.brutuscalendar.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.YearMonth
import kotlinx.datetime.plus
import william.miranda.brutuscalendar.helper.DefaultDayRenderer
import william.miranda.brutuscalendar.helper.DefaultHeaderRenderer
import william.miranda.brutuscalendar.helper.DefaultWeekDayRenderer
import william.miranda.brutuscalendar.helper.today
import william.miranda.brutuscalendar.model.DayRendererModel
import william.miranda.brutuscalendar.model.WeekPagerState

/**
 * Remember for the WeekPagerState
 */
@Composable
public fun rememberWeekPagerState(
    startingDate: LocalDate = LocalDate.today(),
    firstDate: LocalDate = YearMonth(year = 0, month = Month.JANUARY).firstDay,
    lastDate: LocalDate = YearMonth(year = 9999, month = Month.DECEMBER).lastDay
): WeekPagerState {
    return rememberSaveable(saver = WeekPagerState.Saver) {
        WeekPagerState(
            startingDate = startingDate,
            firstDate = firstDate,
            lastDate = lastDate
        )
    }
}

/**
 * Pager that inflates Week Calendars for each Week
 * We use a LocalDate as a reference to show the Week
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
public fun ScrollWeekCalendar(
    pagerState: WeekPagerState,
    modifier: Modifier = Modifier,
    displayHeader: Boolean = true,
    displayWeekDaysName: Boolean = true,
    startingWeekDay: DayOfWeek = DayOfWeek.SUNDAY,
    displayExtraDays: Boolean = false,
    dayOnClick: ((LocalDate) -> Unit)? = null,
    dayRenderer: @Composable RowScope.(DayRendererModel) -> Unit = { DefaultDayRenderer(it, dayOnClick) },
    headerRenderer: @Composable (YearMonth) -> Unit = { DefaultHeaderRenderer(it) },
    weekDayRenderer: @Composable RowScope.(DayOfWeek) -> Unit = { DefaultWeekDayRenderer(it) },
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    onWeekChangedListener: ((LocalDate) -> Unit)? = null
) {
    //SideEffect for the PageChanged callback
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { pageNumber ->
            val date = pagerState.startingDate.plus(pageNumber, DateTimeUnit.WEEK)
            onWeekChangedListener?.invoke(date)
        }
    }

    HorizontalPager(
        pageSize = PageSize.Fill,
        state = pagerState,
        verticalAlignment = verticalAlignment,
        modifier = modifier
    ) { pageNumber ->
        val date = pagerState.startingDate.plus(pageNumber, DateTimeUnit.WEEK)

        WeekCalendar(
            date = date,
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
