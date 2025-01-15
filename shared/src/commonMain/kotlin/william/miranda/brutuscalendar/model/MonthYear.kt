package william.miranda.brutuscalendar.model

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import william.miranda.brutuscalendar.helper.today

/**
 * Convenient class to wrap Month with Year information
 */
data class MonthYear(
    val year: Int,
    val month: Month
) {
    companion object {
        const val FIRST_DAY = 1
        const val LAST_DAY = -1

        fun fromLocalDate(date: LocalDate): MonthYear {
            return MonthYear(
                year = date.year,
                month = date.month
            )
        }

        fun today() = fromLocalDate(date = LocalDate.today())
    }

    fun toLocalDate(dayOfMonth: Int): LocalDate {
        return if (dayOfMonth != LAST_DAY) {
            LocalDate(
                year = this.year,
                month = this.month,
                dayOfMonth = dayOfMonth
            )
        } else {
            LocalDate(
                year = this.year,
                month = this.month,
                dayOfMonth = 1
            ).plus(1, DateTimeUnit.MONTH)
                .minus(1, DateTimeUnit.DAY)
        }
    }
}