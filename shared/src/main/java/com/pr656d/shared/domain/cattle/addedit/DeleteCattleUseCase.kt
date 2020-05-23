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

package com.pr656d.shared.domain.cattle.addedit

import com.pr656d.model.Cattle
import com.pr656d.shared.data.cattle.CattleRepository
import com.pr656d.shared.di.DefaultDispatcher
import com.pr656d.shared.domain.SuspendUseCase
import com.pr656d.shared.domain.breeding.notification.BreedingNotificationAlarmUpdater
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

open class DeleteCattleUseCase @Inject constructor(
    private val cattleRepository: CattleRepository,
    private val breedingNotificationAlarmUpdater: BreedingNotificationAlarmUpdater,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : SuspendUseCase<Cattle, Unit>(defaultDispatcher) {
    override suspend fun execute(parameters: Cattle) {
        // Cancel alarms for this cattle's breedings.
        breedingNotificationAlarmUpdater.cancelByCattleId(parameters.id)
        cattleRepository.deleteCattle(parameters)
    }
}