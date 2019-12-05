package com.pr656d.cattlenotes.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.pr656d.cattlenotes.shared.base.BaseViewModel
import com.pr656d.cattlenotes.utils.common.Event
import javax.inject.Inject

class MainSharedViewModel @Inject constructor(

) : BaseViewModel() {

    private val _refreshCattleList by lazy { MutableLiveData<Unit>() }
    val refreshCattleList = Transformations.map(_refreshCattleList) { Event(it) }

    fun refreshCattleList() = _refreshCattleList.postValue(Unit)
}