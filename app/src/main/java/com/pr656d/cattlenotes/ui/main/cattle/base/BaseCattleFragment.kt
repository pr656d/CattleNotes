package com.pr656d.cattlenotes.ui.main.cattle.base

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.shared.base.BaseFragment
import com.pr656d.cattlenotes.shared.utils.common.CattleValidator
import com.pr656d.cattlenotes.ui.main.cattle.addedit.AddEditCattleFragment
import com.pr656d.cattlenotes.ui.main.cattle.details.CattleDetailsFragment
import com.pr656d.cattlenotes.ui.main.cattle.details.CattleDetailsFragmentDirections
import com.pr656d.cattlenotes.utils.EventObserver
import kotlinx.android.synthetic.main.layout_cattle_details.*
import java.util.*

/**
 * Common abstract class for [CattleDetailsFragment] and [AddEditCattleFragment]
 * to reduce code repetition.
 */
abstract class BaseCattleFragment : BaseFragment() {

    companion object {
        const val TAG = "BaseCattleFragment"
    }

//    @Inject lateinit var selectParentDialog: ParentListDialogFragment

    abstract fun getBaseCattleViewModel(): BaseCattleViewModel

    /**
     * Get fab button id to anchor [Snackbar] to it.
     */
    abstract fun getFabButtonId(): Int

    protected val setupDropDownAdapter: AutoCompleteTextView.(listId: Int) -> Unit = { listId ->
        setAdapter(
            ArrayAdapter<String>(
                requireContext(),
                R.layout.dropdown_menu_popup_item,
                resources.getStringArray(listId)
            )
        )
    }

    override fun setupObservers() {
        getBaseCattleViewModel().run {
            fun TextInputLayout.handleError(@StringRes messageId: Int) {
                isErrorEnabled = if (messageId != CattleValidator.VALID_FIELD) {
                    error = getString(messageId)
                    true
                } else {
                    false
                }
            }

            fun EditText.setTextIfNotSame(s: String?) {
                if (!(text?.toString().isNullOrEmpty() && s.isNullOrEmpty())) {
                    if (text.toString() != s) {
                        setText(s)
                    }
                }
            }

            tagNumber.observe(viewLifecycleOwner) {
                editTextTagNumber.setTextIfNotSame(it?.toString())
            }

            showErrorOnTagNumber.observe(viewLifecycleOwner) {
                layoutTagNumber.handleError(it)
            }

            name.observe(viewLifecycleOwner) {
                editTextName.setTextIfNotSame(it)
            }

            type.observe(viewLifecycleOwner) {
                exposedDropDownType.apply {
                    setTextIfNotSame(it)
                    setupDropDownAdapter(R.array.list_type)
                }
            }

            showErrorOnType.observe(viewLifecycleOwner) {
                layoutType.handleError(it)
            }

            breed.observe(viewLifecycleOwner) {
                exposedDropDownBreed.apply {
                    setTextIfNotSame(it)
                    setupDropDownAdapter(R.array.list_breed)
                }
            }

            showErrorOnBreed.observe(viewLifecycleOwner) {
                layoutBreed.handleError(it)
            }

            group.observe(viewLifecycleOwner) {
                exposedDropDownGroup.apply {
                    setTextIfNotSame(it)
                    setupDropDownAdapter(R.array.list_group)
                }
            }

            showErrorOnGroup.observe(viewLifecycleOwner) {
                layoutGroup.handleError(it)
            }

            lactation.observe(viewLifecycleOwner) {
                editTextLactation.setTextIfNotSame(it)
            }

            showErrorOnLactation.observe(viewLifecycleOwner) {
                layoutLactation.handleError(it)
            }

            dob.observe(viewLifecycleOwner) {
                editTextDateOfBirth.setTextIfNotSame(it)
            }

            parent.observe(viewLifecycleOwner) {
                editTextParent.setTextIfNotSame(it?.toString())
            }

            showSelectParentScreen.observe(viewLifecycleOwner,
                EventObserver {
//                    selectParentDialog.setTargetFragment(this@BaseCattleFragment, 0)
//                    selectParentDialog.show(requireFragmentManager(), TAG)
                })

            showRemoveParentScreen.observe(viewLifecycleOwner,
                EventObserver {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.remove_parent_message)
                        .setPositiveButton(R.string.yes) { _, which ->
                            if (which == AlertDialog.BUTTON_POSITIVE) {
                                setParent(null)
                            }
                        }
                        .setNegativeButton(R.string.no, null)
                        .create()
                        .show()
                })

            launchParentDetailsScreen.observe(viewLifecycleOwner,
                EventObserver {

                })

            homeBorn.observe(viewLifecycleOwner) {
                switchHomeBorn.apply {
                    if (isChecked != it)
                        isChecked = it
                }
                containerPurchase.visibility = if (it) {
                    editTextPurchaseAmount.text = null
                    editTextPurchaseDate.text = null
                    View.GONE
                } else {
                    View.VISIBLE
                }
            }

            purchaseAmount.observe(viewLifecycleOwner) {
                editTextPurchaseAmount.setTextIfNotSame(it?.toString())
            }

            showErrorOnPurchaseAmount.observe(viewLifecycleOwner) {
                layoutPurchaseAmount.handleError(it)
            }

            purchaseDate.observe(viewLifecycleOwner) {
                editTextPurchaseDate.setTextIfNotSame(it)
            }

            launchActiveBreedingScreen.observe(viewLifecycleOwner,
                EventObserver {
                    val action =
                        CattleDetailsFragmentDirections.navigateToActiveBreeding(it.toString())
                    findNavController().navigate(action)
                })

            launchAddBreedingScreen.observe(viewLifecycleOwner,
                EventObserver {
                    val action = CattleDetailsFragmentDirections.navigateToAddBreeding()
                    findNavController().navigate(action)
                })

            launchBreedingHistoryScreen.observe(viewLifecycleOwner,
                EventObserver {
                    val action = CattleDetailsFragmentDirections.navigateToBreedingHistory()
                    findNavController().navigate(action)
                })

            showBackPressedScreen.observe(viewLifecycleOwner,
                EventObserver {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.back_pressed_message)
                        .setMessage(R.string.changes_not_saved_message)
                        .setPositiveButton(R.string.back) { _, which ->
                            if (which == AlertDialog.BUTTON_POSITIVE) {
                                findNavController().navigateUp()
                            }
                        }
                        .setNegativeButton(R.string.stay, null)
                        .create()
                        .show()
                })

            saving.observe(viewLifecycleOwner) {
//                if (it)
//                    findNavController().navigate(
//                        R.id.navigate_to_progress_dialog,
//                        bundleOf(getString(R.string.arg_progressDialogMessage) to getString(R.string.saving_dialog_message))
//                    )
//                else if (findNavController().currentDestination?.id == R.id.progressDialogScreen) {
//                    findNavController().navigateUp()
//                }
            }

            showMessage.observe(viewLifecycleOwner,
                EventObserver {
                    Snackbar.make(requireView(), getString(it), Snackbar.LENGTH_SHORT)
                        .setAnchorView(getFabButtonId())
                        .show()
                })

            navigateUp.observe(viewLifecycleOwner,
                EventObserver {
                    findNavController().navigateUp()
                })
        }
    }

    override fun setupView() {
        getBaseCattleViewModel().run {
            editTextTagNumber.addTextChangedListener {
                setTagNumber(it.toString())
            }

            editTextName.addTextChangedListener {
                setName(it.toString())
            }

            exposedDropDownType.addTextChangedListener {
                setType(it.toString())
            }

            exposedDropDownBreed.addTextChangedListener {
                setBreed(it.toString())
            }

            exposedDropDownGroup.addTextChangedListener {
                setGroup(it.toString())
            }

            editTextLactation.addTextChangedListener {
                setLactation(it.toString())
            }

            editTextDateOfBirth.apply {
                setupForDateInput()
                addTextChangedListener {
                    setDob(it.toString())
                }
            }

            switchHomeBorn.setOnCheckedChangeListener { _, isChecked ->
                setHomeBorn(isChecked)
            }

            editTextPurchaseAmount.addTextChangedListener {
                setPurchaseAmount(it.toString())
            }

            editTextPurchaseDate.apply {
                setupForDateInput()
                addTextChangedListener {
                    setPurchaseDate(it.toString())
                }
            }

            editTextParent.apply {
                // Set input type as null to stop keyboard from opening.
                inputType = InputType.TYPE_NULL

                isFocusableInTouchMode = false  // Initially set to false

                setOnClickListener {
                    hideKeyboard()

                    // Set focusable to true.
                    isFocusableInTouchMode = true
                    // Take view in focus.
                    requestFocus()

                    showSelectParentScreen()
                }

                setOnLongClickListener {
                    hideKeyboard()

                    // Set focusable to true.
                    isFocusableInTouchMode = true
                    // Take view in focus.
                    requestFocus()

                    showRemoveParentScreen()

                    true
                }
            }

            layoutParent.setEndIconOnClickListener {
                showParentDetailsScreen()
            }

            btnShowActiveBreeding.setOnClickListener {
                launchActiveBreeding()
            }

            btnAddNewBreeding.setOnClickListener {
                launchAddBreedingScreen()
            }

            btnShowAllBreeding.setOnClickListener {
                launchBreedingHistoryScreen()
            }
        }

        exposedDropDownBreed.setupDropDownAdapter(R.array.list_breed)

        exposedDropDownType.setupDropDownAdapter(R.array.list_type)

        exposedDropDownGroup.setupDropDownAdapter(R.array.list_group)
    }

//    override fun onParentSelected(parentTagNumber: String) {
//        getBaseCattleViewModel().setParent(parentTagNumber)
//        editTextParent.isFocusableInTouchMode = false
//        selectParentDialog.dismiss()
//    }
//
//    override fun parentDialogCancelled() {
//        editTextParent.isFocusableInTouchMode = false
//        selectParentDialog.dismiss()
//    }
//
//    override fun provideCurrentTagNumber(): Long? = getBaseCattleViewModel().tagNumber.value

    /**
     * To match view theme dates shows as [TextInputEditText].
     *
     * focus is handled manually for date editTexts.
     */
    private fun TextInputEditText.setupForDateInput() {
        // Set input type as null to stop keyboard from opening.
        inputType = InputType.TYPE_NULL

        isFocusableInTouchMode = false  // Initially set to false

        setOnClickListener {
            hideKeyboard()

            // Set focusable to true.
            isFocusableInTouchMode = true
            // Take view in focus.
            requestFocus()

            // Create date picker dialog.
            val dialog = DatePickerDialog(
                context, R.style.DatePicker,
                DatePickerDialog.OnDateSetListener { _, yyyy, mm, dd ->
//                    setText(requireContext().parseToString(dd, mm, yyyy))
                    // After picking date reset focusable to false.
                    isFocusableInTouchMode = false
                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            )

            // Cancel button click handler.
            dialog.setButton(
                DialogInterface.BUTTON_NEGATIVE,
                getString(R.string.cancel)
            ) { _, which ->
                // Reset focus to false.
                if (which == DialogInterface.BUTTON_NEGATIVE)
                    isFocusableInTouchMode = false
            }

            // Disable future date selection
            dialog.datePicker.maxDate = System.currentTimeMillis()

            dialog.show()   // Show the dialog.
        }
    }

    // Hide the soft keyboard if open.
    private fun hideKeyboard() =
        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(requireView().windowToken, 0)

    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyboard()
    }
}