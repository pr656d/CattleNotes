package com.pr656d.cattlenotes.ui.cashflow

import android.os.Bundle
import android.view.View
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.shared.utils.common.viewModelProvider
import com.pr656d.cattlenotes.ui.base.BaseFragment

class CashflowFragment : BaseFragment<CashflowViewModel>() {

    companion object {
        const val TAG = "CashflowFragment"

        fun newInstance(): CashflowFragment {
            val args = Bundle()
            val instance = CashflowFragment()
            instance.arguments = args
            return instance
        }
    }

    override fun setupViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_cashflow

    override fun setupView(view: View) { }
}
