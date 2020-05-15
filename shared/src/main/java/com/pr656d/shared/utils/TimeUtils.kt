package com.pr656d.shared.utils

import android.content.Context
import com.pr656d.shared.R
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter

object TimeUtils {

    /**
     * Return [LocalDate] of [dayOfMonth], [month] and [year].
     */
    fun toLocalDate(dayOfMonth: Int, month: Int, year: Int): LocalDate =
        LocalDate.of(year, month, dayOfMonth)

    /**
     * Return [LocalDate] of epoch day.
     */
    fun toLocalDate(epochDay : Long): LocalDate = LocalDate.ofEpochDay(epochDay)

    /**
     * Return [LocalDate] from string.
     * @param dateString should be in dd/MM/yyyy format.
     */
    fun toLocalDate(dateString: String, pattern: String = "dd/MM/yyyy"): LocalDate =
        LocalDate.parse(dateString, DateTimeFormatter.ofPattern(pattern))

    /**
     * Return [ZonedDateTime] of [dayOfMonth], [month] and [year].
     */
    fun toZonedDateTime(
        dayOfMonth: Int, month: Int, year: Int,
        hour: Int = 0, minute: Int = 0, second: Int = 0, nanoSecond: Int = 0,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): ZonedDateTime = ZonedDateTime.of(
        year, month, dayOfMonth,
        hour, minute, second, nanoSecond,
        zoneId
    )

    /**
     * Return [ZonedDateTime] of [LocalDate], [LocalTime] for [ZoneId].
     */
    fun toZonedDateTime(
        localDate: LocalDate,
        localTime: LocalTime? = null,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): ZonedDateTime = ZonedDateTime.of(localDate, localTime, zoneId)

    /**
     * Returns [ZonedDateTime] from [millis].
     */
    fun toZonedDateTime(millis: Long, zoneId: ZoneId = ZoneId.systemDefault()): ZonedDateTime =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), zoneId)

    /**
     * Return [LocalTime] of [nanoOfDay].
     */
    fun toLocalTime(nanoOfDay: Long): LocalTime = LocalTime.ofNanoOfDay(nanoOfDay)

    /**
     * Return [LocalTime] of [hourOfDay] and [minute].
     */
    fun toLocalTime(hourOfDay: Int, minute: Int): LocalTime = LocalTime.of(hourOfDay, minute)

    /**
     * Return [LocalDate] as [String] in this [pattern].
     * Default return as 01/12/2020.
     */
    fun dateString(localDate: LocalDate, pattern: String = "dd/MM/yyyy"): String =
        DateTimeFormatter.ofPattern(pattern).format(localDate)

    /**
     * Return [ZonedDateTime] as [String] in this [pattern].
     * Default return as 01/12/2020.
     */
    fun dateString(localDate: ZonedDateTime, pattern: String = "dd/MM/yyyy"): String =
        DateTimeFormatter
            .ofPattern(pattern)
            .format(localDate)

    /**
     * Return [LocalTime] as [String] in this [pattern] from [LocalTime].
     * Default return as 09:00 AM.
     */
    fun timeString(localTime: LocalTime, pattern: String = "h:mm a"): String =
        localTime.format(DateTimeFormatter.ofPattern(pattern))

    /**
     * Return [LocalTime] as [String] in this [pattern] from [ZonedDateTime].
     * Default return as 09:00 AM.
     */
    fun timeString(zonedDateTime: ZonedDateTime, pattern: String = "h:mm a"): String =
        zonedDateTime.format(DateTimeFormatter.ofPattern(pattern))

    fun dateTimeString(
        zonedDateTime: ZonedDateTime,
        pattern: String = "dd MMM, yyyy h:mm a"
    ): String = zonedDateTime.format(DateTimeFormatter.ofPattern(pattern))

    /**
     * Return epoch day for [LocalDate].
     */
    fun toLong(localDate: LocalDate): Long = localDate.toEpochDay()

    /**
     * Return epoch day for [LocalTime].
     */
    fun toLong(localTime: LocalTime): Long = localTime.toNanoOfDay()

    /**
     * Return epoch milli of [ZonedDateTime].
     */
    fun toEpochMilli(time: ZonedDateTime): Long = time.toInstant().toEpochMilli()

    /**
     * Returns epoch millis from
     * dayOfMonth, Month, Year, Hour, Minute, Second, NanoSecond and ZoneId.
     */
    fun toEpochMilli(
        dd: Int, mm: Int, yyyy: Int,
        hour: Int = 0, minute: Int = 0, second: Int = 0, nanoSecond: Int = 0,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): Long = toZonedDateTime(dd, mm, yyyy, hour, minute, second, nanoSecond, zoneId)
        .toInstant()
        .toEpochMilli()

    /**
     * Return epoch milli of [LocalDate], [LocalTime] for [ZoneId].
     */
    fun toEpochMilli(
        localDate: LocalDate,
        localTime: LocalTime? = null,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): Long = toZonedDateTime(localDate, localTime, zoneId).toInstant().toEpochMilli()

    /** Return a string to use for nearest day to given time. */
    fun getLabelForTimelineHeader(context: Context, localDate: LocalDate): String {
        val timeNow = LocalDate.now()
        return when {
            localDate.isEqual(timeNow.minusDays(1)) -> context.getString(R.string.day_yesterday)
            localDate.isEqual(timeNow) -> context.getString(R.string.day_today)
            localDate.isEqual(timeNow.plusDays(1)) -> context.getString(R.string.day_tomorrow)
            else -> context.getString(R.string.day_itself, dateString(localDate, "MMM d, yyyy"))
        }
    }

    /** Return a string to use for nearest day to given time. */
    fun getLabelForTimelineHeader(context: Context, zonedDateTime: ZonedDateTime): String {
        val timeNow = ZonedDateTime.now()
        return when {
            zonedDateTime.isEqual(timeNow.minusDays(1)) -> context.getString(R.string.day_yesterday)
            zonedDateTime.isEqual(timeNow) -> context.getString(R.string.day_today)
            zonedDateTime.isEqual(timeNow.plusDays(1)) -> context.getString(R.string.day_tomorrow)
            else -> context.getString(R.string.day_itself, dateString(zonedDateTime, "MMM d, yyyy"))
        }
    }

    /** Return a string to use for nearest date to given time */
    fun getLabelForMilkingHeader(context: Context, zonedDateTime: ZonedDateTime): String {
        return context.getString(R.string.day_itself, dateString(zonedDateTime, "MMM d, yyyy"))
    }
}