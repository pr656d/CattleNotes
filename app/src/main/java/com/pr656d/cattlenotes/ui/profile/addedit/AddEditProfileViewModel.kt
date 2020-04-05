package com.pr656d.cattlenotes.ui.profile.addedit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.ui.profile.ProfileDelegate
import com.pr656d.shared.domain.result.Event
import com.pr656d.shared.domain.result.Result
import javax.inject.Inject

class AddEditProfileViewModel @Inject constructor(
    profileDelegate: ProfileDelegate
) : ViewModel(),
    ProfileDelegate by profileDelegate {

    private val _navigateUp = MediatorLiveData<Event<Unit>>()
    val navigateUp: LiveData<Event<Unit>> = _navigateUp

    private val _loading = MediatorLiveData<Boolean>().apply { value = true }
    val loading: LiveData<Boolean>
        get() = _loading

    init {
        _navigateUp.addSource(updateUserInfoDetailedResult) { result ->
            /**
             * [ProfileViewModel] and [AddEditProfileViewModel] uses same [ProfileDelegate] implementation.
             * So when user go back to profile after editing and goes to edit again.
             * Observers of [updateUserInfoDetailedResult] gets executed. So reset it after execution.
             */
            if (result == null)
                return@addSource    // Break the loop

            (result as? Result.Success)?.data?.let {
                if (it.first is Result.Success && it.second is Result.Success)
                    navigateUp()
            }

            // Reset it.
            updateUserInfoDetailedResult.postValue(null)
        }

        _loading.addSource(currentUserInfo) {
            _loading.postValue(false)
        }

        _loading.addSource(savingProfile) {
            _loading.postValue(it)
        }
    }

    fun navigateUp() {
        _navigateUp.postValue(Event(Unit))
    }
}