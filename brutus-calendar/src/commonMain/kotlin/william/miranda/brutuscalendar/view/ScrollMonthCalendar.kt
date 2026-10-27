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
import william.miranda.brutuscalendar.model.YearMonthPagerState

/**
 * Remember for the YearMonthPagerState
 */
@Composable
public fun rememberYearMonthPagerState(
    firstYearMonth: YearMonth = YearMonth(year = 0, month = Month.JANUARY),
    lastYearMonth: YearMonth = YearMonth(year = 9999, month = Month.DECEMBER),
    startingYearMonth: YearMonth = firstYearMonth,
): YearMonthPagerState {
    return rememberSaveable(saver = YearMonthPagerState.Saver) {
        YearMonthPagerState(
            startingYearMonth = startingYearMonth,
            firstYearMonth = firstYearMonth,
            lastYearMonth = lastYearMonth
        )
    }
}

/**
 * Pager that inflates Month Calendars for each month
 */
@Composable
public fun ScrollMonthCalendar(
    modifier: Modifier = Modifier,
    pagerState: YearMonthPagerState = rememberYearMonthPagerState(),
    displayHeader: Boolean = true,
    displayWeekDaysName: Boolean = true,
    startingWeekDay: DayOfWeek = DayOfWeek.SUNDAY,
    displayExtraDays: Boolean = false,
    dayOnClick: ((LocalDate) -> Unit)? = null,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    onMonthChangedListener: ((YearMonth) -> Unit)? = null,
    headerRenderer: @Composable (YearMonth) -> Unit = { DefaultHeaderRenderer(it) },
    weekDayRenderer: @Composable BoxScope.(DayOfWeek) -> Unit = { DefaultWeekDayRenderer(it) },
    dayRenderer: @Composable BoxScope.(DayRendererModel) -> Unit = { DefaultDayRenderer(it, dayOnClick) },
) {
    // Keep the latest listener without restarting the page observation.
    val currentOnMonthChangedListener = rememberUpdatedState(onMonthChangedListener)

    //SideEffect for the PageChanged callback
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { pageNumber ->
            val yearMonth = pagerState.getYearMonthFromPageNumber(pageNumber)
            currentOnMonthChangedListener.value?.invoke(yearMonth)
        }
    }

    //Pager
    HorizontalPager(
        pageSize = PageSize.Fill,
        state = pagerState,
        verticalAlignment = verticalAlignment,
        modifier = modifier
    ) { pageNumber ->
        //Display the MonthCalendar
        MonthCalendar(
            yearMonth = pagerState.getYearMonthFromPageNumber(pageNumber),
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
