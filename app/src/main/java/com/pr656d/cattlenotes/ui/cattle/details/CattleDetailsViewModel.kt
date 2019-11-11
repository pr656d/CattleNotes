package com.pr656d.cattlenotes.ui.cattle.details

import com.pr656d.cattlenotes.shared.base.BaseViewModel
import com.pr656d.cattlenotes.shared.utils.network.NetworkHelper
import javax.inject.Inject

class CattleDetailsViewModel @Inject constructor(
    networkHelper: NetworkHelper
) : BaseViewModel(networkHelper) {

    override fun onCreate() {}
}