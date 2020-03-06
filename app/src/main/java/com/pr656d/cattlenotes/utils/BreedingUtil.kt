package com.pr656d.cattlenotes.utils

import org.threeten.bp.LocalDate

object BreedingUtil {

    /** AI DATE + 21 days */
    private const val REPEAT_HEAT_DURATION = 21L

    /** AI DATE + 60 days */
    private const val PREGNANCY_CHECK_DURATION = 60L

    /** AI DATE + 210 days */
    private const val DRY_OFF_DURATION = 210L

    /** AI DATE + 280 days */
    private const val CALVING_DURATION = 280L

    fun getExpectedRepeatHeatDate(aiDate: LocalDate): LocalDate = aiDate.plusDays(REPEAT_HEAT_DURATION)

    fun getExpectedPregnancyCheckDate(aiDate: LocalDate): LocalDate = aiDate.plusDays(PREGNANCY_CHECK_DURATION)

    fun getExpectedDryOffDate(aiDate: LocalDate): LocalDate = aiDate.plusDays(DRY_OFF_DURATION)

    fun getExpectedCalvingDate(aiDate: LocalDate): LocalDate = aiDate.plusDays(CALVING_DURATION)
}