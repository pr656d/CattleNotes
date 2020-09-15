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
package com.pr656d.shared.domain.settings

import com.pr656d.model.Theme
import com.pr656d.shared.data.prefs.PreferenceStorageRepository
import com.pr656d.shared.di.DefaultDispatcher
import com.pr656d.shared.domain.FlowUseCase
import com.pr656d.shared.domain.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

open class ObserveThemeModeUseCase @Inject constructor(
    private val preferenceStorageRepository: PreferenceStorageRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, Theme>(defaultDispatcher) {

    override fun execute(parameters: Unit): Flow<Result<Theme>> {
        return preferenceStorageRepository.getObservableSelectedTheme().map { Result.Success(it) }
    }
}
