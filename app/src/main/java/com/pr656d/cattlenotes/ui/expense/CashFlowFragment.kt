package com.pr656d.cattlenotes.ui.expense

import android.os.Bundle
import android.view.View
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.di.component.FragmentComponent
import com.pr656d.cattlenotes.ui.base.BaseFragment

class CashFlowFragment : BaseFragment<CashFlowViewModel>() {

    companion object {
        const val TAG = "CashFlowFragment"

        fun newInstance(): CashFlowFragment {
            val args = Bundle()
            val instance = CashFlowFragment()
            instance.arguments = args
            return instance
        }
    }

    override fun provideLayoutId(): Int = R.layout.fragment_cashflow

    override fun injectDependencies(fragmentComponent: FragmentComponent) =
        fragmentComponent.inject(this)

    override fun setupView(view: View) {

    }
}
