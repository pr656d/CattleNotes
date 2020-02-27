package com.pr656d.cattlenotes.ui.cattle.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.data.model.Cattle
import com.pr656d.cattlenotes.databinding.FragmentCattleDetailBinding
import com.pr656d.cattlenotes.shared.domain.result.EventObserver
import com.pr656d.cattlenotes.ui.NavigationFragment
import com.pr656d.cattlenotes.ui.cattle.detail.CattleDetailFragmentDirections.Companion.toActiveBreeding
import com.pr656d.cattlenotes.ui.cattle.detail.CattleDetailFragmentDirections.Companion.toAddBreeding
import com.pr656d.cattlenotes.ui.cattle.detail.CattleDetailFragmentDirections.Companion.toAddEditCattle
import com.pr656d.cattlenotes.ui.cattle.detail.CattleDetailFragmentDirections.Companion.toBreedingHistory
import com.pr656d.cattlenotes.utils.showDialog
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

        val cattle = Gson().fromJson(args.cattle, Cattle::class.java)
        model.fetchCattle(cattle)

        binding.bottomAppBarCattleDetail.setOnMenuItemClickListener {
             when (it.itemId) {
                R.id.menu_item_delete -> {
                    model.deleteCattleConfirmation()
                    true
                }
                else -> false
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.launchActiveBreeding.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(toActiveBreeding(Gson().toJson(it)))
        })

        model.launchAllBreeding.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(toBreedingHistory(Gson().toJson(it)))
        })

        model.launchAddBreeding.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(toAddBreeding(Gson().toJson(it)))
        })

        model.launchEditCattle.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(toAddEditCattle(Gson().toJson(it)))
        })

        model.launchDeleteConfirmation.observe(viewLifecycleOwner, EventObserver {
            requireContext().showDialog(
                title = R.string.delete_cattle_message,
                positiveTextId = R.string.yes,
                onPositiveSelected = { model.deleteCattle() },
                negativeTextId = R.string.no
            )
        })

        model.navigateUp.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigateUp()
        })

        model.showMessage.observe(viewLifecycleOwner) { resId ->
            Snackbar.make(requireView(), resId, Snackbar.LENGTH_LONG).show()
        }
    }
}