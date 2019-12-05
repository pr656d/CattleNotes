package com.pr656d.cattlenotes.ui.main.timeline

import androidx.fragment.app.viewModels
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.shared.base.BaseFragment

class TimelineFragment : BaseFragment() {

    companion object {
        const val TAG = "TimelineFragment"
    }

    override fun provideLayoutId(): Int = R.layout.fragment_timeline

    override fun setupObservers() {
        val viewModel by viewModels<TimelineViewModel> { viewModelFactory }
    }

    override fun setupView() { }
}
