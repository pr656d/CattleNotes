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
import com.pr656d.cattlenotes.ui.main.cattle.detail.CattleDetailFragmentDirections.Companion.toAddEditCattle
import com.pr656d.cattlenotes.ui.main.cattle.detail.CattleDetailViewModel.Destination
import com.pr656d.cattlenotes.ui.main.cattle.detail.CattleDetailViewModel.Destination.DESTINATIONS.*
import com.pr656d.cattlenotes.utils.EventObserver
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class CattleDetailFragment : DaggerFragment() {

    companion object {
        const val TAG = "CattleDetailsFragment"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val args by navArgs<CattleDetailFragmentArgs>()
    private val model by viewModels<CattleDetailViewModel> { viewModelFactory }
    private lateinit var binding: FragmentCattleDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.fetchCattle(args.cattleTagNumber)
    }

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.action.observe(viewLifecycleOwner, EventObserver { performAction(it) })
    }

    private fun performAction(action: Destination) {
        when (action.destination) {
            ACTIVE_BREEDING -> {

            }
            ALL_BREEDING_SCREEN -> {

            }
            ADD_BREEDING_SCREEN -> {

            }
            EDIT_CATTLE_SCREEN -> {
                findNavController().navigate(toAddEditCattle(action.data as String))
            }
        }
    }
}