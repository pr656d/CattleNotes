package com.pr656d.cattlenotes.ui.milking

import android.view.View
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.shared.base.BaseFragment
import com.pr656d.cattlenotes.shared.utils.common.viewModelProvider

class MilkingFragment : BaseFragment<MilkingViewModel>() {

    companion object {
        const val TAG = "MilkingFragment"
    }

    override fun setupViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_milking

    override fun setupView(view: View) { }
}
