package com.pr656d.cattlenotes.ui.login

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.ui.base.BaseViewModel
import com.pr656d.cattlenotes.utils.common.Event
import com.pr656d.cattlenotes.utils.common.Resource
import com.pr656d.cattlenotes.utils.network.NetworkHelper
import io.reactivex.disposables.CompositeDisposable

class LoginViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    private val _launchFirebaseLoginUI: MutableLiveData<Event<Unit>> = MutableLiveData()
    val launchFirebaseLoginUI: LiveData<Event<Unit>> = _launchFirebaseLoginUI

    private val _launchMain = MutableLiveData<Event<Unit>>()
    val launchMain: LiveData<Event<Unit>> = _launchMain

    private val _loginStatus = MutableLiveData<Resource<@StringRes Int>>()
    val loginStatus: LiveData<Resource<Int>> = _loginStatus

    override fun onCreate() { }

    fun onLoginClick() {
        if (checkInternetConnection())
            _launchFirebaseLoginUI.postValue(Event(Unit))
        else
            _loginStatus.postValue(Resource.error(R.string.network_not_available))
    }

    fun onLoginSuccess() {
        _loginStatus.postValue(Resource.loading(R.string.please_wait_text))
        _launchMain.postValue(Event(Unit))
    }

    fun onLoginFail() = _loginStatus.postValue(Resource.error(R.string.try_login_again_text))
}
