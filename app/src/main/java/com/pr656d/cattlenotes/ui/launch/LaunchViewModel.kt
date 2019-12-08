package com.pr656d.cattlenotes.ui.launch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.pr656d.cattlenotes.shared.base.BaseViewModel
import com.pr656d.cattlenotes.ui.launch.LaunchDestination.LOGIN_ACTIVITY
import com.pr656d.cattlenotes.ui.launch.LaunchDestination.MAIN_ACTIVITY
import com.pr656d.cattlenotes.utils.common.Event
import javax.inject.Inject

class LaunchViewModel @Inject constructor(
    firebaseUser: FirebaseUser?
) : BaseViewModel() {

    private val _launchDestination = MutableLiveData<Event<LaunchDestination>>()
    val launchDestination: LiveData<Event<LaunchDestination>> = _launchDestination

    init {
        _launchDestination.postValue(
            Event(
                if (firebaseUser != null) MAIN_ACTIVITY
                else LOGIN_ACTIVITY
            )
        )
    }
}

enum class LaunchDestination {
    MAIN_ACTIVITY,
    LOGIN_ACTIVITY
}