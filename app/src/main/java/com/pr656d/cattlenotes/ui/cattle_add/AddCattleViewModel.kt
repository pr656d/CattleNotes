package com.pr656d.cattlenotes.ui.cattle_add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pr656d.cattlenotes.data.repository.CattleDataRepository
import com.pr656d.cattlenotes.model.Cattle
import com.pr656d.cattlenotes.shared.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddCattleViewModel @Inject constructor(
    private val cattleDataRepository: CattleDataRepository
) : BaseViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun saveCattle(cattle: Cattle) {
        _loading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            cattleDataRepository.addCattle(cattle)
            withContext(Dispatchers.Main) {
                _loading.postValue(false)
            }
        }
    }
}