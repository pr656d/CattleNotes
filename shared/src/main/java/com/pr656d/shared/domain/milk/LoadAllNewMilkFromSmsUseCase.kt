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
import com.pr656d.shared.di.IoDispatcher
import com.pr656d.shared.domain.SuspendUseCase
import com.pr656d.shared.performance.PerformanceHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

open class LoadAllNewMilkFromSmsUseCase @Inject constructor(
    private val milkRepository: MilkRepository,
    private val performanceHelper: PerformanceHelper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SuspendUseCase<Milk.Source.Sms, List<Milk>>(ioDispatcher) {

    companion object {
        private const val TRACE_KEY_LOAD_ALL_NEW_MILK_FROM_SMS = "load_all_new_milk_from_sms"
    }

    override suspend fun execute(parameters: Milk.Source.Sms): List<Milk> = coroutineScope {
        performanceHelper.startTrace(TRACE_KEY_LOAD_ALL_NEW_MILK_FROM_SMS)

        val dbMilkList = async {
            milkRepository.getAllMilk().firstOrNull() ?: emptyList()
        }

        val smsMilkList = async { milkRepository.getAllMilkFromSms(parameters) }

        /**  Remove elements exist in db from smsMilkList. */
        val list = smsMilkList.await().minus(dbMilkList.await())

        performanceHelper.putAttribute(
            TRACE_KEY_LOAD_ALL_NEW_MILK_FROM_SMS,
            "size" to list.count().toString()
        )

        performanceHelper.stopTrace(TRACE_KEY_LOAD_ALL_NEW_MILK_FROM_SMS)

        list
    }
}