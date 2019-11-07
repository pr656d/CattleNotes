package com.pr656d.cattlenotes.ui.cattle

import android.os.Bundle
import android.view.View
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.shared.utils.common.viewModelProvider
import com.pr656d.cattlenotes.ui.base.BaseFragment

class CattleFragment : BaseFragment<CattleViewModel>() {

    companion object {
        const val TAG = "CattleFragment"

        fun newInstance(): CattleFragment {
            val args = Bundle()
            val instance = CattleFragment()
            instance.arguments = args
            return instance
        }
    }

    override fun setupViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_cattle

    override fun setupView(view: View) { }
}
