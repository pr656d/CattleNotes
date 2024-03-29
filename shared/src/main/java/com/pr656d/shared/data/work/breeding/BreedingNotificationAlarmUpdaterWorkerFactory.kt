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
package com.pr656d.shared.data.work.breeding

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.pr656d.shared.data.login.datasources.AuthIdDataSource
import com.pr656d.shared.domain.breeding.notification.BreedingNotificationAlarmUpdater

class BreedingNotificationAlarmUpdaterWorkerFactory(
    private val breedingNotificationAlarmUpdater: BreedingNotificationAlarmUpdater,
    private val authIdDataSource: AuthIdDataSource
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            BreedingNotificationAlarmUpdaterWorker::class.java.name ->
                BreedingNotificationAlarmUpdaterWorker(
                    appContext,
                    workerParameters,
                    breedingNotificationAlarmUpdater,
                    authIdDataSource
                )
            else ->
                // Return null, so that the base class can delegate to the default WorkerFactory.
                null
        }
    }
}
