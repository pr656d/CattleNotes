package com.pr656d.cattlenotes.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.pr656d.shared.domain.launch.SetLoginCompletedUseCase
import com.pr656d.shared.domain.login.ObservableLoginUseCase
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result.Success
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    observableLoginUseCase: ObservableLoginUseCase,
    private val setLoginCompletedUseCase: SetLoginCompletedUseCase
) : ViewModel() {
    val loginCompleted: LiveData<Boolean> = observableLoginUseCase.observe().map {
        (it as? Success)?.data ?: false
    }

    val firebaseUser = MutableLiveData<FirebaseUser?>(FirebaseAuth.getInstance().currentUser)

    private val _launchLoginScreen = MutableLiveData<Event<Unit>>()
    val launchLoginScreen: LiveData<Event<Unit>> = _launchLoginScreen

    private val _navigateUp = MutableLiveData<Event<Unit>>()
    val navigateUp: LiveData<Event<Unit>> = _navigateUp

    init {
        // Observe login changes
        observableLoginUseCase.execute(Unit)
    }

    fun onLoginClick() {
        _launchLoginScreen.value = Event(Unit)
    }

    fun navigateUp() {
        _navigateUp.postValue(Event(Unit))
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
        setLoginCompletedUseCase(false)
        navigateUp()
    }
}