package com.pr656d.cattlenotes.ui.main.cashflow

import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.shared.base.BaseFragment
import com.pr656d.cattlenotes.shared.utils.common.viewModelProvider

class CashflowFragment : BaseFragment<CashflowViewModel>() {

    companion object {
        const val TAG = "CashflowFragment"
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_cashflow

    override fun setupObservers() { }

    override fun setupView() { }
}
