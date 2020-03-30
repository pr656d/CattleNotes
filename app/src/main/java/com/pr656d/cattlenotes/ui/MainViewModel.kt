package com.pr656d.cattlenotes.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.pr656d.cattlenotes.ui.settings.theme.ThemedActivityDelegate
import com.pr656d.shared.domain.login.ObservableLoginUseCase
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result.Success
import javax.inject.Inject

class MainViewModel @Inject constructor(
    themedActivityDelegate: ThemedActivityDelegate,
    observableLoginUseCase: ObservableLoginUseCase
) : ViewModel(),
    ThemedActivityDelegate by themedActivityDelegate {

    private val _redirectToLoginScreen = MediatorLiveData<Event<Unit>>()
    val redirectToLoginScreen: LiveData<Event<Unit>> = _redirectToLoginScreen

    private val loginCompleted: LiveData<Boolean> = observableLoginUseCase.observe().map { result ->
        (result as? Success)?.data ?: false
    }

    init {
        observableLoginUseCase.execute(Unit)

        _redirectToLoginScreen.addSource(loginCompleted) {
            if (!it) _redirectToLoginScreen.postValue(Event(Unit))
        }
    }
}