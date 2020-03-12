package com.pr656d.shared.utils

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

    fun toEpochMillis(localDate: LocalDate): Long {
        return localDate.toEpochDay()
    }

    fun dateString(localDate: LocalDate): String {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy").format(localDate)
    }
}