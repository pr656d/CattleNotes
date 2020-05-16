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