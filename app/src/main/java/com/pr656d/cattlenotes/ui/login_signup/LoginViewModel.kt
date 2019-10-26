package com.pr656d.cattlenotes.ui.login_signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import com.pr656d.cattlenotes.ui.base.BaseViewModel
import com.pr656d.cattlenotes.utils.common.Event
import com.pr656d.cattlenotes.utils.network.NetworkHelper
import io.reactivex.disposables.CompositeDisposable

class LoginViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    private val _launchFirebaseLoginUI: MutableLiveData<Event<Unit>> = MutableLiveData()
    val launchFirebaseLoginUI: LiveData<Event<Unit>> = _launchFirebaseLoginUI

    override fun onCreate() { }

    fun onLoginClick() = _launchFirebaseLoginUI.postValue(Event(Unit))
}
