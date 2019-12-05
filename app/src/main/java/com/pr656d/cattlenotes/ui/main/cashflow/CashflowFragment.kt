package com.pr656d.cattlenotes.ui.main.cashflow

import androidx.fragment.app.viewModels
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.shared.base.BaseFragment

class CashflowFragment : BaseFragment() {

    companion object {
        const val TAG = "CashflowFragment"
    }

    override fun provideLayoutId(): Int = R.layout.fragment_cashflow

    override fun setupObservers() {
        val viewModel by viewModels<CashflowViewModel> { viewModelFactory }
    }

    override fun setupView() { }
}
