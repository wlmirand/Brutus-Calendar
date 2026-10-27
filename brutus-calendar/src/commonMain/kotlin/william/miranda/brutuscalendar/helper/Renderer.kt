package william.miranda.brutuscalendar.helper

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
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

/** Colors used by the built-in renderers. Custom renderers own their styling. */
@Immutable
private data class CalendarColors(
    val textColor: Color,
    val extraDayTextColor: Color,
    val weekendTextColor: Color,
    val todayTextColor: Color,
    val todayBackgroundColor: Color,
    val weekendBackgroundColor: Color,
) {
    companion object {
        private val DarkColors = CalendarColors(
            textColor = Color(0xFFE6E1E5),
            extraDayTextColor = Color(0xFFB5B0B8),
            weekendTextColor = Color(0xFFD0BCFF),
            todayTextColor = Color(0xFFEADDFF),
            todayBackgroundColor = Color(0xFF4F378B),
            weekendBackgroundColor = Color(0xFF25212E),
        )

        private val LightColors = CalendarColors(
            textColor = Color(0xFF1B1B1F),
            extraDayTextColor = Color(0xFF60606A),
            weekendTextColor = Color(0xFF4F378B),
            todayTextColor = Color(0xFF3B2468),
            todayBackgroundColor = Color(0xFFEADDFF),
            weekendBackgroundColor = Color(0xFFF4F0FA),
        )

        /** Returns a Material-independent palette following the system theme by default. */
        @Composable
        fun defaultColors(darkTheme: Boolean = isSystemInDarkTheme()): CalendarColors =
            if (darkTheme) DarkColors else LightColors
    }
}


/**
 * Default Renderer for the Days
 */
@Composable
internal fun DefaultDayRenderer(
    dayRendererModel: DayRendererModel, //Model containing the day to be rendered
    onClick: ((LocalDate) -> Unit)? //Day OnClick
) {
    val colors = CalendarColors.defaultColors()

    val isToday = dayRendererModel.date.isToday()
    val isWeekend = dayRendererModel.date.isWeekend()
    val textColor = when {
        !dayRendererModel.date.belongsToYearMonth(dayRendererModel.calendarYearMonth) -> colors.extraDayTextColor
        isToday -> colors.todayTextColor
        isWeekend -> colors.weekendTextColor
        else -> colors.textColor
    }

    val background = when {
        isToday -> colors.todayBackgroundColor
        isWeekend -> colors.weekendBackgroundColor
        else -> Color.Transparent
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
                shape = if (isToday) CircleShape else RectangleShape
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
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
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
    val colors = CalendarColors.defaultColors()

    BasicText(
        modifier = Modifier.fillMaxWidth(),
        text = "${yearMonth.month} / ${yearMonth.year}",
        style = TextStyle(textAlign = TextAlign.Center, color = colors.textColor)
    )
}

/**
 * Default Renderer for the Week Days
 */
@Composable
internal fun DefaultWeekDayRenderer(dayOfWeek: DayOfWeek) {
    val colors = CalendarColors.defaultColors()

    BasicText(
        modifier = Modifier,//.weight(1f),
        text = dayOfWeek.name.substring(0..2),
        style = TextStyle(
            color = colors.textColor,
            textAlign = TextAlign.Center
        )
    )
}
