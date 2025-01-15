package william.miranda.brutuscalendar.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import william.miranda.brutuscalendar.helper.DefaultDayRenderer
import william.miranda.brutuscalendar.helper.DefaultHeaderRenderer
import william.miranda.brutuscalendar.helper.DefaultWeekDayRenderer
import william.miranda.brutuscalendar.helper.NUM_DAYS_WEEK
import william.miranda.brutuscalendar.helper.calculateOffset
import william.miranda.brutuscalendar.helper.getWeekDays
import william.miranda.brutuscalendar.helper.toList
import william.miranda.brutuscalendar.model.DayRendererModel
import william.miranda.brutuscalendar.model.MonthYear

/**
 * Composable to display the Month Calendar
 * Since clicks and actions will be handled by The Renderer its the responsibility to the caller
 * to handle Selection
 */
@Composable
fun MonthCalendar(
    monthYear: MonthYear,
    modifier: Modifier = Modifier,
    displayHeader: Boolean = true,
    displayWeekDaysName: Boolean = true,
    startingWeekDay: DayOfWeek = DayOfWeek.SUNDAY,
    displayExtraDays: Boolean = false,
    dayOnClick: ((LocalDate) -> Unit)? = null,
    dayRenderer: @Composable (DayRendererModel) -> Unit = { DefaultDayRenderer(it, dayOnClick) },
    headerRenderer: @Composable (MonthYear) -> Unit = {  DefaultHeaderRenderer(it) },
    weekDayRenderer: @Composable (DayOfWeek) -> Unit = { DefaultWeekDayRenderer(it) }
) {
    //List to Hold the Week Days, in the order we should display them
    val weekDays = remember { getWeekDays(startingWeekDay) }

    //First Day to be displayed
    val firstDay = remember {
        getFirstDay(
            year = monthYear.year,
            month = monthYear.month,
            weekDays = weekDays,
            displayExtraDays = displayExtraDays
        )
    }

    //Last Day to be displayed
    val lastDay = remember {
        getLastDay(
            year = monthYear.year,
            month = monthYear.month,
            weekDays = weekDays,
            displayExtraDays = displayExtraDays
        )
    }

    //List of the interval to be displayed
    val daysList = remember { (firstDay..lastDay).toList(DateTimeUnit.DAY) }

    //Offset for the First Day
    val firstDayOffset = remember {
        calculateOffset(
            firstDayOfMonth = firstDay.dayOfWeek,
            weekDays = weekDays,
        )
    }

    Column {
        //Header
        if (displayHeader) {
            headerRenderer(monthYear)
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(NUM_DAYS_WEEK),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalArrangement = Arrangement.SpaceEvenly,
            userScrollEnabled = false,
            modifier = modifier
        ) {

            //Weekday Label
            if (displayWeekDaysName) {
                items(
                    items = weekDays,
                    key = { it.ordinal }
                ) { weekDayRenderer(it) }
            }

            //Start Offset
            if (firstDayOffset != 0 && !displayExtraDays) {
                item(
                    span = { GridItemSpan(firstDayOffset) }
                ) {}
            }

            //Days
            items(
                items = daysList,
                key = { it.toString() }
            ) {
                DayRendererModel(
                    date = it,
                    calendarMonthYear = MonthYear(
                        year = monthYear.year,
                        month = monthYear.month
                    )
                ).also { dayRenderer(it) }
            }
        }
    }
}

/**
 * Return the first day that should be displayed by the Calendar
 * depending if we want or not complete the Extra Days from the Previous Period
 */
private fun getFirstDay(
    year: Int,
    month: Month,
    weekDays: List<DayOfWeek>,
    displayExtraDays: Boolean
): LocalDate {

    //If we dont want extra days (days from the previous month)
    //we just return the 1st day of the passed month
    val firstDayOfMonth = LocalDate(year, month, 1)
    if (!displayExtraDays) {
        return firstDayOfMonth
    }

    //If we should add the extra days, we need to find how many more to display
    val count = weekDays.indexOf(firstDayOfMonth.dayOfWeek)
    return firstDayOfMonth.minus(
        value = count,
        unit = DateTimeUnit.DAY
    )
}

/**
 * Return the last day that should be displayed by the Calendar
 * depending if we want or not complete the Extra Days from the Next Period
 */
private fun getLastDay(
    year: Int,
    month: Month,
    weekDays: List<DayOfWeek>,
    displayExtraDays: Boolean
): LocalDate {

    //If we dont want extra days (days from the next month)
    //we just return the last day of the passed month
    val lastDayOfMonth = LocalDate(year, month, 1)
        .plus(value = 1, unit = DateTimeUnit.MONTH)
        .minus(value = 1, unit = DateTimeUnit.DAY)
    if (!displayExtraDays) {
        return lastDayOfMonth
    }

    //If we should add the extra days, we need to find how many more to display
    val count = weekDays.size - weekDays.indexOf(lastDayOfMonth.dayOfWeek) - 1
    return lastDayOfMonth.plus(
        value = count,
        unit = DateTimeUnit.DAY
    )
}