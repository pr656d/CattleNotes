package com.pr656d.cattlenotes.ui.cattle.addedit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.BindingAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.data.model.Cattle
import com.pr656d.cattlenotes.databinding.FragmentAddEditCattleBinding
import com.pr656d.cattlenotes.shared.domain.result.EventObserver
import com.pr656d.cattlenotes.ui.NavigationFragment
import com.pr656d.cattlenotes.utils.focus
import com.pr656d.cattlenotes.utils.hideKeyboard
import javax.inject.Inject

class AddEditCattleFragment : NavigationFragment() {

    companion object {
        const val TAG = "AddCattleFragment"
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val model by viewModels<AddEditCattleViewModel> { viewModelFactory }

    private lateinit var binding: FragmentAddEditCattleBinding

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    private val args by navArgs<AddEditCattleFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        args.cattle?.let {
            model.setCattle(Gson().fromJson(it, Cattle::class.java))
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            onBackPressed()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddEditCattleBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = model
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.editing.observe(viewLifecycleOwner) {
            if (it)
                binding.toolbar.setTitle(R.string.edit_cattle_details)
            else
                binding.toolbar.setTitle(R.string.add_cattle)
        }

        bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.parent_list_sheet) as View)

        bottomSheetBehavior.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    val state = if (newState == STATE_EXPANDED) {
                        View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
                    } else {
                        binding.editTextParent.isFocusableInTouchMode = false
                        View.IMPORTANT_FOR_ACCESSIBILITY_AUTO
                    }
                    binding.layoutContainer.importantForAccessibility = state
                    binding.appBarLayout.importantForAccessibility = state
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            }
        )

        model.selectingParent.observe(viewLifecycleOwner) {
            bottomSheetBehavior.state = if (it) {
                hideKeyboard(requireView())
                binding.editTextParent.focus()
                STATE_EXPANDED
            } else {
                STATE_HIDDEN
            }
        }

        model.showBackConfirmationDialog.observe(viewLifecycleOwner, EventObserver {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.back_pressed_message)
                .setMessage(R.string.changes_not_saved_message)
                .setPositiveButton(R.string.yes) { _, _ ->
                    model.onBackPressed(true)
                }
                .setNegativeButton(R.string.no, null)
                .create()
                .show()
        })

        model.navigateUp.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigateUp()
        })

        model.showMessage.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(requireView(), getString(it), Snackbar.LENGTH_SHORT).show()
        })
    }

    private fun onBackPressed(): Boolean {
        if (::bottomSheetBehavior.isInitialized && bottomSheetBehavior.state == STATE_EXPANDED) {
            // collapse or hide the sheet
            if (bottomSheetBehavior.isHideable && bottomSheetBehavior.skipCollapsed) {
                bottomSheetBehavior.state = STATE_HIDDEN
            } else {
                bottomSheetBehavior.state = STATE_COLLAPSED
            }
            return true
        }

        if(::bottomSheetBehavior.isInitialized && bottomSheetBehavior.state != STATE_EXPANDED) {
            model.onBackPressed()
            return true
        }

        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyboard(requireView())
    }
}

/** Set helper text as [R.string.required] on [TextInputLayout] based on text provided. */
@BindingAdapter("setRequired")
fun setRequired(view: TextInputLayout, text: String?) {
    view.apply {
        if (text.isNullOrBlank()) {
            isHelperTextEnabled = true
            helperText = context.getString(R.string.required)
        } else {
            isHelperTextEnabled = false
            helperText = null
        }
    }
}