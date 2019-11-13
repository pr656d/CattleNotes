package com.pr656d.cattlenotes.ui.milking

import com.pr656d.cattlenotes.shared.base.BaseViewModel
import com.pr656d.cattlenotes.shared.utils.network.NetworkHelper
import javax.inject.Inject

class MilkingViewModel @Inject constructor(
    networkHelper: NetworkHelper
) : BaseViewModel(networkHelper)