package com.pr656d.cattlenotes.ui.milking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.shared.utils.common.inflate
import com.pr656d.cattlenotes.shared.utils.common.viewModelProvider
import com.pr656d.cattlenotes.ui.cattle.CattleFragment
import com.pr656d.cattlenotes.ui.cattle.CattleViewModel
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class MilkingFragment : DaggerFragment() {

    companion object {
        const val TAG = "MilkingFragment"

        fun newInstance(): MilkingFragment {
            val args = Bundle()
            val instance = MilkingFragment()
            instance.arguments = args
            return instance
        }
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject lateinit var viewModel: MilkingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = viewModelProvider(viewModelFactory)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return container?.inflate(R.layout.fragment_milking)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Attach click listeners here
    }

}
