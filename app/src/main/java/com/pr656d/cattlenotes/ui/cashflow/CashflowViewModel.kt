package com.pr656d.cattlenotes.ui.cashflow

import com.pr656d.cattlenotes.shared.utils.network.NetworkHelper
import com.pr656d.cattlenotes.shared.base.BaseViewModel
import javax.inject.Inject

class CashflowViewModel @Inject constructor(
    networkHelper: NetworkHelper
) : BaseViewModel(networkHelper) {

    override fun onCreate() { }
}