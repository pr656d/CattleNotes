package com.pr656d.cattlenotes.ui.main.cattle.parent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.pr656d.cattlenotes.data.model.Cattle
import com.pr656d.cattlenotes.data.repository.CattleDataRepository
import com.pr656d.cattlenotes.shared.base.BaseViewModel
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class ParentListViewModel @Inject constructor(
    cattleDataRepository: CattleDataRepository
) : BaseViewModel() {

    private val _currentTagNumber by lazy { MutableLiveData<Long?>(null) }
    fun setCurrentTagNumber(tagNumber: Long?) = _currentTagNumber.postValue(tagNumber)

    val parentList: LiveData<List<Cattle>> = cattleDataRepository.getAllCattle().map { list ->
        runBlocking {
            list.filter { cattle -> cattle.tagNumber != _currentTagNumber.value }
        }
    }
}