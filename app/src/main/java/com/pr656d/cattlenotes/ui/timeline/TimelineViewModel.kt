package com.pr656d.cattlenotes.ui.timeline

import com.pr656d.cattlenotes.shared.base.BaseViewModel
import com.pr656d.cattlenotes.shared.utils.network.NetworkHelper
import javax.inject.Inject

class TimelineViewModel @Inject constructor(
    networkHelper: NetworkHelper
) : BaseViewModel(networkHelper)