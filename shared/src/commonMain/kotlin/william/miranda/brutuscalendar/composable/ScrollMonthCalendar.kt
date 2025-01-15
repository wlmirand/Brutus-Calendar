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
import kotlinx.datetime.monthsUntil
import kotlinx.datetime.plus
import william.miranda.brutuscalendar.helper.DefaultDayRenderer
import william.miranda.brutuscalendar.helper.DefaultHeaderRenderer
import william.miranda.brutuscalendar.helper.DefaultWeekDayRenderer
import william.miranda.brutuscalendar.helper.toMonthYear
import william.miranda.brutuscalendar.model.DayRendererModel
import william.miranda.brutuscalendar.model.MonthYear

/**
 * Pager that inflates Month Calendars for each month
 * We use a MonthYear as a reference to show the Month
 */
@Composable
fun ScrollMonthCalendar(
    selectedMonthYear: MonthYear,
    modifier: Modifier = Modifier,
    startMonthYear: MonthYear = MonthYear(year = 0, month = Month.JANUARY),
    endMonthYear: MonthYear = MonthYear(year = 9999, month = Month.DECEMBER),
    displayHeader: Boolean = true,
    displayWeekDaysName: Boolean = true,
    startingWeekDay: DayOfWeek = DayOfWeek.SUNDAY,
    displayExtraDays: Boolean = false,
    dayOnClick: ((LocalDate) -> Unit)? = null,
    dayRenderer: @Composable (DayRendererModel) -> Unit = { DefaultDayRenderer(it, dayOnClick) },
    headerRenderer: @Composable (MonthYear) -> Unit = { DefaultHeaderRenderer(it) },
    weekDayRenderer: @Composable (DayOfWeek) -> Unit = { DefaultWeekDayRenderer(it) },
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    onMonthChangedListener: ((MonthYear) -> Unit)? = null
) {
    //Page we should start
    val selectedDate = remember {
        LocalDate(
            year = selectedMonthYear.year,
            month = selectedMonthYear.month,
            dayOfMonth = 1
        )
    }

    val startDate = remember { startMonthYear.toLocalDate(MonthYear.FIRST_DAY) }
    val endDate = remember { endMonthYear.toLocalDate(MonthYear.LAST_DAY) }

    //Each page is a month, so we calculate how many we have
    val numberOfPages = remember { startDate.monthsUntil(endDate) + 1 }

    //Calculate the starting page that should be displayed
    val startingPage = remember { startDate.monthsUntil(selectedDate) }

    val pagerState = rememberPagerState(
        initialPage = startingPage,
        pageCount = { numberOfPages }
    )

    //SideEffect for the PageChanged callback
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { pageNumber ->
            val monthYear = startDate.plus(pageNumber, DateTimeUnit.MONTH).toMonthYear()
            onMonthChangedListener?.invoke(monthYear)
        }
    }

    //Pager
    HorizontalPager(
        pageSize = PageSize.Fill,
        state = pagerState,
        verticalAlignment = verticalAlignment,
        modifier = modifier
    ) { pageNumber ->
        //Since each page is a Month, we just add them to the startDate
        val date = startDate.plus(pageNumber, DateTimeUnit.MONTH)

        //Display the MonthCalendar
        MonthCalendar(
            monthYear = date.toMonthYear(),
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