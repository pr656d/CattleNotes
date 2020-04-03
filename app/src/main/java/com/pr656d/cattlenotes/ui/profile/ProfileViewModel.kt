package com.pr656d.cattlenotes.ui.profile

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pr656d.shared.domain.result.Event
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    profileDelegate: ProfileDelegate
) : ViewModel(),
    ProfileDelegate by profileDelegate {

    private val _navigateUp = MutableLiveData<Event<Unit>>()
    val navigateUp: LiveData<Event<Unit>>
        get() = _navigateUp

    private val _launchEditProfile = MutableLiveData<Event<Unit>>()
    val launchEditProfile: LiveData<Event<Unit>>
        get() = _launchEditProfile

    private val _launchLogout = MutableLiveData<Event<Unit>>()
    val launchLogout: LiveData<Event<Unit>>
        get() = _launchLogout

    private val _showLogoutConfirmation = MutableLiveData<Event<Unit>>()
    val showLogoutConfirmation: LiveData<Event<Unit>>
        get() = _showLogoutConfirmation

    private val _loading = MediatorLiveData<Boolean>().apply { value = true }
    val loading: LiveData<Boolean>
        get() = _loading

    private val _showMessage = MediatorLiveData<Event<@StringRes Int>>()
    val showMessage: LiveData<Event<Int>>
        get() = _showMessage

    init {
        _loading.addSource(currentUserInfo) {
            _loading.postValue(false)
        }
    }

    fun navigateUp() {
        _navigateUp.postValue(Event(Unit))
    }

    fun logout(logoutConfirmation: Boolean = false) {
        if (logoutConfirmation)
            _launchLogout.postValue(Event(Unit))
        else
            showLogoutConfirmation()
    }

    fun editProfile() {
        _launchEditProfile.postValue(Event(Unit))
    }

    private fun showLogoutConfirmation() {
        _showLogoutConfirmation.postValue(Event(Unit))
    }
}