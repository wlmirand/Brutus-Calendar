package william.miranda.brutuscalendar.view

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.YearMonth
import william.miranda.brutuscalendar.helper.DefaultDayRenderer
import william.miranda.brutuscalendar.helper.DefaultHeaderRenderer
import william.miranda.brutuscalendar.helper.DefaultWeekDayRenderer
import william.miranda.brutuscalendar.model.DayRendererModel
import william.miranda.brutuscalendar.model.WeekPagerState

/**
 * Remember for the WeekPagerState
 */
@Composable
public fun rememberWeekPagerState(
    firstDate: LocalDate = YearMonth(year = 0, month = Month.JANUARY).firstDay,
    lastDate: LocalDate = YearMonth(year = 9999, month = Month.DECEMBER).lastDay,
    startingDate: LocalDate = firstDate,
    startingDayOfWeek: DayOfWeek,
): WeekPagerState {
    return rememberSaveable(saver = WeekPagerState.Saver) {
        WeekPagerState(
            startingDate = startingDate,
            startingDayOfWeek = startingDayOfWeek,
            firstDate = firstDate,
            lastDate = lastDate
        )
    }
}

/**
 * Pager that inflates Week Calendars for each Week
 * We use a LocalDate as a reference to show the Week
 */
@Composable
public fun ScrollWeekCalendar(
    modifier: Modifier = Modifier,
    displayHeader: Boolean = true,
    displayWeekDaysName: Boolean = true,
    pagerState: WeekPagerState = rememberWeekPagerState(startingDayOfWeek = DayOfWeek.SUNDAY),
    displayExtraDays: Boolean = false,
    dayOnClick: ((LocalDate) -> Unit)? = null,
    headerRenderer: @Composable (YearMonth) -> Unit = { DefaultHeaderRenderer(it) },
    weekDayRenderer: @Composable BoxScope.(DayOfWeek) -> Unit = { DefaultWeekDayRenderer(it) },
    dayRenderer: @Composable BoxScope.(DayRendererModel) -> Unit = {
        DefaultDayRenderer(
            it,
            dayOnClick
        )
    },
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    onWeekChangedListener: ((LocalDate) -> Unit)? = null
) {
    // Keep the latest listener without restarting the page observation.
    val currentOnWeekChangedListener = rememberUpdatedState(onWeekChangedListener)

    //SideEffect for the PageChanged callback
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { pageNumber ->
            val date = pagerState.getFirstDateFromPageNumber(pageNumber)
            currentOnWeekChangedListener.value?.invoke(date)
        }
    }

    HorizontalPager(
        pageSize = PageSize.Fill,
        state = pagerState,
        verticalAlignment = verticalAlignment,
        modifier = modifier
    ) { pageNumber ->
        //Display the WeekCalendar
        WeekCalendar(
            date = pagerState.getFirstDateFromPageNumber(pageNumber),
            displayHeader = displayHeader,
            displayWeekDaysName = displayWeekDaysName,
            startingWeekDay = pagerState.startingDayOfWeek,
            displayExtraDays = displayExtraDays,
            dayOnClick = dayOnClick,
            dayRenderer = dayRenderer,
            headerRenderer = headerRenderer,
            weekDayRenderer = weekDayRenderer
        )
    }
}
