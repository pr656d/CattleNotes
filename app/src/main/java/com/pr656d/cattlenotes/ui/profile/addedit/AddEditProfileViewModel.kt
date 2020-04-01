package com.pr656d.cattlenotes.ui.profile.addedit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pr656d.shared.domain.result.Event
import javax.inject.Inject

class AddEditProfileViewModel @Inject constructor(

) : ViewModel() {

    private val _navigateUp = MutableLiveData<Event<Unit>>()
    val navigateUp: LiveData<Event<Unit>> = _navigateUp

    fun navigateUp() {
        _navigateUp.postValue(Event(Unit))
    }
}