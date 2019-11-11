package com.pr656d.cattlenotes.ui.cattle

import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.shared.base.BaseFragment
import com.pr656d.cattlenotes.shared.utils.common.viewModelProvider
import kotlinx.android.synthetic.main.fragment_cattle.*

class CattleFragment : BaseFragment<CattleViewModel>() {

    companion object {
        const val TAG = "CattleFragment"

        const val KEY_CATTLE_TAG_NUMBER = "key_cattle_tag_number"
    }

    private var cattleAdapter: CattleAdapter

    init {
        cattleAdapter = CattleAdapter(object: CattleListClickListener {
            override fun onClick(tagNumber: String) {
                val action = CattleFragmentDirections.navigateToCattleDetail(tagNumber)
                findNavController().navigate(action)
            }
        })
    }

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_cattle

    override fun setupObservers() {
        super.setupObservers()

        viewModel.cattleList.observe(this, Observer {
            cattleAdapter.updateList(it)
        })
    }

    override fun setupView(view: View) {
        rvCattle.apply { adapter = cattleAdapter }
    }
}

interface CattleListClickListener {
    fun onClick(tagNumber: String)
}