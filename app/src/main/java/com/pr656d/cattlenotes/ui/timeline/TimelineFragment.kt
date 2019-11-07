package com.pr656d.cattlenotes.ui.timeline

import android.os.Bundle
import android.view.View
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.shared.utils.common.viewModelProvider
import com.pr656d.cattlenotes.ui.base.BaseFragment

class TimelineFragment : BaseFragment<TimelineViewModel>() {

    companion object {
        const val TAG = "TimelineFragment"

        fun newInstance(): TimelineFragment {
            val args = Bundle()
            val instance = TimelineFragment()
            instance.arguments = args
            return instance
        }
    }

    override fun setupViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_timeline

    override fun setupView(view: View) { }
}
