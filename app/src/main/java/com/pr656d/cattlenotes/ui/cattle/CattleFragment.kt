package com.pr656d.cattlenotes.ui.cattle

import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.shared.base.BaseFragment
import com.pr656d.cattlenotes.shared.utils.common.viewModelProvider
import kotlinx.android.synthetic.main.fragment_cattle.*
import javax.inject.Inject

class CattleFragment : BaseFragment<CattleViewModel>() {

    companion object {
        const val TAG = "CattleFragment"
    }

    @Inject lateinit var cattleAdapter: CattleAdapter

    @Inject lateinit var linearLayoutManager: LinearLayoutManager

    override fun setupViewModel() {
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
        rvCattle.apply {
            adapter = cattleAdapter
            layoutManager = linearLayoutManager
        }
    }
}
