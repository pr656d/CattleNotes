package com.pr656d.cattlenotes.shared.domain.cattle.addedit.validator

import androidx.core.text.isDigitsOnly
import com.pr656d.cattlenotes.R

object CattleValidator {
    const val VALID_FIELD = 0

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
}