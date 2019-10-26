package com.pr656d.cattlenotes.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import com.pr656d.cattlenotes.ui.base.BaseViewModel
import com.pr656d.cattlenotes.utils.common.Event
import com.pr656d.cattlenotes.utils.log.Logger
import com.pr656d.cattlenotes.utils.network.NetworkHelper
import io.reactivex.disposables.CompositeDisposable

class SplashViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    private val _launchMain: MutableLiveData<Event<Unit>> = MutableLiveData()
    val launchMain: LiveData<Event<Unit>> = _launchMain

    private val _launchLogin: MutableLiveData<Event<Unit>> = MutableLiveData()
    val launchLogin: LiveData<Event<Unit>> = _launchLogin

    // Activity will set this authUI at setupView()
    private var _firebaseUser: FirebaseUser? = null

    fun setFirebaseUser(user: FirebaseUser?) { _firebaseUser = user }

    override fun onCreate() {
        if (_firebaseUser != null) {
            _launchMain.postValue(Event(Unit))
        } else {
            _launchLogin.postValue(Event(Unit))
        }
    }
}