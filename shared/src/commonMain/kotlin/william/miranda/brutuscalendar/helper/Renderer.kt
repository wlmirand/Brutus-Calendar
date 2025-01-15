package william.miranda.brutuscalendar.helper

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import william.miranda.brutuscalendar.model.DayRendererModel
import william.miranda.brutuscalendar.model.MonthYear

/**
 * Default Renderer for the Days
 */
@Composable
internal fun DefaultDayRenderer(
    dayRendererModel: DayRendererModel, //Calendar Month and Year
    onClick: ((LocalDate) -> Unit)? //Day to be rendered. This may be an extra day (outside the month)
) {
    val textColor = when {
        !dayRendererModel.date.belongsToMonthYear(dayRendererModel.calendarMonthYear) -> Color.Gray
        dayRendererModel.date.isToday() -> Color.Red
        dayRendererModel.date.isWeekend() -> Color.Cyan
        else -> Color.Unspecified
    }

    val background = when {
        dayRendererModel.date.isToday() -> Color.LightGray
        dayRendererModel.date.isWeekend() -> Color(
            red = 0xEB,
            green = 0xEB,
            blue = 0xEB,
            alpha = 0x15
        )

        else -> Color.Unspecified
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = onClick != null,
                onClick = { onClick?.invoke(dayRendererModel.date) }
            )
            .background(
                color = background,
                shape = if (dayRendererModel.date.isToday()) CircleShape else RectangleShape
            )
    ) {
        Text(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            text = "${dayRendererModel.date.dayOfMonth}",
            textAlign = TextAlign.Center,
            color = textColor,
            fontWeight = if (dayRendererModel.date.isToday()) FontWeight.Bold else FontWeight.Normal
        )
    }
}

/**
 * Default Renderer for the Header
 */
@Composable
internal fun DefaultHeaderRenderer(
    monthYear: MonthYear
) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = "${monthYear.month} / ${monthYear.year}",
        textAlign = TextAlign.Center
    )
}

/**
 * Default Renderer for the Week Days
 */
@Composable
internal fun DefaultWeekDayRenderer(dayOfWeek: DayOfWeek) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = dayOfWeek.name.substring(0..2),
        textAlign = TextAlign.Center
    )
}