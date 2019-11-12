package com.pr656d.cattlenotes.shared.data

import androidx.lifecycle.MediatorLiveData
import com.pr656d.cattlenotes.model.Cattle
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Caching the data which is needed most for whole app.
 */
@Singleton
class CacheData @Inject constructor() {

    private val _cattleList = MediatorLiveData<List<Cattle>>()

    fun setCattleList(list: List<Cattle>) {_cattleList.postValue(list)}

    fun getCattleList(): List<Cattle>? = _cattleList.value
}