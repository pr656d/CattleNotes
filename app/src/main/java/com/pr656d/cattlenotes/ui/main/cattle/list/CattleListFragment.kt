package com.pr656d.cattlenotes.ui.main.cattle.list

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.shared.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_cattle_list.*

class CattleListFragment : BaseFragment() {

    companion object {
        const val TAG = "CattleFragment"
    }

    private lateinit var cattleAdapter: CattleListAdapter

    override fun provideLayoutId(): Int = R.layout.fragment_cattle_list

    override fun setupObservers() {
        val viewModel by viewModels<CattleListViewModel> { viewModelFactory }

        viewModel.cattleList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                cattleAdapter.updateList(it)

                progressBar.visibility = View.GONE
                tvEmptyListMessage.visibility = View.GONE
                rvCattleList.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.VISIBLE
                rvCattleList.visibility = View.GONE
                tvEmptyListMessage.visibility = View.VISIBLE
            }
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