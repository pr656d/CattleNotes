package com.pr656d.cattlenotes.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pr656d.cattlenotes.shared.base.BaseViewModel
import com.pr656d.cattlenotes.shared.utils.network.NetworkHelper
import com.pr656d.cattlenotes.ui.main.MainFragmentNavigation.*
import com.pr656d.cattlenotes.utils.common.Event
import javax.inject.Inject

class MainViewModel @Inject constructor(
    networkHelper: NetworkHelper
) : BaseViewModel(networkHelper) {

    private val _fragmentNavigation = MutableLiveData<Event<MainFragmentNavigation>>()
    val fragmentNavigation: LiveData<Event<MainFragmentNavigation>> = _fragmentNavigation

    override fun onCreate() {
        _fragmentNavigation.postValue(Event(CATTLE_FRAGMENT))
    }

    fun onCattleSelected() = _fragmentNavigation.postValue(Event(CATTLE_FRAGMENT))

    fun onTimelineSelected() = _fragmentNavigation.postValue(Event(TIMELINE_FRAGMENT))

    fun onMilkingSelected() = _fragmentNavigation.postValue(Event(MILKING_FRAGMENT))

    fun onCashFlowSelected() = _fragmentNavigation.postValue(Event(CASHFLOW_FRAGMENT))
}

enum class MainFragmentNavigation {
    CATTLE_FRAGMENT,
    TIMELINE_FRAGMENT,
    MILKING_FRAGMENT,
    CASHFLOW_FRAGMENT
}