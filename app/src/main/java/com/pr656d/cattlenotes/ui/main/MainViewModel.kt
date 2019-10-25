package com.pr656d.cattlenotes.ui.main

import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import com.pr656d.cattlenotes.ui.base.BaseViewModel
import com.pr656d.cattlenotes.utils.network.NetworkHelper
import io.reactivex.disposables.CompositeDisposable

class MainViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    override fun onCreate() {}
}