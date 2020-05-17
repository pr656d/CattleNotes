/*
 * Copyright (c) 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.databinding.FragmentAddEditCattleBinding
import com.pr656d.cattlenotes.ui.NavigationFragment
import com.pr656d.cattlenotes.utils.focus
import com.pr656d.cattlenotes.utils.hideKeyboard
import com.pr656d.shared.domain.result.EventObserver
import org.threeten.bp.ZonedDateTime
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

        args.cattleId?.let { model.setCattle(it) }

        args.parentId?.let { model.setParent(it) }

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
                    // On drag it can be collapsed, hide if it is.
                    if (newState == STATE_COLLAPSED && bottomSheetBehavior.skipCollapsed)
                        bottomSheetBehavior.state = STATE_HIDDEN

                    val a11yState = if (newState == STATE_EXPANDED) {
                        View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
                    } else {
                        binding.editTextParent.isFocusableInTouchMode = false
                        View.IMPORTANT_FOR_ACCESSIBILITY_AUTO
                    }
                    binding.layoutContainer.importantForAccessibility = a11yState
                    binding.appBarLayout.importantForAccessibility = a11yState
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
                .setPositiveButton(R.string.go_back) { _, _ ->
                    model.onBackPressed(true)
                }
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show()
        })

        model.navigateUp.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigateUp()
        })

        model.showMessage.observe(viewLifecycleOwner, EventObserver {
            hideKeyboard(requireView())
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

/** Set helper text as [R.string.required] on [TextInputLayout] based on text provided. */
@BindingAdapter("setRequired")
fun setRequired(view: TextInputLayout, dateTime: ZonedDateTime?) {
    view.apply {
        if (dateTime == null) {
            isHelperTextEnabled = true
            helperText = context.getString(R.string.required)
        } else {
            isHelperTextEnabled = false
            helperText = null
        }
    }
}