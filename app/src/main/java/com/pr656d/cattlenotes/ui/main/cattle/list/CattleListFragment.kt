package com.pr656d.cattlenotes.ui.main.cattle.list

import android.view.View
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.shared.base.BaseFragment
import com.pr656d.cattlenotes.shared.utils.common.viewModelProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_cattle_list.*

class CattleListFragment : BaseFragment<CattleListViewModel>() {

    companion object {
        const val TAG = "CattleFragment"
    }

    private lateinit var cattleAdapter: CattleListAdapter

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_cattle_list

    override fun setupObservers() {
        viewModel.cattleList.observe(viewLifecycleOwner) {
            cattleAdapter.updateList(it)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    override fun setupView() {
        requireActivity().appBarLayout.elevation = 0f

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