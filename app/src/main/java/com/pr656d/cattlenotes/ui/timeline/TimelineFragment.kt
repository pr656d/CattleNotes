package com.pr656d.cattlenotes.ui.timeline

import android.view.View
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.shared.base.BaseFragment
import com.pr656d.cattlenotes.shared.utils.common.viewModelProvider

class TimelineFragment : BaseFragment<TimelineViewModel>() {

    companion object {
        const val TAG = "TimelineFragment"
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_timeline

    override fun setupView(view: View) { }
}
