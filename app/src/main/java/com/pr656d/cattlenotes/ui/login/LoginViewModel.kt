package com.pr656d.cattlenotes.ui.login

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.ui.settings.theme.ThemedActivityDelegate
import com.pr656d.shared.domain.launch.SetLoginCompletedUseCase
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.utils.NetworkHelper
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val networkHelper: NetworkHelper,
    private val setLoginCompletedUseCase: SetLoginCompletedUseCase,
    themedActivityDelegate: ThemedActivityDelegate
) : ViewModel(),
    ThemedActivityDelegate by themedActivityDelegate {

    private val _launchFirebaseLoginUI = MutableLiveData<Event<Unit>>()
    val launchFirebaseAuthUI: LiveData<Event<Unit>> = _launchFirebaseLoginUI

    private val _launchMain = MutableLiveData<Event<Unit>>()
    val launchMain: LiveData<Event<Unit>> = _launchMain

    private val _loginStatus = MutableLiveData<@StringRes Int>(R.string.login_message)
    val loginStatus: LiveData<Int> = _loginStatus

    private val _logginIn = MutableLiveData<Boolean>(false)
    val logginIn: LiveData<Boolean> = _logginIn

    fun onLoginClick() {
        if (networkHelper.isNetworkConnected()) {
            _logginIn.postValue(true)
            _launchFirebaseLoginUI.postValue(Event(Unit))
        } else {
            _loginStatus.postValue(R.string.network_not_available)
        }
    }

    fun onLoginSuccess() {
        setLoginCompletedUseCase(true)
        _loginStatus.postValue(R.string.please_wait_text)
        _launchMain.postValue(Event(Unit))
        _logginIn.postValue(false)
    }

    fun onLoginFail() {
        setLoginCompletedUseCase(false)
        _loginStatus.postValue(R.string.try_login_again_text)
        _logginIn.postValue(false)
    }
}