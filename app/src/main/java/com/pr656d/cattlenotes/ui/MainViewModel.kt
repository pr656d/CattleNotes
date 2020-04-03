package com.pr656d.cattlenotes.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.pr656d.cattlenotes.ui.settings.theme.ThemedActivityDelegate
import com.pr656d.shared.data.user.info.UserInfoBasic
import com.pr656d.shared.domain.auth.ObserveUserAuthStateUseCase
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result.Success
import javax.inject.Inject

class MainViewModel @Inject constructor(
    themedActivityDelegate: ThemedActivityDelegate,
    observeAuthStateUseCase: ObserveUserAuthStateUseCase
) : ViewModel(),
    ThemedActivityDelegate by themedActivityDelegate {

    private val _redirectToLoginScreen = MediatorLiveData<Event<Unit>>()
    val redirectToLoginScreen: LiveData<Event<Unit>> = _redirectToLoginScreen

    private val currentFirebaseUser: LiveData<UserInfoBasic?> =
        observeAuthStateUseCase.observe().map { result ->
            (result as? Success)?.data
        }

    init {
        observeAuthStateUseCase.execute(Any())

        _redirectToLoginScreen.addSource(currentFirebaseUser) {
            if (it?.isSignedIn() == false) _redirectToLoginScreen.postValue(Event(Unit))
        }
    }
}