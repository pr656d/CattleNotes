package com.pr656d.cattlenotes.shared.domain.cattle.addedit.validator

import androidx.annotation.StringRes
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.MutableLiveData
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.shared.domain.MediatorUseCase
import com.pr656d.cattlenotes.shared.domain.cattle.addedit.IsCattleExistWithTagNumberUseCase
import com.pr656d.cattlenotes.shared.domain.result.Result
import javax.inject.Inject

class CattleValidatorUseCase @Inject constructor(
    private val isCattleExistWithTagNumberUseCase: IsCattleExistWithTagNumberUseCase
) : MediatorUseCase<Pair<String?, Long?>, @StringRes Int>() {
    companion object {
        const val TAG = "CattleValidatorUseCase"

        const val VALID_FIELD = 0
    }

    private val isCattleExistResult = MutableLiveData<Result<Boolean>>()

    init {
        result.addSource(isCattleExistResult) {
            if (it is Result.Success) {
                result.postValue(
                    Result.Success(
                        if (it.data)
                            R.string.error_already_exists
                        else
                            VALID_FIELD
                    )
                )
            }
        }
    }

    override fun execute(parameters: Pair<String?, Long?>) {
        val tagNumber = parameters.first
        val oldTagNumber = parameters.second

        if (tagNumber.isNullOrEmpty()) {
            result.postValue(Result.Success(R.string.error_empty_field))
            return
        }

        if (tagNumber.count() > 19) {
            result.postValue(Result.Success(R.string.error_length_exceed))
            return
        }

        if (tagNumber.toLongOrNull() == null) {
            result.postValue(Result.Success(R.string.error_contain_digits_only))
            return
        }

        if (tagNumber.toLongOrNull() == oldTagNumber) {
            result.postValue(Result.Success(VALID_FIELD))
            return
        }

        isCattleExistWithTagNumberUseCase(tagNumber.toLong(), isCattleExistResult)
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