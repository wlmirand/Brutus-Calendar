package william.miranda.brutuscalendar.view.cells

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import william.miranda.brutuscalendar.helper.NUM_DAYS_WEEK
import william.miranda.brutuscalendar.model.DayRendererModel

/**
 * Grid definition for the Week Calendar
 */
@Composable
internal fun WeekGrid(
    modifier: Modifier = Modifier,
    yearMonth: YearMonth,
    weekDays: List<DayOfWeek>,
    calendarDays: List<LocalDate?>,
    columns: Int = NUM_DAYS_WEEK,
    weekDayRenderer: @Composable BoxScope.(DayOfWeek) -> Unit,
    dayRenderer: @Composable BoxScope.(DayRendererModel) -> Unit
) {
    //For this Week Grid, we expect the full [NUM_DAYS_WEEK] elements because here we dont know
    //If we have to append empty space to the beginning or the end in the case we get less elements
    require(calendarDays.size == NUM_DAYS_WEEK) { "Need to receive 7 days to render. Set null to invisible elements" }

    Column(
        modifier = modifier
    ) {
        //Render the WeekDays
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            weekDays.forEach { weekDay ->
                key(weekDay) {
                    //Wrap in a Box to ensure that all cells follows the Grid
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        //Call the Renderer for each weekday
                        weekDayRenderer(weekDay)
                    }
                }
            }

        }

        //Render the Days. Take 7 each time
        calendarDays.chunked(columns).forEach { rowDays ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                rowDays.forEach { date ->
                    if (date == null) {
                        //Fill the empty cells at the start
                        Spacer(
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        // Keep remembered cell state associated with its date within this row.
                        key(date) {
                            //Wrap in a Box to ensure that all cells follows the Grid
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                //Render each Day
                                DayRendererModel(
                                    date = date,
                                    calendarYearMonth = yearMonth
                                ).also { model -> dayRenderer(model) }
                            }
                        }
                    }
                }
            }
        }
    }
}
