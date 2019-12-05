package com.pr656d.cattlenotes.ui.main.cattle.list

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.shared.base.BaseFragment
import com.pr656d.cattlenotes.ui.main.MainSharedViewModel
import com.pr656d.cattlenotes.utils.common.EventObserver
import kotlinx.android.synthetic.main.fragment_cattle_list.*

class CattleListFragment : BaseFragment() {

    companion object {
        const val TAG = "CattleFragment"
    }

    private lateinit var cattleAdapter: CattleListAdapter

    override fun provideLayoutId(): Int = R.layout.fragment_cattle_list

    override fun setupObservers() {
        val viewModel by viewModels<CattleListViewModel> { viewModelFactory }
        val mainSharedViewModel by activityViewModels<MainSharedViewModel> { viewModelFactory }

        mainSharedViewModel.refreshCattleList.observe(viewLifecycleOwner, EventObserver {
            viewModel.refreshCattleList()
        })

        viewModel.cattleList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                cattleAdapter.updateList(it)

                tvEmptyListMessage.visibility = View.GONE
                rvCattleList.visibility = View.VISIBLE
            } else {
                rvCattleList.visibility = View.GONE
                tvEmptyListMessage.visibility = View.VISIBLE
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            rvCattleList.visibility = if (it) View.GONE else View.VISIBLE
            progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    override fun setupView() {
        rvCattleList.run {
            cattleAdapter = CattleListAdapter()
            adapter = cattleAdapter
        }

        fabButtonAddCattle.setOnClickListener {
            val action = CattleListFragmentDirections.navigateToAddCattle()
            findNavController().navigate(action)
        }
    }
}