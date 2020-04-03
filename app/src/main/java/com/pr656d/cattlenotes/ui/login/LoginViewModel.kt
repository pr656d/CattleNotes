package com.pr656d.cattlenotes.ui.login

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.ui.profile.ProfileDelegate
import com.pr656d.cattlenotes.ui.settings.theme.ThemedActivityDelegate
import com.pr656d.shared.domain.login.GetFirstTimeProfileSetupCompletedUseCase
import com.pr656d.shared.domain.login.SetFirstTimeProfileSetupCompletedUseCase
import com.pr656d.shared.domain.login.SetLoginCompletedUseCase
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result
import com.pr656d.shared.utils.NetworkHelper
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    profileDelegate: ProfileDelegate,
    themedActivityDelegate: ThemedActivityDelegate,
    getFirstTimeProfileSetupCompletedUseCase: GetFirstTimeProfileSetupCompletedUseCase,
    private val setFirstTimeProfileSetupCompletedUseCase: SetFirstTimeProfileSetupCompletedUseCase,
    private val networkHelper: NetworkHelper,
    private val setLoginCompletedUseCase: SetLoginCompletedUseCase
) : ViewModel(),
    ProfileDelegate by profileDelegate,
    ThemedActivityDelegate by themedActivityDelegate {

    private val getFirstTimeProfileSetupCompletedResult = MutableLiveData<Result<Boolean>>()

    private val _launchFirebaseLoginUI = MediatorLiveData<Event<Unit>>()
    val launchFirebaseAuthUI: LiveData<Event<Unit>> = _launchFirebaseLoginUI

    private val _loginStatus = MutableLiveData<@StringRes Int>(R.string.login_message)
    val loginStatus: LiveData<Int> = _loginStatus

    private val _logginIn = MutableLiveData<Boolean>(false)
    val logginIn: LiveData<Boolean> = _logginIn

    private val _launchSetupProfileScreen = MediatorLiveData<Event<Unit>>()
    val launchSetupProfileScreen: LiveData<Event<Unit>> = _launchSetupProfileScreen

    private val _launchLoginScreen = MediatorLiveData<Event<Unit>>()
    val launchLoginScreen: LiveData<Event<Unit>> = _launchLoginScreen

    private val _launchMainScreen = MediatorLiveData<Event<Unit>>()
    val launchMainScreen: LiveData<Event<Unit>> = _launchMainScreen


    private val _loading = MediatorLiveData<Boolean>().apply { value = true }
    val loading: LiveData<Boolean>
        get() = _loading

    /**
     * updateResultInfoDetailedResult Sends two result back
     *      1. For update on Firebase
     *      2. For update on Firestore
     *
     * Problem: Even activity gets finished on first result,
     *          for second result this runs and opens MainActivity again.
     *
     * TODO("Temporary fix : Provide more accurate result info of each update")
     *
     * To overcome this remember if already starting.
     */
    private val alreadyLaunchingMainActivity = MutableLiveData<Boolean>(false)

    init {
        getFirstTimeProfileSetupCompletedUseCase(Unit, getFirstTimeProfileSetupCompletedResult)

        _launchSetupProfileScreen.addSource(getFirstTimeProfileSetupCompletedResult) { result ->
            (result as? Result.Success)?.data?.let { isProfileSetupCompleted ->
                val isUserSignedIn = currentUserInfo.value?.isSignedIn() ?: false

                if (isUserSignedIn && !isProfileSetupCompleted)
                    _launchSetupProfileScreen.postValue(Event(Unit))
            }
        }

        _launchSetupProfileScreen.addSource(currentUserInfo) { user ->
            if (user?.isSignedIn() == true)
                _launchSetupProfileScreen.postValue(Event(Unit))
        }

        _launchLoginScreen.addSource(currentUserInfo) { user ->
            if (user?.isSignedIn() == false)
                _launchLoginScreen.postValue(Event(Unit))
        }

        _launchMainScreen.addSource(updateUserInfoDetailedResult) { result ->
            (result as? Result.Success)?.let {
                setFirstTimeProfileSetupCompletedUseCase(true)

                if (alreadyLaunchingMainActivity.value == false) {
                    alreadyLaunchingMainActivity.postValue(true)
                    _launchMainScreen.postValue(Event(Unit))
                }
            }
        }

        _loading.addSource(currentUserInfo) {
            _loading.postValue(false)
        }

        _loading.addSource(savingProfile) {
            _loading.postValue(it)
        }
    }

    fun onLoginClick() {
        if (networkHelper.isNetworkConnected()) {
            _logginIn.postValue(true)
            _loginStatus.postValue(R.string.logging_in)
            _launchFirebaseLoginUI.postValue(Event(Unit))
        } else {
            _loginStatus.postValue(R.string.network_not_available)
        }
    }

    fun onLoginSuccess() {
        setLoginCompletedUseCase(true)
        _loginStatus.postValue(R.string.please_wait_text)
        _launchSetupProfileScreen.postValue(Event(Unit))
    }

    fun onLoginFail() {
        setLoginCompletedUseCase(false)
        _loginStatus.postValue(R.string.try_login_again_text)
        _logginIn.postValue(false)
    }
}