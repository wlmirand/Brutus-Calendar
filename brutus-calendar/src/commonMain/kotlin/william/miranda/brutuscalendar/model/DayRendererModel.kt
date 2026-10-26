package william.miranda.brutuscalendar.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth

/**
 * This class wrap all information to be passed to the DayRenderer (which will render each Day)
 * Stores:
 *  - LocalDate for the Date that is being Rendered
 *  - YearMonth that represents the Calendar Month and Year
 */
public data class DayRendererModel(
    val date: LocalDate,
    val calendarYearMonth: YearMonth
)
