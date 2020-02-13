package com.pr656d.cattlenotes.utils

import java.util.*

object BreedingUtil {

    /** AI DATE + 21 days */
    val REPEAT_HEAT_DURATION = 21

    /** AI DATE + 60 days */
    val PREGNANCY_CHECK_DURATION = 60

    /** AI DATE + 210 days */
    val DRY_OFF_DURATION = 210

    /** AI DATE + 280 days */
    val CALVING_DURATION = 280


    fun getExpectedRepeatHeatDate(aiDate: Date): Date = Calendar.getInstance().run {
        time = aiDate
        add(Calendar.DATE, REPEAT_HEAT_DURATION)
        time
    }

    fun getExpectedPregnancyCheckDate(aiDate: Date): Date = Calendar.getInstance().run {
        time = aiDate
        add(Calendar.DATE, PREGNANCY_CHECK_DURATION)
        time
    }

    fun getExpectedDryOffDate(aiDate: Date): Date = Calendar.getInstance().run {
        time = aiDate
        add(Calendar.DATE, DRY_OFF_DURATION)
        time
    }

    fun getExpectedCalvingDate(aiDate: Date): Date = Calendar.getInstance().run {
        time = aiDate
        add(Calendar.DATE, CALVING_DURATION)
        time
    }
}