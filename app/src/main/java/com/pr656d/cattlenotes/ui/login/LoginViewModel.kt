package com.pr656d.cattlenotes.ui.login

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.shared.base.BaseViewModel
import com.pr656d.cattlenotes.shared.utils.network.NetworkHelper
import com.pr656d.cattlenotes.utils.common.Event
import com.pr656d.cattlenotes.utils.common.Resource
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val networkHelper: NetworkHelper
) : BaseViewModel() {

    private val _launchFirebaseLoginUI: MutableLiveData<Event<Unit>> = MutableLiveData()
    val launchFirebaseAuthUI: LiveData<Event<Unit>> = _launchFirebaseLoginUI

    private val _launchMain = MutableLiveData<Event<Unit>>()
    val launchMain: LiveData<Event<Unit>> = _launchMain

    private val _loginStatus = MutableLiveData<Resource<@StringRes Int>>()
    val loginStatus: LiveData<Resource<Int>> = _loginStatus

    fun onLoginClick() {
        if (networkHelper.isNetworkConnected())
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