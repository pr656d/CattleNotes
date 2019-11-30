package com.pr656d.cattlenotes.ui.main.cattle.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.pr656d.cattlenotes.data.repository.CattleDataRepository
import com.pr656d.cattlenotes.model.Cattle
import com.pr656d.cattlenotes.shared.base.BaseViewModel
import com.pr656d.cattlenotes.utils.common.Event
import kotlinx.coroutines.launch
import javax.inject.Inject

class CattleAddViewModel @Inject constructor(
    private val cattleDataRepository: CattleDataRepository
) : BaseViewModel() {

    private val _cattle =  MutableLiveData<Cattle>()
    val cattle: LiveData<Event<Cattle>> = Transformations.map(_cattle) { Event(it) }

    private val _loading = MutableLiveData<Boolean>()
    val loading:LiveData<Boolean> = _loading

    fun saveCattle(cattle: Cattle) {
        _loading.postValue(true)
        viewModelScope.launch {
            cattleDataRepository.updateCattle(cattle)
            _loading.postValue(false)
        }
    }
}