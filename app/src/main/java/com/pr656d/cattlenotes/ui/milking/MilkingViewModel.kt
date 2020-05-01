package com.pr656d.cattlenotes.ui.milking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pr656d.shared.domain.result.Event
import javax.inject.Inject

class MilkingViewModel @Inject constructor(

) : ViewModel() {

    private val _permissionsGranted = MutableLiveData<Boolean>()
    val permissionsGranted: LiveData<Boolean>
        get() = _permissionsGranted

    private val _requestPermissions = MutableLiveData<Event<Unit>>()
    val requestPermissions: LiveData<Event<Unit>>
        get() = _requestPermissions

    private val _showPermissionExplanation = MutableLiveData<Event<Unit>>()
    val showPermissionExplanation: LiveData<Event<Unit>>
        get() = _showPermissionExplanation

    fun setPermissionsGranted(isGranted: Boolean) {
        _permissionsGranted.postValue(isGranted)
    }

    fun requestPermission() = _requestPermissions.postValue(Event(Unit))

    fun showPermissionExplanation() = _showPermissionExplanation.postValue(Event(Unit))
}