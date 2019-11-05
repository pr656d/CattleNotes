package com.pr656d.cattlenotes.ui.launch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.pr656d.cattlenotes.utils.common.Event
import com.pr656d.cattlenotes.ui.launch.LaunchDestination.MAIN_ACTIVITY
import com.pr656d.cattlenotes.ui.launch.LaunchDestination.LOGIN_ACTIVITY
import javax.inject.Inject

class LaunchViewModel @Inject constructor(
    firebaseUser: FirebaseUser?
) : ViewModel() {

    private val _launchDestination = MutableLiveData<Event<LaunchDestination>>()
    val launchDestination: LiveData<Event<LaunchDestination>> = _launchDestination

    init {
        if (firebaseUser != null) {
            _launchDestination.postValue(Event(MAIN_ACTIVITY))
        } else {
            _launchDestination.postValue(Event(LOGIN_ACTIVITY))
        }
    }

}

enum class LaunchDestination {
    MAIN_ACTIVITY,
    LOGIN_ACTIVITY
}