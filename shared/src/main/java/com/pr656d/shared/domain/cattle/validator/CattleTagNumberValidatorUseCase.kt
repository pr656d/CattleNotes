package com.pr656d.shared.domain.cattle.validator

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import com.pr656d.shared.R
import com.pr656d.shared.domain.MediatorUseCase
import com.pr656d.shared.domain.cattle.addedit.IsCattleExistWithTagNumberUseCase
import com.pr656d.shared.domain.cattle.validator.CattleValidator.VALID_FIELD
import com.pr656d.shared.domain.internal.DefaultScheduler
import com.pr656d.shared.domain.result.Result
import javax.inject.Inject

class CattleTagNumberValidatorUseCase @Inject constructor(
    private val isCattleExistWithTagNumberUseCase: IsCattleExistWithTagNumberUseCase
) : MediatorUseCase<Pair<String?, Long?>, @StringRes Int>() {
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
        DefaultScheduler.execute {
            val (tagNumber, oldTagNumber) = parameters

            when {
                tagNumber.isNullOrEmpty() -> {
                    result.postValue(Result.Success(R.string.error_empty_field))
                }
                tagNumber.count() > 19 -> {
                    result.postValue(Result.Success(R.string.error_length_exceed))
                }
                tagNumber.toLongOrNull() == null -> {
                    result.postValue(Result.Success(R.string.error_contain_digits_only))
                }
                tagNumber.toLongOrNull() == oldTagNumber -> {
                    result.postValue(Result.Success(VALID_FIELD))
                }
                else -> {
                    isCattleExistWithTagNumberUseCase(tagNumber.toLong(), isCattleExistResult)
                }
            }
        }
    }
}