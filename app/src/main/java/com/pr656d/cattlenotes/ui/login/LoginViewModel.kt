package com.pr656d.cattlenotes.ui.login

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.ui.profile.ProfileDelegate
import com.pr656d.cattlenotes.ui.settings.theme.ThemedActivityDelegate
import com.pr656d.shared.domain.data.LoadDataUseCase
import com.pr656d.shared.domain.invoke
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
    private val setLoginCompletedUseCase: SetLoginCompletedUseCase,
    private val loadDataUseCase: LoadDataUseCase
) : ViewModel(),
    ProfileDelegate by profileDelegate,
    ThemedActivityDelegate by themedActivityDelegate {

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
    val loading: LiveData<Boolean> = _loading

    init {
        /**
         * User will be here for two reasons.
         *      1. If not logged in.
         *      2. If new user and not completed setup profile.
         *
         * Setup profile screen will be launched with 2nd scenario.
         */
        _launchSetupProfileScreen.addSource(getFirstTimeProfileSetupCompletedUseCase.invoke()) { result ->
            (result as? Result.Success)?.data?.let { isProfileSetupCompleted ->
                val isUserSignedIn = currentUserInfo.value?.isSignedIn() ?: false

                if (isUserSignedIn && !isProfileSetupCompleted)
                    _launchSetupProfileScreen.postValue(Event(Unit))
            }
        }

        /**
         * User will be here for two reasons.
         *      1. If not logged in.
         *      2. If new user and not completed setup profile.
         *
         * Login screen will be launched with 1st scenario.
         */
        _launchLoginScreen.addSource(currentUserInfo) { user ->
            if (user?.isSignedIn() == false)
                _launchLoginScreen.postValue(Event(Unit))
        }

        /**
         * User will be here for two reasons.
         *      1. If not logged in.
         *      2. If new user and not completed setup profile.
         *
         * After profile is updated, launch main screen. There is nothing to do anymore for login.
         */
        _launchMainScreen.addSource(updateUserInfoDetailedResult) { result ->
            _loading.postValue(true)

            (result as? Result.Success)?.data?.getContentIfNotHandled()?.let {
                if (it.first is Result.Success && it.second is Result.Success) {
                    setFirstTimeProfileSetupCompletedUseCase(true)
                    _launchMainScreen.postValue(Event(Unit))
                } else {
                    _loading.postValue(false)
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

    fun onLoginSuccess(isNewUser: Boolean) {
        setLoginCompletedUseCase(true)
        _loginStatus.postValue(R.string.login_complete)

        // New user
        if (isNewUser) {
            // Launch setup profile screen.
            _launchSetupProfileScreen.postValue(Event(Unit))
            return
        }

        // Existing user.
        loadDataUseCase.invoke()    // Start fetching data.
        setFirstTimeProfileSetupCompletedUseCase(true)
        _launchMainScreen.postValue(Event(Unit))
    }

    fun onLoginFail() {
        setLoginCompletedUseCase(false)
        _loginStatus.postValue(R.string.try_login_again_text)
        _logginIn.postValue(false)
    }
}