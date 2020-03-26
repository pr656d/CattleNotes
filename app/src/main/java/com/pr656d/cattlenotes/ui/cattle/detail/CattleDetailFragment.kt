package com.pr656d.cattlenotes.ui.cattle.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.databinding.FragmentCattleDetailBinding
import com.pr656d.cattlenotes.ui.NavigationFragment
import com.pr656d.cattlenotes.ui.cattle.detail.CattleDetailFragmentDirections.Companion.toActiveBreeding
import com.pr656d.cattlenotes.ui.cattle.detail.CattleDetailFragmentDirections.Companion.toAddBreeding
import com.pr656d.cattlenotes.ui.cattle.detail.CattleDetailFragmentDirections.Companion.toAddEditCattle
import com.pr656d.cattlenotes.ui.cattle.detail.CattleDetailFragmentDirections.Companion.toBreedingHistory
import com.pr656d.model.Cattle
import com.pr656d.shared.domain.result.EventObserver
import javax.inject.Inject

class CattleDetailFragment : NavigationFragment() {

    companion object {
        const val TAG = "CattleDetailsFragment"
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val model by viewModels<CattleDetailViewModel> { viewModelFactory }

    private lateinit var binding: FragmentCattleDetailBinding

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    private val args by navArgs<CattleDetailFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback {
            onBackPressed()
        }
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

        val cattle = Gson().fromJson(args.cattle, Cattle::class.java)
        model.fetchCattle(cattle)

        binding.bottomAppBarCattleDetail.setOnMenuItemClickListener {
             when (it.itemId) {
                R.id.menu_item_delete -> {
                    model.deleteCattle()
                    true
                }
                else -> false
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.parent_detail_sheet) as View)

        bottomSheetBehavior.state = if (bottomSheetBehavior.skipCollapsed)
            STATE_HIDDEN
        else
            STATE_COLLAPSED

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // On drag it can be collapsed, hide if it is.
                if (newState == STATE_COLLAPSED && bottomSheetBehavior.skipCollapsed)
                    bottomSheetBehavior.state = STATE_HIDDEN

                val state = if (newState == STATE_EXPANDED) {
                    View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
                } else {
                    View.IMPORTANT_FOR_ACCESSIBILITY_AUTO
                }
                binding.layoutContainer.importantForAccessibility = state
                binding.appBarLayout.importantForAccessibility = state
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.includeCattleDetail.layoutParent.setEndIconOnClickListener {
            if (model.parentCattle.value != null)
                bottomSheetBehavior.state = STATE_EXPANDED
        }

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
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_cattle)
                .setPositiveButton(R.string.delete) { _, _ ->
                    model.deleteCattle(deleteConfirmation = true)
                }
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show()
        })

        model.navigateUp.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigateUp()
        })

        model.showMessage.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG).show()
        })
    }

    private fun onBackPressed(): Boolean {
        return if (::bottomSheetBehavior.isInitialized && bottomSheetBehavior.state == STATE_EXPANDED) {
            // collapse or hide the sheet
            if (bottomSheetBehavior.isHideable && bottomSheetBehavior.skipCollapsed) {
                bottomSheetBehavior.state = STATE_HIDDEN
            } else {
                bottomSheetBehavior.state = STATE_COLLAPSED
            }
            true
        } else {
            findNavController().navigateUp()
            true
        }
    }
}