package william.miranda.brutuscalendar.helper

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.isoDayNumber

/**
 * Get the List of Days of week from a given starting day
 * This makes some calculations easier
 */
internal fun getWeekDays(startingWeekDay: DayOfWeek): List<DayOfWeek> {
    val result = mutableListOf<DayOfWeek>()

    //Start with the Desired day
    var dayNumber = startingWeekDay.isoDayNumber

    while (result.size < DayOfWeek.entries.size) {
        result.add(DayOfWeek(dayNumber++))
        //If we passed the last one (which is Sunday), go back to the start
        if (dayNumber > DayOfWeek.SUNDAY.isoDayNumber) {
            dayNumber = 1
        }
    }
    return result
}
