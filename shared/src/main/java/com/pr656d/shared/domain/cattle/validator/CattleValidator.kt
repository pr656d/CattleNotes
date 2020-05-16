package com.pr656d.shared.domain.cattle.validator

import com.pr656d.model.AnimalType
import com.pr656d.shared.R
import com.pr656d.shared.utils.isDigitsOnly

object CattleValidator {
    const val VALID_FIELD = 0

    fun isValidType(type: String?): Int = when {
        type.isNullOrBlank() -> R.string.error_empty_field
        else -> VALID_FIELD
    }

    fun isValidLactation(totalCalving: String?, type: String?): Int = when {
        type == AnimalType.Bull.displayName -> VALID_FIELD
        totalCalving.isNullOrBlank() -> R.string.error_empty_field
        !totalCalving.isDigitsOnly() -> R.string.error_contain_digits_only
        else -> VALID_FIELD
    }

    fun isValidBreed(breed: String?): Int = when {
        breed.isNullOrBlank() -> R.string.error_empty_field
        else -> VALID_FIELD
    }

    fun isValidGroup(group: String?, type: String?): Int = when {
        type == AnimalType.Bull.displayName -> VALID_FIELD
        group.isNullOrBlank() -> R.string.error_empty_field
        else -> VALID_FIELD
    }
}