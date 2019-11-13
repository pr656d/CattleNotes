package com.pr656d.cattlenotes.ui.main

import com.pr656d.cattlenotes.shared.base.BaseViewModel
import com.pr656d.cattlenotes.shared.utils.network.NetworkHelper
import javax.inject.Inject

class MainViewModel @Inject constructor(
    networkHelper: NetworkHelper
) : BaseViewModel(networkHelper)