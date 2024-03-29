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

import android.os.Build
import com.pr656d.model.Theme
import com.pr656d.shared.di.DefaultDispatcher
import com.pr656d.shared.domain.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetAvailableThemesUseCase @Inject constructor(
    @DefaultDispatcher defaultDispatcher: CoroutineDispatcher
) : UseCase<Unit, List<Theme>>(defaultDispatcher) {

    override fun execute(parameters: Unit): List<Theme> = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
            listOf(Theme.LIGHT, Theme.DARK, Theme.SYSTEM)
        }
        else -> {
            listOf(Theme.LIGHT, Theme.DARK, Theme.BATTERY_SAVER)
        }
    }
}
