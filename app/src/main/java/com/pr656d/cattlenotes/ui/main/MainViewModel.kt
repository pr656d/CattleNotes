package com.pr656d.cattlenotes.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.firebase.auth.FirebaseUser
import com.pr656d.cattlenotes.shared.base.BaseViewModel
import com.pr656d.cattlenotes.utils.common.Event
import javax.inject.Inject

class MainViewModel @Inject constructor(
    firebaseUser: FirebaseUser?
) : BaseViewModel() {

    private val _redirectToLoginScreen by lazy { MutableLiveData<Unit>() }
    val redirectToLoginScreen = Transformations.map(_redirectToLoginScreen) { Event(it) }

    init {
        if (firebaseUser == null)
            _redirectToLoginScreen.value = Unit
    }
}