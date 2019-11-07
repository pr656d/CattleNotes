package com.pr656d.cattlenotes.ui.timeline

import com.pr656d.cattlenotes.shared.utils.network.NetworkHelper
import com.pr656d.cattlenotes.ui.base.BaseViewModel
import javax.inject.Inject

class TimelineViewModel @Inject constructor(
    networkHelper: NetworkHelper
) : BaseViewModel(networkHelper) {

    override fun onCreate() { }
}