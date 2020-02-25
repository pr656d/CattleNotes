package com.pr656d.cattlenotes.ui.launch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.pr656d.cattlenotes.shared.domain.launch.GetLoginCompletedUseCase
import com.pr656d.cattlenotes.shared.domain.result.Event
import com.pr656d.cattlenotes.shared.domain.result.Result
import com.pr656d.cattlenotes.shared.domain.result.Result.Success
import com.pr656d.cattlenotes.ui.launch.LaunchViewModel.LaunchDestination.LOGIN_ACTIVITY
import com.pr656d.cattlenotes.ui.launch.LaunchViewModel.LaunchDestination.MAIN_ACTIVITY
import com.pr656d.cattlenotes.ui.settings.theme.ThemedActivityDelegate
import javax.inject.Inject

class LaunchViewModel @Inject constructor(
    getLoginCompletedUseCase: GetLoginCompletedUseCase,
    themedActivityDelegate: ThemedActivityDelegate
) : ViewModel(),
    ThemedActivityDelegate by themedActivityDelegate{

    val launchDestination: LiveData<Event<LaunchDestination>>

    private val loginCompletedResult = MutableLiveData<Result<Boolean>>()

    init {
        getLoginCompletedUseCase(Unit, loginCompletedResult)
        launchDestination = loginCompletedResult.map {
            if ((it as Success).data)
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