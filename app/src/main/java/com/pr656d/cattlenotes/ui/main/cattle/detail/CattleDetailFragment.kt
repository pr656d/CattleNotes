package com.pr656d.cattlenotes.ui.main.cattle.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.pr656d.cattlenotes.databinding.FragmentCattleDetailBinding
import com.pr656d.cattlenotes.ui.main.NavigationFragment
import com.pr656d.cattlenotes.ui.main.cattle.detail.CattleDetailFragmentDirections.Companion.toActiveBreeding
import com.pr656d.cattlenotes.ui.main.cattle.detail.CattleDetailFragmentDirections.Companion.toAddBreeding
import com.pr656d.cattlenotes.ui.main.cattle.detail.CattleDetailFragmentDirections.Companion.toAddEditCattle
import com.pr656d.cattlenotes.ui.main.cattle.detail.CattleDetailFragmentDirections.Companion.toBreedingHistory
import com.pr656d.cattlenotes.ui.main.cattle.detail.CattleDetailViewModel.Destination
import com.pr656d.cattlenotes.ui.main.cattle.detail.CattleDetailViewModel.Destination.DESTINATIONS.*
import com.pr656d.cattlenotes.utils.EventObserver
import javax.inject.Inject

class CattleDetailFragment : NavigationFragment() {

    companion object {
        const val TAG = "CattleDetailsFragment"
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val args by navArgs<CattleDetailFragmentArgs>()
    private val model by viewModels<CattleDetailViewModel> { viewModelFactory }
    private lateinit var binding: FragmentCattleDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCattleDetailBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = model
        }

        model.fetchCattle(args.cattleTagNumber)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.action.observe(viewLifecycleOwner, EventObserver { performAction(it) })
    }

    private fun performAction(action: Destination) = with(action) {
        when (destination) {
            ACTIVE_BREEDING -> data?.let { findNavController().navigate(toActiveBreeding(it)) }

            ALL_BREEDING_SCREEN -> data?.let { findNavController().navigate(toBreedingHistory(it)) }

            ADD_BREEDING_SCREEN -> data?.let { findNavController().navigate(toAddBreeding(it)) }

            EDIT_CATTLE_SCREEN -> data?.let { findNavController().navigate(toAddEditCattle(it)) }
        }
    }
}