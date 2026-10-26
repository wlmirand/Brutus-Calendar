package william.miranda.brutuscalendar.demo

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.YearMonth
import kotlinx.coroutines.launch
import william.miranda.brutuscalendar.helper.today
import william.miranda.brutuscalendar.model.YearMonthPagerState
import william.miranda.brutuscalendar.view.ScrollMonthCalendar

@Composable
fun App() {
    val colors = if (isSystemInDarkTheme()) {
        darkColorScheme(
            primary = Color(0xFFCCBEFF),
            onPrimary = Color(0xFF30205F),
            background = Color(0xFF141219),
            surface = Color(0xFF201D27),
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF6750A4),
            onPrimary = Color.White,
            background = Color(0xFFF7F5FA),
            surface = Color.White,
        )
    }

    MaterialTheme(colorScheme = colors) {
        val pagerState = remember {
            YearMonthPagerState(
                startingYearMonth = YearMonth(year = 2026, month = Month.SEPTEMBER),
                firstYearMonth = YearMonth(year = 2026, month = Month.JANUARY),
                lastYearMonth = YearMonth(year = 2026, month = Month.DECEMBER)
            )
        }
        val scope = rememberCoroutineScope()
        val today = remember { LocalDate.today() }
        var selectedDate by remember { mutableStateOf<LocalDate?>(LocalDate(2026, 9, 16)) }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier = Modifier
                    .safeDrawingPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "BRUTUS CALENDAR",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text("Make room for your days.", style = MaterialTheme.typography.headlineMedium)
                    Text(
                        text = "A little space to plan what comes next.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                ) {
                    ScrollMonthCalendar(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        pagerState = pagerState,
                        startingWeekDay = DayOfWeek.SUNDAY,
                        displayExtraDays = true,
                        headerRenderer = { yearMonth ->
                            DemoMonthHeader(
                                yearMonth = yearMonth,
                                previousEnabled = pagerState.currentPage > 0 && !pagerState.isScrollInProgress,
                                nextEnabled = pagerState.currentPage < pagerState.pageCount - 1 && !pagerState.isScrollInProgress,
                                onPrevious = { scope.launch { pagerState.animateScrollToPreviousPage() } },
                                onNext = { scope.launch { pagerState.animateScrollToNextPage() } },
                            )
                        },
                        weekDayRenderer = { dayOfWeek -> DemoWeekDay(dayOfWeek) },
                        dayRenderer = { model ->
                            // Custom renderers own their click handling; selection lives outside the cells.
                            DemoDayCell(
                                model = model,
                                today = today,
                                selected = selectedDate == model.date,
                                onClick = { selectedDate = model.date },
                            )
                        },
                    )
                }

                DemoSelectedDate(selectedDate)
                Text(
                    text = "Swipe to explore · Tap a day to select",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
