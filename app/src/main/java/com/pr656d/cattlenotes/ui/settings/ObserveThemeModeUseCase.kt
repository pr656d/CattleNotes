/*
 * Copyright 2019 Google LLC
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

package com.pr656d.cattlenotes.ui.settings

import com.pr656d.cattlenotes.data.local.prefs.PreferenceStorage
import com.pr656d.cattlenotes.data.model.Theme
import com.pr656d.cattlenotes.data.model.themeFromStorageKey
import com.pr656d.cattlenotes.shared.domain.MediatorUseCase
import com.pr656d.cattlenotes.shared.domain.result.Result
import javax.inject.Inject

open class ObserveThemeModeUseCase @Inject constructor(
    private val preferenceStorage: PreferenceStorage
) : MediatorUseCase<Unit, Theme>() {
    override fun execute(parameters: Unit) {
        result.addSource(preferenceStorage.observableSelectedTheme) {
            result.postValue(Result.Success(themeFromStorageKey(it)))
        }
    }
}