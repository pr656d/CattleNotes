package com.pr656d.cattlenotes.ui.main.cattle

import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.model.Cattle
import com.pr656d.cattlenotes.shared.base.BaseFragment
import com.pr656d.cattlenotes.shared.utils.common.viewModelProvider
import kotlinx.android.synthetic.main.fragment_cattle.*

class CattleFragment : BaseFragment<CattleListViewModel>() {

    companion object {
        const val TAG = "CattleFragment"
    }

    private lateinit var cattleAdapter: CattleListAdapter

    override fun init() {
        viewModel = viewModelProvider(viewModelFactory)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_cattle

    override fun setupObservers() {
        viewModel.cattleList.observe(this, Observer {
            cattleAdapter.updateList(it)
        })

        viewModel.isLoading.observe(this, Observer {
            progressBar.visibility = if (it) View.VISIBLE else View.GONE
        })
    }

    override fun setupView(view: View) {
        rvCattle.run {
            cattleAdapter = CattleListAdapter(object: CattleListClickListener {
                override fun onClick(cattle: Cattle) {
                    val action = CattleFragmentDirections.navigateToCattleActivity(cattle)
                    findNavController().navigate(action)
                }
            })
            adapter = cattleAdapter
        }
    }
}

interface CattleListClickListener {
    fun onClick(cattle: Cattle)
}