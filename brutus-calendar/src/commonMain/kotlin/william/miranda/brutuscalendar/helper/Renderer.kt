package william.miranda.brutuscalendar.helper

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import william.miranda.brutuscalendar.model.DayRendererModel

/**
 * Default Renderer for the Days
 */
@Composable
internal fun DefaultDayRenderer(
    dayRendererModel: DayRendererModel, //Model containing the day to be rendered
    onClick: ((LocalDate) -> Unit)? //Day OnClick
) {
    val textColor = when {
        !dayRendererModel.date.belongsToYearMonth(dayRendererModel.calendarYearMonth) -> Color.Gray
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
            .padding(vertical = 2.dp)
            .background(
                color = background,
                shape = if (dayRendererModel.date.isToday()) CircleShape else RectangleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        BasicText(
            modifier = Modifier
                .padding(6.dp),
            text = "${dayRendererModel.date.day}",
            style = TextStyle(
                textAlign = TextAlign.Center,
                color = textColor,
                fontWeight = if (dayRendererModel.date.isToday()) FontWeight.Bold else FontWeight.Normal
            )
        )
    }
}

/**
 * Default Renderer for the Header
 */
@Composable
internal fun DefaultHeaderRenderer(
    yearMonth: YearMonth
) {
    BasicText(
        modifier = Modifier.fillMaxWidth(),
        text = "${yearMonth.month} / ${yearMonth.year}",
        style = TextStyle(textAlign = TextAlign.Center)
    )
}

/**
 * Default Renderer for the Week Days
 */
@Composable
internal fun DefaultWeekDayRenderer(dayOfWeek: DayOfWeek) {
    BasicText(
        modifier = Modifier,//.weight(1f),
        text = dayOfWeek.name.substring(0..2),
        style = TextStyle(
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    )
}
