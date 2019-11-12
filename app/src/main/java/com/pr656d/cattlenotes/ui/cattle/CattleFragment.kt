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
    }

    private lateinit var cattleAdapter: CattleAdapter

    override fun init() {
        viewModel = viewModelProvider(viewModelFactory)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_cattle

    override fun setupObservers() {
        super.setupObservers()

        viewModel.cattleList.observe(this, Observer {
            cattleAdapter.updateList(it)
        })

        viewModel.isLoading.observe(this, Observer {
            progressBar.visibility = if (it) View.VISIBLE else View.GONE
        })
    }

    override fun setupView(view: View) {
        rvCattle.run {
            cattleAdapter = CattleAdapter(object: CattleListClickListener {
                override fun onClick(tagNumber: String) {
                    val action = CattleFragmentDirections.navigateToCattleDetail(tagNumber)
                    findNavController().navigate(action)
                }
            })
            adapter = cattleAdapter
        }
    }
}

interface CattleListClickListener {
    fun onClick(tagNumber: String)
}