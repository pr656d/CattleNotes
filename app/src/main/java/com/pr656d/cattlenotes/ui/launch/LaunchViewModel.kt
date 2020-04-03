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