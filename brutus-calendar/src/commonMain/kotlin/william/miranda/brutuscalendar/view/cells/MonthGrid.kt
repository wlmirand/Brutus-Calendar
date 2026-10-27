package william.miranda.brutuscalendar.view.cells

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import william.miranda.brutuscalendar.helper.NUM_DAYS_WEEK
import william.miranda.brutuscalendar.model.DayRendererModel

/**
 * Grid definition for the Month Calendar
 */
@Composable
internal fun MonthGrid(
    modifier: Modifier = Modifier,
    yearMonth: YearMonth,
    weekDays: List<DayOfWeek>,
    calendarDays: List<LocalDate>,
    startOffset: Int = 0,
    columns: Int = NUM_DAYS_WEEK,
    weekDayRenderer: @Composable BoxScope.(DayOfWeek) -> Unit,
    dayRenderer: @Composable BoxScope.(DayRendererModel) -> Unit
) {
    //Add nulls to fulfill the 7 days per week
    val newDays = remember(startOffset, calendarDays) {
        List(startOffset) { null } + calendarDays
    }

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
        newDays.chunked(columns).forEach { rowDays ->
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

                // Fill incomplete last row
                repeat(columns - rowDays.size) {
                    Spacer(
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
