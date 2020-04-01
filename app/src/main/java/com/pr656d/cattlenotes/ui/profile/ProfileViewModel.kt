package com.pr656d.cattlenotes.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.pr656d.shared.data.signin.UserInfoDetailed
import com.pr656d.shared.domain.profile.ObserveGetProfileInfoUseCase
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result.Success
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    observeGetProfileInfoUseCase: ObserveGetProfileInfoUseCase
) : ViewModel() {

    val currentUserInfo: LiveData<UserInfoDetailed?>

    private val _navigateUp = MutableLiveData<Event<Unit>>()
    val navigateUp: LiveData<Event<Unit>>
        get() = _navigateUp

    private val _launchEditProfile = MutableLiveData<Event<Unit>>()
    val launchEditProfile: LiveData<Event<Unit>>
        get() = _launchEditProfile

    private val _launchLogout = MutableLiveData<Event<Unit>>()
    val launchLogout: LiveData<Event<Unit>>
        get() = _launchLogout

    init {
        currentUserInfo = observeGetProfileInfoUseCase.observe().map { result ->
            (result as? Success)?.data
        }

        observeGetProfileInfoUseCase.execute(Unit)
    }

    fun navigateUp() {
        _navigateUp.postValue(Event(Unit))
    }

    fun logout() {
        _launchLogout.postValue(Event(Unit))
    }

    fun editProfile() {
        _launchEditProfile.postValue(Event(Unit))
    }
}