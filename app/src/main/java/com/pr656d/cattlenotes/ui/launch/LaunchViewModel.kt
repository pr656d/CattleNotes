package com.pr656d.cattlenotes.ui.launch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.pr656d.cattlenotes.ui.launch.LaunchViewModel.LaunchDestination.LOGIN_ACTIVITY
import com.pr656d.cattlenotes.ui.launch.LaunchViewModel.LaunchDestination.MAIN_ACTIVITY
import com.pr656d.shared.domain.launch.GetLoginCompletedUseCase
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result
import com.pr656d.shared.domain.result.Result.Success
import javax.inject.Inject

class LaunchViewModel @Inject constructor(
    getLoginCompletedUseCase: GetLoginCompletedUseCase
) : ViewModel() {

    val launchDestination: LiveData<Event<LaunchDestination>>

    private val loginCompletedResult = MutableLiveData<Result<Boolean>>()

    init {
        getLoginCompletedUseCase(Unit, loginCompletedResult)
        launchDestination = loginCompletedResult.map {
            if ((it as? Success)?.data == true)
                Event(MAIN_ACTIVITY)
            else
                Event(LOGIN_ACTIVITY)
        }
    }

    enum class LaunchDestination {
        MAIN_ACTIVITY,
        LOGIN_ACTIVITY
    }
}