package com.pr656d.cattlenotes.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.pr656d.cattlenotes.shared.domain.result.Event
import com.pr656d.cattlenotes.ui.settings.theme.ThemedActivityDelegate
import javax.inject.Inject

class MainViewModel @Inject constructor(
    firebaseUser: FirebaseUser?,
    themedActivityDelegate: ThemedActivityDelegate
) : ViewModel(),
    ThemedActivityDelegate by themedActivityDelegate {

    private val _redirectToLoginScreen by lazy { MutableLiveData<Event<Unit>>() }
    val redirectToLoginScreen: LiveData<Event<Unit>> = _redirectToLoginScreen

    init {
        if (firebaseUser == null) _redirectToLoginScreen.postValue(Event(Unit))
    }
}