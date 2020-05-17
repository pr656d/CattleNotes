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

package com.pr656d.shared.domain.milk.sms

import androidx.lifecycle.LiveData
import com.pr656d.model.Milk
import com.pr656d.shared.data.prefs.PreferenceStorageRepository
import javax.inject.Inject

open class ObserveMilkSmsSourceUseCase @Inject constructor(
    private val preferenceStorageRepository: PreferenceStorageRepository
) {
    operator fun invoke(): LiveData<Milk.Source.Sms> {
        return preferenceStorageRepository.getObservablePreferredMilkSmsSource()
    }
}