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

package com.pr656d.cattlenotes.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.pr656d.cattlenotes.ui.settings.theme.ThemedActivityDelegate
import com.pr656d.shared.domain.auth.ObserveUserAuthStateUseCase
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.successOr
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MainViewModel @Inject constructor(
    themedActivityDelegate: ThemedActivityDelegate,
    observeAuthStateUseCase: ObserveUserAuthStateUseCase
) : ViewModel(),
    ThemedActivityDelegate by themedActivityDelegate {

    @ExperimentalCoroutinesApi
    val redirectToLoginScreen: LiveData<Event<Unit>> = liveData {
        observeAuthStateUseCase(Any()).map { result ->
            result.successOr(null)?.isSignedIn() == true
        }.collect { isSignedIn ->
            if (!isSignedIn) emit(Event(Unit))
        }
    }
}