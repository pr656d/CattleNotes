package com.pr656d.cattlenotes.ui.cashflow

import com.pr656d.cattlenotes.shared.base.BaseViewModel
import com.pr656d.cattlenotes.shared.utils.network.NetworkHelper
import javax.inject.Inject

class CashflowViewModel @Inject constructor(
    networkHelper: NetworkHelper
) : BaseViewModel(networkHelper)