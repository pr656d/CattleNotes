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

package com.pr656d.shared.data.work

import androidx.work.DelegatingWorkerFactory
import com.pr656d.shared.data.login.datasources.AuthIdDataSource
import com.pr656d.shared.data.milk.datasource.MilkDataSourceFromSms
import com.pr656d.shared.data.work.breeding.BreedingNotificationAlarmUpdaterWorkerFactory
import com.pr656d.shared.data.work.milk.AddMilkFromSmsWorkerFactory
import com.pr656d.shared.domain.breeding.notification.BreedingNotificationAlarmUpdater
import com.pr656d.shared.domain.milk.AddMilkUseCase
import com.pr656d.shared.domain.milk.sms.GetPreferredMilkSmsSourceUseCase
import com.pr656d.shared.domain.settings.GetAutomaticMilkingCollectionUseCase
import com.pr656d.shared.performance.PerformanceHelper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CattleNotesWorkerFactory @Inject constructor(
    getPreferredMilkSmsSourceUseCase: GetPreferredMilkSmsSourceUseCase,
    getAutomaticMilkingCollectionUseCase: GetAutomaticMilkingCollectionUseCase,
    milkDataSourceFromSms: MilkDataSourceFromSms,
    addMilkUseCase: AddMilkUseCase,
    performanceHelper: PerformanceHelper,
    breedingNotificationAlarmUpdater: BreedingNotificationAlarmUpdater,
    authIdDataSource: AuthIdDataSource
) : DelegatingWorkerFactory() {
    init {
        addFactory(
            AddMilkFromSmsWorkerFactory(
                getPreferredMilkSmsSourceUseCase, getAutomaticMilkingCollectionUseCase,
                milkDataSourceFromSms, addMilkUseCase, performanceHelper
            )
        )

        addFactory(
            BreedingNotificationAlarmUpdaterWorkerFactory(
                breedingNotificationAlarmUpdater, authIdDataSource
            )
        )
    }
}