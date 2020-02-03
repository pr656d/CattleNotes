package com.pr656d.cattlenotes.ui.main.cattle.addedit

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.addCallback
import androidx.annotation.ArrayRes
import androidx.databinding.BindingAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.databinding.FragmentAddEditCattleBinding
import com.pr656d.cattlenotes.ui.main.NavigationFragment
import com.pr656d.cattlenotes.ui.main.cattle.addedit.AddEditCattleFragmentDirections.Companion.toActiveBreeding
import com.pr656d.cattlenotes.ui.main.cattle.addedit.AddEditCattleFragmentDirections.Companion.toAddBreeding
import com.pr656d.cattlenotes.ui.main.cattle.addedit.AddEditCattleFragmentDirections.Companion.toBreedingHistory
import com.pr656d.cattlenotes.ui.main.cattle.addedit.AddEditCattleViewModel.Destination
import com.pr656d.cattlenotes.ui.main.cattle.addedit.AddEditCattleViewModel.Destination.DESTINATIONS.*
import com.pr656d.cattlenotes.ui.main.cattle.addedit.parent.ParentListDialogFragment
import com.pr656d.cattlenotes.utils.*
import javax.inject.Inject

class AddEditCattleFragment : NavigationFragment() {

    companion object {
        const val TAG = "AddCattleFragment"
        const val SELECT_PARENT_REQUEST_CODE = 0
        private const val EXTRA_PARENT_TAG_NUMBER = "extra_parent_tag_number"

        /**
         * Use only if you want to pass data back to this fragment by setting this fragment
         * as target fragment.
         */
        fun newIntent(tagNumber: String): Intent = Intent().apply {
            putExtra(EXTRA_PARENT_TAG_NUMBER, tagNumber)
        }
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val model by viewModels<AddEditCattleViewModel> { viewModelFactory }
    private lateinit var binding: FragmentAddEditCattleBinding
    private val args by navArgs<AddEditCattleFragmentArgs>()
    private val progressDialog by lazy {
        @Suppress("DEPRECATION")
        ProgressDialog(requireContext()).apply {
            setMessage(requireContext().getString(R.string.saving_dialog_message))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        args.cattleTagNumber?.let {
            model.fetchCattle(it)
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
            exposedDropDownBreed.setupDropDownAdapters(R.array.list_breed)
            exposedDropDownType.setupDropDownAdapters(R.array.list_type)
            exposedDropDownGroup.setupDropDownAdapters(R.array.list_group)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args.cattleTagNumber?.let { binding.toolbar.setTitle(R.string.edit_cattle_details) }

        model.action.observe(viewLifecycleOwner, EventObserver { performAction(it) })

        model.saving.observe(viewLifecycleOwner) {
            if (it)
                progressDialog.show()
            else
                progressDialog.hide()
        }

        model.showMessage.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(requireView(), getString(it), Snackbar.LENGTH_SHORT)
                .setAnchorView(binding.fabButtonSaveCattle.id)
                .show()
        })

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            model.onBackPressed()
        }
    }

    private fun performAction(action: Destination) = with(action) {
        when (destination) {
            PICK_DATE_OF_BIRTH -> selectDateOfBirth()
            REMOVE_DATE_OF_BIRTH -> {
                requireContext().showDialog(
                    title = R.string.remove_dob_message,
                    positiveTextId = R.string.yes,
                    onPositiveSelected = { model.setDob(null) },
                    negativeTextId = R.string.no
                )
            }

            PICK_PARENT -> data?.let { binding.editTextParent.pickParent(it) }
            REMOVE_PARENT -> {
                requireContext().showDialog(
                    title = R.string.remove_parent_message,
                    positiveTextId = R.string.yes,
                    onPositiveSelected = { model.setParent(null) },
                    negativeTextId = R.string.no
                )
            }

            PICK_PURCHASE_DATE -> selectPurchaseDate()
            REMOVE_PURCHASE_DATE -> {
                requireContext().showDialog(
                    title = R.string.remove_purchase_date_message,
                    positiveTextId = R.string.yes,
                    onPositiveSelected = { model.setPurchaseDate(null) },
                    negativeTextId = R.string.no
                )
            }

            ACTIVE_BREEDING -> data?.let { findNavController().navigate(toActiveBreeding(it)) }

            ALL_BREEDING_SCREEN -> data?.let { findNavController().navigate(toBreedingHistory(it)) }

            ADD_BREEDING_SCREEN -> data?.let { findNavController().navigate(toAddBreeding(it)) }

            BACK_CONFIRMATION_DIALOG -> {
                requireContext().showDialog(
                    title = R.string.back_pressed_message,
                    message = R.string.changes_not_saved_message,
                    positiveTextId = R.string.yes,
                    onPositiveSelected = { model.navigateUp() },
                    negativeTextId = R.string.no
                )
            }

            NAVIGATE_UP -> findNavController().navigateUp()
        }
    }

    private fun selectDateOfBirth() {
        binding.editTextDateOfBirth.apply {
            requireActivity().hideKeyboard(requireView())
            focus()
            pickADate { _, dd, mm, yyyy ->
                requireContext().toDate(dd, mm, yyyy)?.let {
                    model.setDob(it)
                }
                // After picking date reset focusable to false.
                isFocusableInTouchMode = false
            }
        }
    }

    private fun selectPurchaseDate() {
        binding.editTextPurchaseDate.apply {
            requireActivity().hideKeyboard(requireView())
            focus()
            pickADate { _, dd, mm, yyyy ->
                requireContext().toDate(dd, mm, yyyy)?.let {
                    model.setPurchaseDate(it)
                }
                // After picking date reset focusable to false.
                isFocusableInTouchMode = false
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SELECT_PARENT_REQUEST_CODE) {
            when(resultCode) {
                Activity.RESULT_OK -> {
                    data?.extras?.getString(EXTRA_PARENT_TAG_NUMBER)?.let {
                        model.setParent(it)
                    }
                }
            }
            binding.editTextParent.isFocusableInTouchMode = false
        }
    }

    private fun TextInputEditText.pickParent(tagNumber: String) {
        requireActivity().hideKeyboard(requireView())
        focus()

        ParentListDialogFragment.newInstance(tagNumber)
            .apply {
                setTargetFragment(this@AddEditCattleFragment, SELECT_PARENT_REQUEST_CODE)
            }
            .show(parentFragmentManager, TAG)
    }

    private fun AutoCompleteTextView.setupDropDownAdapters(@ArrayRes listId: Int) {
        val stringArray: Array<String> = resources.getStringArray(listId)

        setAdapter(
            ArrayAdapter<String>(
                requireContext(),
                R.layout.dropdown_menu_popup_item,
                stringArray
            )
        )

        setOnItemClickListener { _, _, position, _ ->
            when(listId) {
                R.array.list_type -> model.setType(stringArray[position])
                R.array.list_breed -> model.setBreed(stringArray[position])
                R.array.list_group -> model.setGroup(stringArray[position])
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().hideKeyboard(requireView())
    }
}

/**
 * Removes text if boolean is true.
 */
@BindingAdapter("resetText")
fun resetText(view: TextInputEditText, value: Boolean) {
    if (value) {
        view.text = null
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

/**
 * Set text on [AutoCompleteTextView] which is used in [TextInputLayout] for dropdown menu.
 * Default [AutoCompleteTextView.setText] method filters so dropdown doesn't show all options
 * when pre set option is needed.
 * pass false for filter while setting text which will not filter and shows all options of dropdown.
 */
@BindingAdapter("dropDownText")
fun dropDownText(view: AutoCompleteTextView, text: String?) {
    view.apply {
        if (text.isNullOrEmpty()) {
            setText(null, false)
        } else {
            setText(text, false)
        }
    }
}