package com.pr656d.cattlenotes.shared.utils.common

import androidx.core.text.isDigitsOnly
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.data.repository.CattleDataRepository

object CattleValidator {

    const val TAG = "CattleValidator"
    const val VALID_MESSAGE_ID = -100

    suspend fun isValidTagNumber(
        tagNumber: String?,
        repository: CattleDataRepository,
        oldTagNumber: String? = null
    ): Int = when {
        tagNumber.isNullOrBlank() -> R.string.error_empty_field
        tagNumber == oldTagNumber -> VALID_MESSAGE_ID
        repository.isCattleExists(tagNumber) -> R.string.error_already_exists
        else -> VALID_MESSAGE_ID
    }

    fun isValidType(type: String?): Int = when {
        type.isNullOrBlank() -> R.string.error_empty_field
        else -> VALID_MESSAGE_ID
    }

    fun isValidLactation(totalCalving: String?): Int = when {
        totalCalving.isNullOrBlank() -> R.string.error_empty_field
        !totalCalving.isDigitsOnly() -> R.string.error_contain_digits_only
        else -> VALID_MESSAGE_ID
    }

    fun isValidBreed(breed: String?): Int = when {
        breed.isNullOrBlank() -> R.string.error_empty_field
        else -> VALID_MESSAGE_ID
    }

    fun isValidGroup(group: String?): Int = when {
        group.isNullOrBlank() -> R.string.error_empty_field
        else -> VALID_MESSAGE_ID
    }
}