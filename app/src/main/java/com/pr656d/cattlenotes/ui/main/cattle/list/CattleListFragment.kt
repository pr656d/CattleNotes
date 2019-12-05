package com.pr656d.cattlenotes.ui.main.cattle.list

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.shared.base.BaseFragment
import com.pr656d.cattlenotes.shared.log.Logger
import com.pr656d.cattlenotes.shared.utils.common.viewModelProvider
import com.pr656d.cattlenotes.ui.main.MainSharedViewModel
import com.pr656d.cattlenotes.utils.common.EventObserver
import kotlinx.android.synthetic.main.fragment_cattle_list.*

class CattleListFragment : BaseFragment<CattleListViewModel>() {

    companion object {
        const val TAG = "CattleFragment"
    }

    val mainSharedViewModel by activityViewModels<MainSharedViewModel> { viewModelFactory }

    private lateinit var cattleAdapter: CattleListAdapter

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_cattle_list

    override fun setupObservers() {
        viewModel.cattleList.observe(viewLifecycleOwner) {
            Logger.d(TAG, "updateList() : ${it.size}")
            cattleAdapter.updateList(it)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            rvCattle.visibility = if (it) View.GONE else View.VISIBLE
            progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        mainSharedViewModel.refreshCattleList.observe(viewLifecycleOwner, EventObserver {
            viewModel.refreshCattleList()
        })
    }

    override fun setupView() {
        rvCattle.run {
            cattleAdapter = CattleListAdapter()
            adapter = cattleAdapter
        }

        fabButtonAddCattle.setOnClickListener {
            val action = CattleListFragmentDirections.navigateToAddCattle()
            findNavController().navigate(action)
        }
    }
}