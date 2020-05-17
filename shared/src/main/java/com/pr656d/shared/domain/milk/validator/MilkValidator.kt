/*
 * Copyright (c) 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pr656d.shared.domain.milk.validator

import com.pr656d.shared.R
import org.threeten.bp.ZonedDateTime

object MilkValidator {
    const val VALID_FIELD = 0

    fun isValidType(type: String?): Int = when {
        type.isNullOrBlank() -> R.string.error_empty_field
        else -> VALID_FIELD
    }

    fun isQuantityValid(quantity: String?) = when {
        quantity.isNullOrBlank() -> R.string.error_empty_field
        quantity.toFloatOrNull() == null -> R.string.error_contain_digits_only
        else -> VALID_FIELD
    }

    fun isFatValid(quantity: String?) = when {
        quantity.isNullOrBlank() -> R.string.error_empty_field
        quantity.toFloatOrNull() == null -> R.string.error_contain_digits_only
        else -> VALID_FIELD
    }

    fun isDateTimeValid(dateTime: ZonedDateTime?) = when(dateTime) {
        null -> R.string.error_empty_field
        else -> VALID_FIELD
    }

}