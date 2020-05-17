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

package com.pr656d.cattlenotes.ui.launch

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.ui.launch.LaunchViewModel.LaunchDestination.LOGIN_ACTIVITY
import com.pr656d.cattlenotes.ui.launch.LaunchViewModel.LaunchDestination.MAIN_ACTIVITY
import com.pr656d.shared.domain.login.GetLoginAndAllStepsCompletedUseCase
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result
import com.pr656d.shared.domain.result.Result.Success
import javax.inject.Inject

class LaunchViewModel @Inject constructor(
    loginAndAllStepsCompletedUseCase: GetLoginAndAllStepsCompletedUseCase
) : ViewModel() {

    val launchDestination = MediatorLiveData<Event<LaunchDestination>>()

    private val loginAndAllStepsCompletedResult = MutableLiveData<Result<Boolean>>()

    init {
        loginAndAllStepsCompletedUseCase(Unit, loginAndAllStepsCompletedResult)

        launchDestination.addSource(loginAndAllStepsCompletedResult) {
            if ((it as? Success)?.data == true)
                launchDestination.postValue(Event(MAIN_ACTIVITY))
            else
                launchDestination.postValue(Event(LOGIN_ACTIVITY))
        }
    }

    enum class LaunchDestination {
        MAIN_ACTIVITY,
        LOGIN_ACTIVITY
    }
}