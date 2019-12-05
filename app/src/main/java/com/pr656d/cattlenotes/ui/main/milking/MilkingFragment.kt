package com.pr656d.cattlenotes.ui.main.milking

import androidx.fragment.app.viewModels
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.shared.base.BaseFragment

class MilkingFragment : BaseFragment() {

    companion object {
        const val TAG = "MilkingFragment"
    }

    override fun provideLayoutId(): Int = R.layout.fragment_milking

    override fun setupObservers() {
        val viewModel by viewModels<MilkingViewModel> { viewModelFactory }
    }

    override fun setupView() { }
}
