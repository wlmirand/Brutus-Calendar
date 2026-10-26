package william.miranda.brutuscalendar.demo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.yearMonth
import william.miranda.brutuscalendar.model.DayRendererModel

@Composable
internal fun DemoMonthHeader(
    yearMonth: YearMonth,
    previousEnabled: Boolean,
    nextEnabled: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                Text(
                    text = yearMonth.month.name.displayName(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = yearMonth.year.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(
                onClick = onPrevious,
                enabled = previousEnabled,
                modifier = Modifier.semantics { contentDescription = "Previous month" },
            ) {
                Text("‹", style = MaterialTheme.typography.headlineMedium)
            }
            IconButton(
                onClick = onNext,
                enabled = nextEnabled,
                modifier = Modifier.semantics { contentDescription = "Next month" },
            ) {
                Text("›", style = MaterialTheme.typography.headlineMedium)
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    }
}

@Composable
internal fun DemoWeekDay(dayOfWeek: DayOfWeek) {
    val weekend = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY
    Text(
        text = dayOfWeek.name.take(3).displayName(),
        modifier = Modifier.padding(vertical = 12.dp),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = if (weekend) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
internal fun DemoDayCell(
    model: DayRendererModel,
    today: LocalDate,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val inDisplayedMonth = model.date.yearMonth == model.calendarYearMonth
    val isToday = model.date == today
    val colors = MaterialTheme.colorScheme
    val textColor = when {
        selected -> colors.onPrimary
        !inDisplayedMonth -> colors.onSurface.copy(alpha = 0.35f)
        isToday -> colors.primary
        else -> colors.onSurface
    }

    Surface(
        onClick = onClick,
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth()
            .aspectRatio(1f)
            .semantics {
                this.selected = selected
                contentDescription = model.date.toString() + if (isToday) ", today" else ""
            },
        shape = RoundedCornerShape(14.dp),
        color = when {
            selected -> colors.primary
            isToday -> colors.primaryContainer.copy(alpha = 0.4f)
            else -> Color.Transparent
        },
        border = if (isToday && !selected) BorderStroke(1.dp, colors.primary) else null,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = model.date.day.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selected || isToday) FontWeight.Bold else FontWeight.Medium,
                color = textColor,
            )
        }
    }
}

@Composable
internal fun DemoSelectedDate(date: LocalDate?) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = date?.day?.toString() ?: "—",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("SELECTED DAY", style = MaterialTheme.typography.labelSmall)
                Text(
                    text = date?.let {
                        "${it.dayOfWeek.name.displayName()}, ${it.month.name.displayName()} ${it.day}"
                    } ?: "Choose a day above",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = date?.year?.toString() ?: "Your plans start here.",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

private fun String.displayName(): String = lowercase().replaceFirstChar { it.uppercase() }
