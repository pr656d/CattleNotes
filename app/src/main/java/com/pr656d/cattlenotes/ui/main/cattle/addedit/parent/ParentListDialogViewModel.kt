package com.pr656d.cattlenotes.ui.main.cattle.addedit.parent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.pr656d.cattlenotes.data.model.Cattle
import com.pr656d.cattlenotes.data.repository.CattleDataRepository
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class ParentListDialogViewModel @Inject constructor(
    cattleDataRepository: CattleDataRepository
) : ViewModel() {

    private val _tagNumber = MutableLiveData<String>()

    private val _parentIsLoading = MutableLiveData<Boolean>(true)
    val parentIsLoading: LiveData<Boolean>
        get() = _parentIsLoading

    val parentList: LiveData<List<Cattle>> = cattleDataRepository.getAllCattle().map { list ->
        runBlocking {
            val filteredList = list.filter { cattle ->
                cattle.tagNumber != _tagNumber.value?.toLongOrNull()
            }
            _parentIsLoading.postValue(false)
            filteredList
        }
    }

    val isParentListEmpty: LiveData<Boolean> = parentList.map { it.isEmpty() }

    fun setTagNumber(value: String?) {
        _tagNumber.value = value
    }
}