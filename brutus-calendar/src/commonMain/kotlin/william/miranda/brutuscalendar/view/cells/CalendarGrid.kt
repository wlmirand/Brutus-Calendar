package william.miranda.brutuscalendar.view.cells

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.yearMonth
import william.miranda.brutuscalendar.helper.NUM_DAYS_WEEK
import william.miranda.brutuscalendar.model.DayRendererModel

/**
 * Grid definition for the Calendar
 */
@Composable
public fun CalendarGrid(
    modifier: Modifier = Modifier,
    weekDays: List<DayOfWeek>,
    calendarDays: List<LocalDate>,
    startOffset: Int = 0,
    columns: Int = NUM_DAYS_WEEK,
    weekDayRenderer: @Composable BoxScope.(DayOfWeek) -> Unit,
    dayRenderer: @Composable BoxScope.(DayRendererModel) -> Unit
) {
    val newDays = List(startOffset) { null } + calendarDays

    Column(
        modifier = modifier
    ) {
        //Render the WeekDays
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            weekDays.forEach { weekDay ->
                //Wrap in a Box to ensure all cells will follow the Grid
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    weekDayRenderer(weekDay)
                }
            }

        }

        //Render the Days
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
                        //Wrap in a Box to ensure all cells will follow the Grid
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            //Render each Day
                            DayRendererModel(
                                date = date,
                                calendarYearMonth = date.yearMonth
                            ).also { model -> dayRenderer(model) }
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
