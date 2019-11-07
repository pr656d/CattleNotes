package com.pr656d.cattlenotes.ui.cattle

import com.pr656d.cattlenotes.shared.utils.network.NetworkHelper
import com.pr656d.cattlenotes.ui.base.BaseViewModel
import com.pr656d.cattlenotes.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class CattleViewModel @Inject constructor(
    networkHelper: NetworkHelper
) : BaseViewModel(networkHelper) {

    override fun onCreate() { }
}