package com.pr656d.cattlenotes.ui.cattle.addedit.parent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.data.model.Cattle
import com.pr656d.cattlenotes.shared.domain.cattle.addedit.parent.GetParentListUseCase
import com.pr656d.cattlenotes.shared.domain.result.Event
import com.pr656d.cattlenotes.shared.domain.result.Result
import com.pr656d.cattlenotes.shared.domain.result.Result.Success
import javax.inject.Inject

class ParentListDialogViewModel @Inject constructor(
    private val getParentListUseCase: GetParentListUseCase
) : ViewModel(), ParentActionListener {

    private val parentListResult = MutableLiveData<Result<List<Cattle>>>()

    fun setTagNumber(value: Long) {
        getParentListUseCase(value, parentListResult)
    }

    private val _parentList = MediatorLiveData<List<Cattle>>()
    val parentList: LiveData<List<Cattle>> = _parentList

    private val _loading = MutableLiveData<Boolean>(true)
    val loading: LiveData<Boolean> = _loading

    private val _isEmpty = MediatorLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> = _isEmpty

    init {
        _parentList.addSource(parentListResult) {
            (it as? Success)?.data?.let { list ->
                _parentList.postValue(list)
            }
            _loading.postValue(false)
        }
        _isEmpty.addSource(parentList) {
            _isEmpty.value = it.isNullOrEmpty()
        }
    }

    private val _parentSelected = MutableLiveData<Event<Cattle>>()
    val parentSelected: LiveData<Event<Cattle>> = _parentSelected

    override fun parentSelected(cattle: Cattle) {
        _parentSelected.value = Event(cattle)
    }
}