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
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.databinding.FragmentCattleDetailBinding
import com.pr656d.cattlenotes.ui.NavigationFragment
import com.pr656d.cattlenotes.ui.cattle.detail.CattleDetailFragmentDirections.Companion.toAddBreeding
import com.pr656d.cattlenotes.ui.cattle.detail.CattleDetailFragmentDirections.Companion.toAddEditCattle
import com.pr656d.cattlenotes.ui.cattle.detail.CattleDetailFragmentDirections.Companion.toBreedingHistory
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

        model.fetchCattle(args.cattleId)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
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

        bottomSheetBehavior =
            BottomSheetBehavior.from(view.findViewById(R.id.parent_detail_sheet) as View)

        bottomSheetBehavior.state = if (bottomSheetBehavior.skipCollapsed)
            STATE_HIDDEN
        else
            STATE_COLLAPSED

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
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
            model.showParentDetail()
        }

        model.showParentDetail.observe(viewLifecycleOwner, EventObserver {
            bottomSheetBehavior.state = STATE_EXPANDED
        })

        model.launchAllBreeding.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(toBreedingHistory(it.id))
        })

        model.launchAddBreeding.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(toAddBreeding(it.id))
        })

        model.launchEditCattle.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(toAddEditCattle(it.id))
        })

        model.launchDeleteConfirmation.observe(viewLifecycleOwner, EventObserver {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_cattle_title)
                .setMessage(R.string.delete_cattle_message)
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
            Snackbar
                .make(requireView(), it, Snackbar.LENGTH_LONG)
                .setAnchorView(binding.fabButtonEditCattle)
                .show()
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
            model.navigateUp()
            true
        }
    }
}