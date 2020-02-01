package com.pr656d.cattlenotes.ui.main.cattle.addedit.parent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.data.model.Cattle
import com.pr656d.cattlenotes.data.repository.CattleDataRepository
import com.pr656d.cattlenotes.utils.Event
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class ParentListDialogViewModel @Inject constructor(
    cattleDataRepository: CattleDataRepository
) : ViewModel(), ParentActionListener {

    val list = cattleDataRepository.getAllCattle()

    private val _tagNumber = MutableLiveData<String>()

    private val _loading = MutableLiveData<Boolean>(true)
    val loading: LiveData<Boolean> = _loading

    private val _parentList = MediatorLiveData<List<Cattle>>()
    val parentList: LiveData<List<Cattle>> = _parentList

    private val _isEmpty = MediatorLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> = _isEmpty

    init {
        _parentList.addSource(list) {
            _parentList.value = runBlocking {
                val result = it.filter { cattle ->
                    cattle.tagNumber != _tagNumber.value!!.toLongOrNull()
                }
                _loading.postValue(false)
                result
            }
        }
        _isEmpty.addSource(_parentList) {
            _isEmpty.value = it.isNullOrEmpty()
        }
    }

    private val _parentSelected = MutableLiveData<Event<Cattle>>()
    val parentSelected: LiveData<Event<Cattle>> = _parentSelected

    fun setTagNumber(value: String?) {
        _tagNumber.value = value
    }

    override fun parentSelected(cattle: Cattle) {
        _parentSelected.value = Event(cattle)
    }
}