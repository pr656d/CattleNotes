/*
 * Copyright 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pr656d.cattlenotes.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.pr656d.shared.domain.result.successOr
import com.pr656d.shared.utils.NetworkHelper
import kotlinx.coroutines.launch
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

    private val _loginStatus = MutableLiveData(R.string.login_message)
    val loginStatus: LiveData<Int> = _loginStatus

    private val _logginIn = MutableLiveData(false)
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
         * Setup profile screen will be launched with 2nd scenario.
         */
        _launchSetupProfileScreen.addSource(currentUserInfo) { user ->
            viewModelScope.launch {
                val isProfileSetupCompleted =
                    getFirstTimeProfileSetupCompletedUseCase().successOr(false)

                val isUserSignedIn = user?.isSignedIn() == true

                if (isUserSignedIn && !isProfileSetupCompleted)
                    _launchSetupProfileScreen.postValue(Event(Unit))
            }
        }

        _loading.addSource(currentUserInfo) {
            _loading.postValue(false)
        }

        _loading.addSource(savingProfile) {
            _loading.postValue(it)
        }
    }

    fun save() {
        viewModelScope.launch {
            val result = saveProfile()

            _loading.postValue(true)

            result.successOr(null)?.getContentIfNotHandled()?.let {
                if (it.first is Result.Success && it.second is Result.Success) {
                    setFirstTimeProfileSetupCompletedUseCase(true)
                    _launchMainScreen.postValue(Event(Unit))
                } else {
                    _loading.postValue(false)
                }
            }
        }
    }

    fun onLoginClick() {
        if (networkHelper.isNetworkConnected()) {
            _logginIn.value = true
            _loginStatus.value = R.string.logging_in
            _launchFirebaseLoginUI.value = Event(Unit)
        } else {
            _loginStatus.value = R.string.network_not_available
        }
    }

    fun onLoginSuccess(isNewUser: Boolean) {
        viewModelScope.launch { setLoginCompletedUseCase(true) }

        _loginStatus.value = R.string.login_complete

        // New user
        if (isNewUser) {
            // Launch setup profile screen.
            _launchSetupProfileScreen.value = Event(Unit)
            return
        }

        // Existing user.
        viewModelScope.launch {
            loadDataUseCase() // Start fetching data.
        }

        viewModelScope.launch {
            setFirstTimeProfileSetupCompletedUseCase(true)
        }

        _launchMainScreen.value = Event(Unit)
    }

    fun onLoginFail() {
        viewModelScope.launch { setLoginCompletedUseCase(false) }
        _loginStatus.value = R.string.try_login_again_text
        _logginIn.value = false
    }
}
