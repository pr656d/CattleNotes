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

    private val _cattleNavigation = MutableLiveData<Event<Unit>>()
    val cattleNavigation: LiveData<Event<Unit>> = _cattleNavigation

    private val _timelineNavigation = MutableLiveData<Event<Unit>>()
    val timelineNavigation: LiveData<Event<Unit>> = _timelineNavigation

    private val _milkingNavigation = MutableLiveData<Event<Unit>>()
    val milkingNavigation: LiveData<Event<Unit>> = _milkingNavigation

    private val _cashFlowNavigation = MutableLiveData<Event<Unit>>()
    val cashFlowNavigation: LiveData<Event<Unit>> = _cashFlowNavigation

    override fun onCreate() {
        _cattleNavigation.postValue(Event(Unit))
    }

    fun onCattleSelected() = _cattleNavigation.postValue(Event(Unit))

    fun onTimelineSelected() = _timelineNavigation.postValue(Event(Unit))

    fun onMilkingSelected() = _milkingNavigation.postValue(Event(Unit))

    fun onCashFlowSelected() = _cashFlowNavigation.postValue(Event(Unit))

}