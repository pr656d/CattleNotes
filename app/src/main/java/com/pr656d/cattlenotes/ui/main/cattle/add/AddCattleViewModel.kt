package com.pr656d.cattlenotes.ui.main.cattle.add

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.pr656d.cattlenotes.data.repository.CattleDataRepository
import com.pr656d.cattlenotes.model.Cattle
import com.pr656d.cattlenotes.shared.base.BaseViewModel
import com.pr656d.cattlenotes.utils.common.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddCattleViewModel @Inject constructor(
    private val cattleDataRepository: CattleDataRepository
) : BaseViewModel() {

    private val _saving by lazy { MutableLiveData<Boolean>() }
    val saving = Transformations.map(_saving) { Event(it) }

    private val _launchCattleDetails by lazy { MutableLiveData<Cattle>() }
    val launchCattleDetails = Transformations.map(_launchCattleDetails) { Event(it) }

    fun saveCattle(cattle: Cattle) {
        _saving.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            cattleDataRepository.addCattle(cattle)
            withContext(Dispatchers.Main) {
                _saving.postValue(false)
                _launchCattleDetails.postValue(cattle)
            }
        }
    }
}