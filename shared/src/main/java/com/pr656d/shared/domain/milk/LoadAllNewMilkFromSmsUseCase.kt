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

package com.pr656d.shared.domain.milk

import com.pr656d.model.Milk
import com.pr656d.shared.data.milk.MilkRepository
import com.pr656d.shared.domain.UseCase
import com.pr656d.shared.performance.PerformanceHelper
import javax.inject.Inject

open class LoadAllNewMilkFromSmsUseCase @Inject constructor(
    private val milkRepository: MilkRepository,
    private val performanceHelper: PerformanceHelper
) : UseCase<Milk.Source.Sms, List<Milk>>() {
    companion object {
        private const val TRACE_KEY_LOAD_ALL_NEW_MILK_FROM_SMS = "load_all_new_milk_from_sms"
    }

    override fun execute(parameters: Milk.Source.Sms): List<Milk> {
        performanceHelper.startTrace(TRACE_KEY_LOAD_ALL_NEW_MILK_FROM_SMS)

        val dbMilkList = milkRepository.getAllMilkUnobserved()
        val smsMilkList = milkRepository.getAllMilkFromSms(parameters)

        /**  Remove elements exist in db from [smsMilkList]. */
        val list = smsMilkList.minus(dbMilkList)

        performanceHelper.putAttribute(
            TRACE_KEY_LOAD_ALL_NEW_MILK_FROM_SMS,
            "size" to list.count().toString()
        )

        performanceHelper.stopTrace(TRACE_KEY_LOAD_ALL_NEW_MILK_FROM_SMS)

        return list
    }
}