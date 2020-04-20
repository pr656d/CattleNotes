package com.pr656d.shared.utils

import android.content.Context
import com.pr656d.shared.R
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

object TimeUtils {

    fun toLocalDate(dayOfMonth: Int, month: Int, year: Int): LocalDate {
        return LocalDate.of(year, month, dayOfMonth)
    }

    fun toLocalDate(epochMillis : Long): LocalDate {
        return LocalDate.ofEpochDay(epochMillis)
    }

    /**
     * @param dateString should be in dd/MM/yyyy format.
     */
    fun toLocalDate(dateString: String): LocalDate {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return LocalDate.parse(dateString, formatter)
    }

    fun dateString(localDate: LocalDate, pattern: String? = null): String {
        return DateTimeFormatter
            .ofPattern(pattern ?: "dd/MM/yyyy")
            .format(localDate)
    }

    fun toEpochMillis(localDate: LocalDate): Long {
        return localDate.toEpochDay()
    }

    /** Return a string to use for nearest day to given time. */
    fun getLabelForTimelineHeader(context: Context, time: LocalDate): String {
        val timeNow = LocalDate.now()
        return when {
            time.isEqual(timeNow.minusDays(1)) -> context.getString(R.string.day_yesterday)
            time.isEqual(timeNow) -> context.getString(R.string.day_today)
            time.isEqual(timeNow.plusDays(1)) -> context.getString(R.string.day_tomorrow)
            else -> context.getString(R.string.day_itself, dateString(time, "MMM d, yyyy"))
        }
    }
}