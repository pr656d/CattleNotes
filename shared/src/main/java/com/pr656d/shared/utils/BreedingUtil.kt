/*
 * Copyright 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pr656d.shared.utils

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

    fun getExpectedRepeatHeatDate(aiDate: LocalDate): LocalDate = aiDate.plusDays(
        REPEAT_HEAT_DURATION
    )

    fun getExpectedPregnancyCheckDate(aiDate: LocalDate): LocalDate = aiDate.plusDays(
        PREGNANCY_CHECK_DURATION
    )

    fun getExpectedDryOffDate(aiDate: LocalDate): LocalDate = aiDate.plusDays(DRY_OFF_DURATION)

    fun getExpectedCalvingDate(aiDate: LocalDate): LocalDate = aiDate.plusDays(CALVING_DURATION)
}
