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

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.pr656d.cattlenotes.ui.launch.LaunchViewModel.LaunchDestination.LoginActivity
import com.pr656d.cattlenotes.ui.launch.LaunchViewModel.LaunchDestination.MainActivity
import com.pr656d.shared.domain.invoke
import com.pr656d.shared.domain.login.GetLoginAndAllStepsCompletedUseCase
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result.Success
import javax.inject.Inject

class LaunchViewModel @Inject constructor(
    loginAndAllStepsCompletedUseCase: GetLoginAndAllStepsCompletedUseCase
) : ViewModel() {

    val launchDestination: LiveData<Event<LaunchDestination>> = liveData {
        val isLoginAndAllStepsCompleted = loginAndAllStepsCompletedUseCase().let {
            (it as? Success)?.data == true
        }

        emit(
            if (isLoginAndAllStepsCompleted)
                Event(MainActivity)
            else
                Event(LoginActivity)
        )
    }

    sealed class LaunchDestination {
        object MainActivity : LaunchDestination()
        object LoginActivity : LaunchDestination()
    }
}