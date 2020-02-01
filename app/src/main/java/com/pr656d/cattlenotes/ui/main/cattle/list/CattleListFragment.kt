package com.pr656d.cattlenotes.ui.main.cattle.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.pr656d.cattlenotes.databinding.FragmentCattleListBinding
import com.pr656d.cattlenotes.ui.main.cattle.list.CattleListFragmentDirections.Companion.toAddEditCattle
import com.pr656d.cattlenotes.utils.EventObserver
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class CattleListFragment : DaggerFragment() {

    companion object {
        const val TAG = "CattleFragment"
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val model by viewModels<CattleListViewModel> { viewModelFactory }
    private lateinit var binding: FragmentCattleListBinding
    private val cattleAdapter = CattleListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCattleListBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = model
            rvCattleList.adapter = cattleAdapter
        }

        model.cattleList.observe(viewLifecycleOwner) {
            cattleAdapter.updateList(it)
        }

        model.launchAddCattleScreen.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(toAddEditCattle())
        })

        return binding.root
    }
}