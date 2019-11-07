package com.pr656d.cattlenotes.ui.milking

import android.os.Bundle
import android.view.View
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.shared.utils.common.viewModelProvider
import com.pr656d.cattlenotes.ui.base.BaseFragment

class MilkingFragment : BaseFragment<MilkingViewModel>() {

    companion object {
        const val TAG = "MilkingFragment"

        fun newInstance(): MilkingFragment {
            val args = Bundle()
            val instance = MilkingFragment()
            instance.arguments = args
            return instance
        }
    }

    override fun setupViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_milking

    override fun setupView(view: View) { }
}
