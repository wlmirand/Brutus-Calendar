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
import william.miranda.brutuscalendar.helper.DefaultDayRenderer
import william.miranda.brutuscalendar.helper.DefaultHeaderRenderer
import william.miranda.brutuscalendar.helper.DefaultWeekDayRenderer
import william.miranda.brutuscalendar.helper.getWeekDays
import william.miranda.brutuscalendar.model.DayRendererModel
import william.miranda.brutuscalendar.view.cells.MonthGrid

/**
 * Composable to display the Calendar for a given Month
 */
@Composable
public fun MonthCalendar(
    modifier: Modifier = Modifier,
    yearMonth: YearMonth,
    displayHeader: Boolean = true,
    displayWeekDaysName: Boolean = true,
    startingWeekDay: DayOfWeek = DayOfWeek.SUNDAY,
    displayExtraDays: Boolean = false,
    dayOnClick: ((LocalDate) -> Unit)? = null,
    headerRenderer: @Composable (YearMonth) -> Unit = { yearMonth -> DefaultHeaderRenderer(yearMonth) },
    weekDayRenderer: @Composable BoxScope.(DayOfWeek) -> Unit = { dayOfWeek ->
        DefaultWeekDayRenderer(
            dayOfWeek
        )
    },
    dayRenderer: @Composable BoxScope.(DayRendererModel) -> Unit = { model ->
        DefaultDayRenderer(
            model,
            dayOnClick
        )
    }
) {
    //List to Hold the Week Days, in the order we should display them
    val weekDays = remember(startingWeekDay) { getWeekDays(startingWeekDay) }

    //Offset for the First Day... This is needed only when not displaying extra days
    val firstDayOffset = remember(yearMonth, startingWeekDay, displayExtraDays) {
        if (displayExtraDays) {
            0
        } else {
            weekDays.indexOf(yearMonth.firstDay.dayOfWeek)
        }
    }

    //We need to check if we have to display extra days because this will change first and last days
    val firstDay = remember(displayExtraDays, yearMonth, weekDays) {
        if (!displayExtraDays) {
            yearMonth.firstDay
        } else {
            val offset = weekDays.indexOf(yearMonth.firstDay.dayOfWeek)
            yearMonth.firstDay.minus(offset, DateTimeUnit.DAY)
        }
    }

    val lastDay = remember(displayExtraDays, yearMonth, weekDays) {
        if (!displayExtraDays) {
            yearMonth.lastDay
        } else {
            val offset = weekDays.size - weekDays.indexOf(yearMonth.lastDay.dayOfWeek) - 1
            yearMonth.lastDay.plus(offset, DateTimeUnit.DAY)
        }
    }

    Column(modifier = modifier) {
        //Header
        if (displayHeader) {
            headerRenderer(yearMonth)
        }

        //Grid
        MonthGrid(
            modifier = Modifier.fillMaxWidth(),
            yearMonth = yearMonth,
            weekDays = if (displayWeekDaysName) weekDays else emptyList(),
            calendarDays = (firstDay..lastDay).toList(),
            startOffset = firstDayOffset,
            weekDayRenderer = weekDayRenderer,
            dayRenderer = dayRenderer
        )
    }
}
