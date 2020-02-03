package com.pr656d.cattlenotes.ui.main.cattle.addedit.parent

import androidx.lifecycle.*
import com.pr656d.cattlenotes.data.model.Cattle
import com.pr656d.cattlenotes.data.repository.CattleDataRepository
import com.pr656d.cattlenotes.utils.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class ParentListDialogViewModel @Inject constructor(
    cattleDataRepository: CattleDataRepository
) : ViewModel(), ParentActionListener {

    private val _tagNumber = MutableLiveData<String>()

    fun setTagNumber(value: String) { _tagNumber.value = value }

    private val allCattle: LiveData<List<Cattle>> = _tagNumber.map {
        cattleDataRepository.getAllCattle().value ?: emptyList()
    }

    private val _parentList = MediatorLiveData<List<Cattle>>()
    val parentList: LiveData<List<Cattle>> = _parentList

    private val _loading = MutableLiveData<Boolean>(true)
    val loading: LiveData<Boolean> = _loading

    private val _isEmpty = MediatorLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> = _isEmpty

    init {
        _parentList.addSource(allCattle) {
            _parentList.value = runBlocking(Dispatchers.IO) {
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


    override fun parentSelected(cattle: Cattle) {
        _parentSelected.value = Event(cattle)
    }
}