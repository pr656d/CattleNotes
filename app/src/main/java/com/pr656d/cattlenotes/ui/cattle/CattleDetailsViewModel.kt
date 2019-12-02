package com.pr656d.cattlenotes.ui.cattle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.pr656d.cattlenotes.data.repository.CattleDataRepository
import com.pr656d.cattlenotes.model.Cattle
import com.pr656d.cattlenotes.shared.base.BaseViewModel
import com.pr656d.cattlenotes.utils.common.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class CattleDetailsViewModel @Inject constructor(
    private val cattleDataRepository: CattleDataRepository
) : BaseViewModel() {

    private val _editMode = MutableLiveData<Boolean>(false)
    val editMode: LiveData<Event<Boolean>> = Transformations.map(_editMode) { Event(it) }

    private val _cattle =  MutableLiveData<Cattle>()
    val cattle: LiveData<Event<Cattle>> = Transformations.map(_cattle) { Event(it) }

    fun isInEditMode(): Boolean = _editMode.value!!

    fun changeMode() {
        if (_editMode.value!!)
            _editMode.postValue(false)
        else
            _editMode.postValue(true)
    }

    fun setCattle(cattle: Cattle) {
        _cattle.postValue(cattle)
    }

    fun saveCattle(cattle: Cattle) {
        viewModelScope.launch(Dispatchers.IO) {
            cattleDataRepository.updateCattle(cattle)
        }
    }
}