package com.pr656d.cattlenotes.ui.launch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.data.local.prefs.UserPreferences
import com.pr656d.cattlenotes.ui.launch.LaunchViewModel.LaunchDestination.LOGIN_ACTIVITY
import com.pr656d.cattlenotes.ui.launch.LaunchViewModel.LaunchDestination.MAIN_ACTIVITY
import com.pr656d.cattlenotes.utils.common.Event
import javax.inject.Inject

class LaunchViewModel @Inject constructor(userPreferences: UserPreferences) : ViewModel() {

    private val _launchDestination = MutableLiveData<Event<LaunchDestination>>()
    val launchDestination: LiveData<Event<LaunchDestination>> = _launchDestination

    init {
        _launchDestination.value = Event(
            if (userPreferences.isUserLoggedIn())
                MAIN_ACTIVITY
            else
                LOGIN_ACTIVITY
        )
    }

    enum class LaunchDestination {
        MAIN_ACTIVITY,
        LOGIN_ACTIVITY
    }
}