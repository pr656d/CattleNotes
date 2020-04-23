package com.pr656d.shared.utils

import android.content.Context
import com.pr656d.shared.R
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

object TimeUtils {

    /**
     * Return [LocalDate] of [dayOfMonth], [month] and [year].
     */
    fun toLocalDate(dayOfMonth: Int, month: Int, year: Int): LocalDate {
        return LocalDate.of(year, month, dayOfMonth)
    }

    /**
     * Return [LocalDate] of epoch day.
     */
    fun toLocalDate(epochDay : Long): LocalDate {
        return LocalDate.ofEpochDay(epochDay)
    }

    /**
     * Return [LocalDate] from string.
     * @param dateString should be in dd/MM/yyyy format.
     */
    fun toLocalDate(dateString: String): LocalDate {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return LocalDate.parse(dateString, formatter)
    }

    /**
     * Return [ZonedDateTime] of [dayOfMonth], [month] and [year].
     */
    fun toZonedDateTime(dayOfMonth: Int, month: Int, year: Int): ZonedDateTime {
        return toZonedDateTime(toLocalDate(dayOfMonth, month, year))
    }

    /**
     * Return [ZonedDateTime] of [LocalDate], [LocalTime] for [ZoneId].
     */
    fun toZonedDateTime(
        localDate: LocalDate,
        localTime: LocalTime? = null,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): ZonedDateTime {
        return ZonedDateTime.ofInstant(
            ZonedDateTime.of(localDate, localTime, zoneId).toInstant(),
            zoneId
        )
    }

    /**
     * Return [LocalTime] of [nanoOfDay].
     */
    fun toLocalTime(nanoOfDay: Long): LocalTime {
        return LocalTime.ofNanoOfDay(nanoOfDay)
    }

    /**
     * Return [LocalTime] of [hourOfDay] and [minute].
     */
    fun toLocalTime(hourOfDay: Int, minute: Int): LocalTime {
        return LocalTime.of(hourOfDay, minute)
    }

    /**
     * Return [LocalDate] as [String] in this [pattern].
     * Default return as 01/12/2020.
     */
    fun dateString(localDate: LocalDate, pattern: String = "dd/MM/yyyy"): String {
        return DateTimeFormatter
            .ofPattern(pattern)
            .format(localDate)
    }

    /**
     * Return [LocalTime] as [String] in this [pattern].
     * Default return as 09:00 AM.
     */
    fun timeString(localTime: LocalTime, pattern: String = "h:mm a"): String {
        return localTime.format(DateTimeFormatter.ofPattern(pattern))
    }

    /**
     * Return epoch day for [LocalDate].
     */
    fun toLong(localDate: LocalDate): Long {
        return localDate.toEpochDay()
    }

    /**
     * Return epoch milli of [ZonedDateTime].
     */
    fun toEpochMilli(time: ZonedDateTime): Long {
        return time.toInstant().toEpochMilli()
    }

    /**
     * Return epoch milli of [LocalDate], [LocalTime] for [ZoneId].
     */
    fun toEpochMilli(
        localDate: LocalDate,
        localTime: LocalTime? = null,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): Long {
        return toZonedDateTime(localDate, localTime, zoneId).toInstant().toEpochMilli()
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