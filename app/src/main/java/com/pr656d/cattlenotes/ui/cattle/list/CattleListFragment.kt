package com.pr656d.cattlenotes.ui.cattle.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.pr656d.cattlenotes.databinding.FragmentCattleListBinding
import com.pr656d.cattlenotes.ui.NavigationFragment
import com.pr656d.cattlenotes.ui.cattle.list.CattleListFragmentDirections.Companion.toAddEditCattle
import com.pr656d.cattlenotes.ui.cattle.list.CattleListFragmentDirections.Companion.toCattleDetail
import com.pr656d.shared.domain.result.EventObserver
import javax.inject.Inject

class CattleListFragment : NavigationFragment() {

    companion object {
        const val TAG = "CattleListFragment"
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val model by viewModels<CattleListViewModel> { viewModelFactory }
    private lateinit var binding: FragmentCattleListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCattleListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = model

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.launchAddCattleScreen.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(toAddEditCattle())
        })

        model.launchCattleDetail.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(toCattleDetail(it.id))
        })
    }
}