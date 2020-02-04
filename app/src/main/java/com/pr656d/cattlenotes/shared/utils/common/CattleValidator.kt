package com.pr656d.cattlenotes.shared.utils.common

import androidx.core.text.isDigitsOnly
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.data.model.Cattle

object CattleValidator {

    const val TAG = "CattleValidator"
    const val VALID_FIELD = 0

    fun isValidTagNumber(
        tagNumber: Long?,
        oldCattle: Cattle? = null
    ): Int = when (tagNumber) {
        null -> R.string.error_empty_field
        oldCattle?.tagNumber -> VALID_FIELD
        else -> VALID_FIELD
    }

    fun isValidType(type: String?): Int = when {
        type.isNullOrBlank() -> R.string.error_empty_field
        else -> VALID_FIELD
    }

    fun isValidLactation(totalCalving: String?): Int = when {
        totalCalving.isNullOrBlank() -> R.string.error_empty_field
        !totalCalving.isDigitsOnly() -> R.string.error_contain_digits_only
        else -> VALID_FIELD
    }

    fun isValidBreed(breed: String?): Int = when {
        breed.isNullOrBlank() -> R.string.error_empty_field
        else -> VALID_FIELD
    }

    fun isValidGroup(group: String?): Int = when {
        group.isNullOrBlank() -> R.string.error_empty_field
        else -> VALID_FIELD
    }

    fun isValidAmount(amount: Long?): Int = when (amount) {
        null -> VALID_FIELD
        else -> VALID_FIELD
    }
}