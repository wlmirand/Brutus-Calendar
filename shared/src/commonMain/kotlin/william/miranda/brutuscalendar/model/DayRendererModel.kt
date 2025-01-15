package william.miranda.brutuscalendar.model

import kotlinx.datetime.LocalDate

/**
 * This class wrap all information to be passed to the DayRenderer (which will render each Day)
 * Stores:
 *  - LocalDate for the Day
 *  - MonthYear that represents the Calendar Month and Year
 *  - Selection state
 */
data class DayRendererModel(
    val date: LocalDate,
    val calendarMonthYear: MonthYear
)