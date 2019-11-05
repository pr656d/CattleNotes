package com.pr656d.cattlenotes.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import com.pr656d.cattlenotes.ui.base.BaseViewModel
import com.pr656d.cattlenotes.utils.common.Event
import com.pr656d.cattlenotes.utils.network.NetworkHelper
import io.reactivex.disposables.CompositeDisposable

class SplashViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    enum class LaunchDestination {
        MAIN_ACTIVITY,
        LOGIN_ACTIVITY
    }

    private val _launchDestination = MutableLiveData<Event<LaunchDestination>>()
    val launchDestination: LiveData<Event<LaunchDestination>> = _launchDestination

    // Activity will set this user at setup()
    private var firebaseUser: FirebaseUser? = null

    fun setFirebaseUser(user: FirebaseUser?) { firebaseUser = user }

    override fun onCreate() {
        if (firebaseUser != null) {
            _launchDestination.postValue(Event(LaunchDestination.MAIN_ACTIVITY))
        } else {
            _launchDestination.postValue(Event(LaunchDestination.LOGIN_ACTIVITY))
        }
    }
}