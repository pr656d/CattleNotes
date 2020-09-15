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
package com.pr656d.shared.domain.cattle.validator

import androidx.annotation.StringRes
import com.pr656d.shared.R
import com.pr656d.shared.di.DefaultDispatcher
import com.pr656d.shared.domain.SuspendUseCase
import com.pr656d.shared.domain.cattle.addedit.IsCattleExistWithTagNumberUseCase
import com.pr656d.shared.domain.cattle.validator.CattleValidator.VALID_FIELD
import com.pr656d.shared.domain.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class CattleTagNumberValidatorUseCase @Inject constructor(
    private val isCattleExistWithTagNumberUseCase: IsCattleExistWithTagNumberUseCase,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : SuspendUseCase<Pair<String?, Long?>, @StringRes Int>(defaultDispatcher) {

    override suspend fun execute(parameters: Pair<String?, Long?>): Int {
        val (tagNumber, oldTagNumber) = parameters

        return when {
            tagNumber.isNullOrEmpty() -> R.string.error_empty_field

            tagNumber.count() > 19 -> R.string.error_length_exceed

            tagNumber.toLongOrNull() == null -> R.string.error_contain_digits_only

            tagNumber.toLongOrNull() == oldTagNumber -> VALID_FIELD

            else -> isCattleExistWithTagNumberUseCase(tagNumber.toLong()).let {
                if ((it as? Result.Success)?.data == true)
                    R.string.error_already_exists
                else
                    VALID_FIELD
            }
        }
    }
}
