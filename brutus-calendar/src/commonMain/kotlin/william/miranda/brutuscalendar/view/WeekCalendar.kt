package william.miranda.brutuscalendar.view

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.yearMonth
import william.miranda.brutuscalendar.helper.DefaultDayRenderer
import william.miranda.brutuscalendar.helper.DefaultHeaderRenderer
import william.miranda.brutuscalendar.helper.DefaultWeekDayRenderer
import william.miranda.brutuscalendar.helper.getWeekDays
import william.miranda.brutuscalendar.helper.toList
import william.miranda.brutuscalendar.model.DayRendererModel
import william.miranda.brutuscalendar.view.cells.WeekGrid

/**
 * Composable to display the Week Calendar
 * Here we take a LocalDate for the Week Reference
 */
@Composable
public fun WeekCalendar(
    modifier: Modifier = Modifier,
    date: LocalDate,
    displayHeader: Boolean = true,
    displayWeekDaysName: Boolean = true,
    startingWeekDay: DayOfWeek = DayOfWeek.SUNDAY,
    displayExtraDays: Boolean = false,
    dayOnClick: ((LocalDate) -> Unit)? = null,
    headerRenderer: @Composable (YearMonth) -> Unit = { yearMonth -> DefaultHeaderRenderer(yearMonth) },
    weekDayRenderer: @Composable BoxScope.(DayOfWeek) -> Unit = { dayOfWeek -> DefaultWeekDayRenderer(dayOfWeek) },
    dayRenderer: @Composable BoxScope.(DayRendererModel) -> Unit = { model -> DefaultDayRenderer(model, dayOnClick) }
) {
    //List to Hold the Week Days, in the order we should display them
    val weekDays = remember(startingWeekDay) { getWeekDays(startingWeekDay) }

    //First Day of the Week
    val firstDay = remember(date, weekDays) {
        getFirstDay(
            day = date,
            weekDays = weekDays
        )
    }

    //Last Day of the Week
    val lastDay = remember(date, weekDays) {
        getLastDay(
            day = date,
            weekDays = weekDays
        )
    }

    //List of the interval to be displayed
    //We need to prepare a list of 7 elements to be displayed (ensured by firstDay..lastDay)
    //If by any means the item should not be displayed, we set it to null
    val daysList = remember(firstDay, lastDay, displayExtraDays, date.yearMonth) {
        val list = (firstDay..lastDay).toList(DateTimeUnit.DAY)

        if (!displayExtraDays) {
            //No extra days, so we need to check elements to be excluded
            list.map { element ->
                if (element.yearMonth == date.yearMonth) element else null
            }
        } else {
            //Display extra days so, just return the original list
            list
        }
    }

    Column(modifier = modifier) {
        //Header
        if (displayHeader) {
            headerRenderer(date.yearMonth)
        }

        //Grid
        WeekGrid(
            modifier = Modifier.fillMaxWidth(),
            yearMonth = date.yearMonth,
            weekDays = if (displayWeekDaysName) weekDays else emptyList(),
            calendarDays = daysList,
            weekDayRenderer = weekDayRenderer,
            dayRenderer = dayRenderer
        )
    }
}

/**
 * Return the first day of the week that contains [day] based on the [weekDays]
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
 * Return the last day of the week that contains [day] based on the [weekDays]
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
