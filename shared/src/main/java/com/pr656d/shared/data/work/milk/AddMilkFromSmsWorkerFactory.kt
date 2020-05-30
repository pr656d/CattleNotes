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

package com.pr656d.shared.data.work.milk

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.pr656d.shared.data.milk.datasource.MilkDataSourceFromSms
import com.pr656d.shared.domain.milk.AddMilkUseCase
import com.pr656d.shared.domain.milk.sms.GetPreferredMilkSmsSourceUseCase
import com.pr656d.shared.domain.settings.GetAutomaticMilkingCollectionUseCase
import com.pr656d.shared.performance.PerformanceHelper

class AddMilkFromSmsWorkerFactory(
    private val getPreferredMilkSmsSourceUseCase: GetPreferredMilkSmsSourceUseCase,
    private val getAutomaticMilkingCollectionUseCase: GetAutomaticMilkingCollectionUseCase,
    private val milkDataSourceFromSms: MilkDataSourceFromSms,
    private val addMilkUseCase: AddMilkUseCase,
    private val performanceHelper: PerformanceHelper
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            AddMilkFromSmsWorker::class.java.name ->
                AddMilkFromSmsWorker(
                    appContext,
                    workerParameters,
                    getPreferredMilkSmsSourceUseCase,
                    getAutomaticMilkingCollectionUseCase,
                    milkDataSourceFromSms,
                    addMilkUseCase,
                    performanceHelper
                )
            else ->
                // Return null, so that the base class can delegate to the default WorkerFactory.
                null
        }
    }
}