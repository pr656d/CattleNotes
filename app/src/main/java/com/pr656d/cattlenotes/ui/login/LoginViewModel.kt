package com.pr656d.cattlenotes.ui.login

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

    private val _launchMain: MutableLiveData<Event<Unit>> = MutableLiveData()
    val launchMain: LiveData<Event<Unit>> = _launchMain

    override fun onCreate() = _launchFirebaseLoginUI.postValue(Event(Unit))

    fun onLoginClick() = _launchFirebaseLoginUI.postValue(Event(Unit))

    fun onLoginSuccess() = _launchMain.postValue(Event(Unit))
}
