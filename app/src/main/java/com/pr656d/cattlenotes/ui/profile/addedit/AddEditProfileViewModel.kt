package com.pr656d.cattlenotes.ui.profile.addedit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
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

    /**
     * updateResultInfoDetailedResult Sends two result back
     *      1. For update on Firebase
     *      2. For update on Firestore
     *
     * Problem: Even activity gets finished on first result,
     *          for second result this runs and opens MainActivity again.
     *
     * TODO("Temporary fix : Provide more accurate result info of each update")
     *
     * To overcome this remember if already starting.
     */
    private val alreadyNavigatingUp = MutableLiveData<Boolean>(false)

    init {
        _navigateUp.addSource(updateUserInfoDetailedResult) { result ->
            (result as? Result.Success)?.data?.let {
                if (alreadyNavigatingUp.value == false) {
                    alreadyNavigatingUp.postValue(true)
                    navigateUp()
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

    fun navigateUp() {
        _navigateUp.postValue(Event(Unit))
    }
}