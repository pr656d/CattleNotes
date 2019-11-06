package com.pr656d.cattlenotes.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.ui.main.MainFragmentNavigation.CATTLE_FRAGMENT
import com.pr656d.cattlenotes.ui.main.MainFragmentNavigation.TIMELINE_FRAGMENT
import com.pr656d.cattlenotes.ui.main.MainFragmentNavigation.MILKING_FRAGMENT
import com.pr656d.cattlenotes.ui.main.MainFragmentNavigation.CASHFLOW_FRAGMENT
import com.pr656d.cattlenotes.utils.common.Event
import javax.inject.Inject

class MainViewModel @Inject constructor() : ViewModel() {

    private val _fragmentNavigation = MutableLiveData<Event<MainFragmentNavigation>>()
    val fragmentNavigation: LiveData<Event<MainFragmentNavigation>> = _fragmentNavigation

    init {
        _fragmentNavigation.postValue(Event(CATTLE_FRAGMENT))
    }

    fun onCattleSelected() =
        _fragmentNavigation.postValue(Event(CATTLE_FRAGMENT))

    fun onTimelineSelected() =
        _fragmentNavigation.postValue(Event(TIMELINE_FRAGMENT))

    fun onMilkingSelected() =
        _fragmentNavigation.postValue(Event(MILKING_FRAGMENT))

    fun onCashFlowSelected() =
        _fragmentNavigation.postValue(Event(CASHFLOW_FRAGMENT))
}

enum class MainFragmentNavigation {
    CATTLE_FRAGMENT,
    TIMELINE_FRAGMENT,
    MILKING_FRAGMENT,
    CASHFLOW_FRAGMENT
}