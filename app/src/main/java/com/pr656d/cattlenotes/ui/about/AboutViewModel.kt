package com.pr656d.cattlenotes.ui.about

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pr656d.shared.domain.result.Event
import javax.inject.Inject

class AboutViewModel @Inject constructor() : ViewModel() {

    private val _launchCredits = MutableLiveData<Event<Unit>>()
    val launchCredits: LiveData<Event<Unit>>
        get() = _launchCredits

    private val _launchOpenSourceLicense = MutableLiveData<Event<Unit>>()
    val launchOpenSourceLicense: LiveData<Event<Unit>>
        get() = _launchOpenSourceLicense

    fun openCredits() {
        _launchCredits.postValue(Event(Unit))
    }

    fun openOpenSourceLicense() {
        _launchOpenSourceLicense.postValue(Event(Unit))
    }

}