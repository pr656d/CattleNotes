package com.pr656d.cattlenotes.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import com.pr656d.cattlenotes.ui.base.BaseViewModel
import com.pr656d.cattlenotes.utils.common.Event
import com.pr656d.cattlenotes.utils.network.NetworkHelper
import io.reactivex.disposables.CompositeDisposable

class MainViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    enum class MainFragmentNavigation {
        CATTLE_FRAGMENT,
        TIMELINE_FRAGMENT,
        MILKING_FRAGMENT,
        CASHFLOW_FRAGMENT
    }

    private val _fragmentNavigation = MutableLiveData<Event<MainFragmentNavigation>>()
    val fragmentNavigation: LiveData<Event<MainFragmentNavigation>> = _fragmentNavigation

    override fun onCreate() {
        _fragmentNavigation.postValue(Event(MainFragmentNavigation.CATTLE_FRAGMENT))
    }

    fun onCattleSelected() =
        _fragmentNavigation.postValue(Event(MainFragmentNavigation.CATTLE_FRAGMENT))

    fun onTimelineSelected() =
        _fragmentNavigation.postValue(Event(MainFragmentNavigation.TIMELINE_FRAGMENT))

    fun onMilkingSelected() =
        _fragmentNavigation.postValue(Event(MainFragmentNavigation.MILKING_FRAGMENT))

    fun onCashFlowSelected() =
        _fragmentNavigation.postValue(Event(MainFragmentNavigation.CASHFLOW_FRAGMENT))

}