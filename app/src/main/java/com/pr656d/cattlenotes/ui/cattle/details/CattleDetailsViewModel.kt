package com.pr656d.cattlenotes.ui.cattle.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pr656d.cattlenotes.data.repository.CattleDataRepository
import com.pr656d.cattlenotes.model.Cattle
import com.pr656d.cattlenotes.shared.base.BaseViewModel
import com.pr656d.cattlenotes.shared.utils.network.NetworkHelper
import com.pr656d.cattlenotes.utils.common.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CattleDetailsViewModel @Inject constructor(
    networkHelper: NetworkHelper,
    private val cattleDataRepository: CattleDataRepository
) : BaseViewModel(networkHelper) {

    private val _cattle = MutableLiveData<Event<Cattle>>()
    val cattle: LiveData<Event<Cattle>> = _cattle

    fun fetchCattle(tagNumber: String) {
        viewModelScope.launch {
            val cattle = cattleDataRepository.getCattle(tagNumber)
            withContext(Dispatchers.Main) {
                _cattle.postValue(Event(cattle))
            }
        }
    }
}