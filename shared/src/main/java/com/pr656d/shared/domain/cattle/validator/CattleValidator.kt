package com.pr656d.shared.domain.cattle.validator

import com.pr656d.shared.R
import com.pr656d.shared.utils.isDigitsOnly

object CattleValidator {
    const val VALID_FIELD = 0

    fun isValidType(type: String?): Int = when {
        type.isNullOrEmpty() -> R.string.error_empty_field
        else -> VALID_FIELD
    }

    fun isValidLactation(totalCalving: String?): Int = when {
        totalCalving.isNullOrEmpty() -> R.string.error_empty_field
        !totalCalving.isDigitsOnly() -> R.string.error_contain_digits_only
        else -> VALID_FIELD
    }

    fun isValidBreed(breed: String?): Int = when {
        breed.isNullOrEmpty() -> R.string.error_empty_field
        else -> VALID_FIELD
    }

    fun isValidGroup(group: String?): Int = when {
        group.isNullOrEmpty() -> R.string.error_empty_field
        else -> VALID_FIELD
    }
}