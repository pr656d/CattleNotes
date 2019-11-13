package com.pr656d.cattlenotes.ui.cattle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pr656d.cattlenotes.data.repository.CattleDataRepository
import com.pr656d.cattlenotes.model.Cattle
import com.pr656d.cattlenotes.shared.base.BaseViewModel
import com.pr656d.cattlenotes.shared.data.CacheData
import com.pr656d.cattlenotes.shared.log.Logger
import com.pr656d.cattlenotes.shared.utils.network.NetworkHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CattleViewModel @Inject constructor(
    networkHelper: NetworkHelper,
    private val cattleDataRepository: CattleDataRepository,
    private val cacheData: CacheData
) : BaseViewModel(networkHelper) {

    private val _cattleList = MutableLiveData<List<Cattle>>()
    val cattleList: LiveData<List<Cattle>> = _cattleList

    private val _loading = MutableLiveData<Boolean>()
    val isLoading = _loading

    init {
        if (!checkCacheData()) fetchCattleList()
    }

    private fun checkCacheData(): Boolean {
        cacheData.getCattleList()?.let {
            _cattleList.postValue(it)
            return true
        }
        return false
    }

    private fun fetchCattleList() {
        _loading.postValue(true)
        viewModelScope.launch {
            val list = cattleDataRepository.getAllCattle()
            withContext(Dispatchers.Main) {
                _cattleList.postValue(list)
                cacheData.setCattleList(list)
                _loading.postValue(false)
                Logger.d(CattleFragment.TAG, "updated list: ${list.count()}")
            }
        }
    }
}