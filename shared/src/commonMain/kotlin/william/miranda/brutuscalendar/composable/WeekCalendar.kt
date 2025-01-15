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
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import william.miranda.brutuscalendar.helper.DefaultDayRenderer
import william.miranda.brutuscalendar.helper.DefaultHeaderRenderer
import william.miranda.brutuscalendar.helper.DefaultWeekDayRenderer
import william.miranda.brutuscalendar.helper.NUM_DAYS_WEEK
import william.miranda.brutuscalendar.helper.calculateOffset
import william.miranda.brutuscalendar.helper.getWeekDays
import william.miranda.brutuscalendar.helper.toList
import william.miranda.brutuscalendar.helper.toMonthYear
import william.miranda.brutuscalendar.model.DayRendererModel
import william.miranda.brutuscalendar.model.MonthYear

/**
 * Composable to display the Week Calendar
 * Here we take a LocalDate for the Week Reference
 */
@Composable
fun WeekCalendar(
    day: LocalDate,
    modifier: Modifier = Modifier,
    displayHeader: Boolean = true,
    displayWeekDaysName: Boolean = true,
    startingWeekDay: DayOfWeek = DayOfWeek.SUNDAY,
    displayExtraDays: Boolean = false,
    dayOnClick: ((LocalDate) -> Unit)? = null,
    dayRenderer: @Composable (DayRendererModel) -> Unit = { DefaultDayRenderer(it, dayOnClick) },
    headerRenderer: @Composable (MonthYear) -> Unit = { DefaultHeaderRenderer(it) },
    weekDayRenderer: @Composable (DayOfWeek) -> Unit = { DefaultWeekDayRenderer(it) }
) {
    //List to Hold the Week Days, in the order we should display them
    val weekDays = remember { getWeekDays(startingWeekDay) }

    //First Day to be displayed
    val firstDay = remember {
        getFirstDay(
            day = day,
            weekDays = weekDays
        )
    }

    //Last Day to be displayed
    val lastDay = remember {
        getLastDay(
            day = day,
            weekDays = weekDays
        )
    }

    //List of the interval to be displayed
    val daysList = remember {
        val list = (firstDay..lastDay).toList(DateTimeUnit.DAY)
        if (!displayExtraDays) {
            list.filter {
                it.month == day.month
            }
        } else list
    }

    //Offset for the First Day
    val firstDayOffset = remember {
        calculateOffset(
            firstDayOfMonth = daysList.first().dayOfWeek,
            weekDays = weekDays,
        )
    }

    Column {
        //Header
        if (displayHeader) {
            headerRenderer(day.toMonthYear())
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
                    calendarMonthYear = day.toMonthYear(),
                ).also { dayRenderer(it) }
            }
        }
    }
}

/**
 * Return the first day that should be displayed by the Calendar
 */
private fun getFirstDay(
    day: LocalDate,
    weekDays: List<DayOfWeek>
): LocalDate {
    return day.minus(
        value = weekDays.indexOf(day.dayOfWeek),
        unit = DateTimeUnit.DAY
    )
}

/**
 * Return the last day that should be displayed by the Calendar
 * depending if we want or not complete the Extra Days from the Next Period
 */
private fun getLastDay(
    day: LocalDate,
    weekDays: List<DayOfWeek>
): LocalDate {
    return day.plus(
        value = weekDays.size - weekDays.indexOf(day.dayOfWeek) - 1,
        unit = DateTimeUnit.DAY
    )
}