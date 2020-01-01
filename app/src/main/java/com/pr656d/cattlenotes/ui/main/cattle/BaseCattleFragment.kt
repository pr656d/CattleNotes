package com.pr656d.cattlenotes.ui.main.cattle

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.core.text.isDigitsOnly
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.shared.base.BaseFragment
import com.pr656d.cattlenotes.shared.utils.common.CattleValidator
import com.pr656d.cattlenotes.shared.utils.display.Toaster
import com.pr656d.cattlenotes.ui.main.cattle.add.AddCattleFragment
import com.pr656d.cattlenotes.ui.main.cattle.details.CattleDetailsFragment
import com.pr656d.cattlenotes.utils.common.EventObserver
import com.pr656d.cattlenotes.utils.common.parseToString
import kotlinx.android.synthetic.main.layout_cattle_details.*
import java.util.*

/**
 * Common abstract class for [CattleDetailsFragment] and [AddCattleFragment]
 * to reduce code repetition.
 */
abstract class BaseCattleFragment : BaseFragment() {

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
            val handleError: TextInputLayout.(messageId: Int) -> Unit = { messageId: Int ->
                isErrorEnabled = if (messageId != CattleValidator.VALID_MESSAGE_ID) {
                    error = getString(messageId)
                    true
                } else { false }
            }

            val setTextIfNotSame: EditText.(s: String?) -> Unit = { s: String? ->
                if (text?.toString() != s)
                    setText(s)
            }

            tagNumber.observe(viewLifecycleOwner) {
                editTextTagNumber.setTextIfNotSame(it)
            }

            showErrorOnTagNumber.observe(viewLifecycleOwner) {
                layoutTagNumber.handleError(it)
            }

            name.observe(viewLifecycleOwner) {
                editTextName.setTextIfNotSame(it)
            }

            type.observe(viewLifecycleOwner) {
                exposedDropDownType.setTextIfNotSame(it)
            }

            showErrorOnType.observe(viewLifecycleOwner) {
                layoutType.handleError(it)
            }

            breed.observe(viewLifecycleOwner) {
                exposedDropDownBreed.setTextIfNotSame(it)
            }

            showErrorOnBreed.observe(viewLifecycleOwner) {
                layoutBreed.handleError(it)
            }

            group.observe(viewLifecycleOwner) {
                exposedDropDownGroup.setTextIfNotSame(it)
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
                editTextParent.setTextIfNotSame(it)
            }

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

            purchaseDate.observe(viewLifecycleOwner) {
                editTextPurchaseDate.setTextIfNotSame(it)
            }

            saving.observe(viewLifecycleOwner) {
                if (it)
                    findNavController().navigate(
                        R.id.navigate_to_progress_dialog,
                        bundleOf("message" to getString(R.string.saving_dialog_message))
                    )
                else if (findNavController().currentDestination?.id == R.id.progressDialogScreen)
                    findNavController().navigateUp()
            }

            showMessage.observe(viewLifecycleOwner, EventObserver {
                Snackbar.make(requireView(), getString(it), Snackbar.LENGTH_SHORT)
                    .setAnchorView(getFabButtonId())
                    .show()
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
                    val dob = it.toString()
                    setDob(
                        if (dob.isNotBlank())
                            dob
                        else
                            null
                    )
                }
            }

            switchHomeBorn.setOnCheckedChangeListener { _, isChecked ->
                setHomeBorn(isChecked)
            }

            editTextPurchaseAmount.addTextChangedListener {
                val amount = it.toString()
                setPurchaseAmount(
                    if (amount.isNotBlank() && amount.isDigitsOnly())
                        amount.toLong()
                    else
                        null
                )
            }

            editTextPurchaseDate.apply {
                setupForDateInput()
                addTextChangedListener {
                    val date = it.toString()
                    setPurchaseDate(
                        if (date.isNotBlank())
                            date
                        else
                            null
                    )
                }
            }

            editTextParent.apply {
                // Set input type as null to stop keyboard from opening.
                inputType = InputType.TYPE_NULL
                isFocusableInTouchMode = false
                setOnClickListener {
                    hideKeyboard()
                    isFocusableInTouchMode = true
                    requestFocus()
                    Toaster.show(requireContext(), "select parent")
                }
                setOnLongClickListener {
                    hideKeyboard()
                    isFocusableInTouchMode = true
                    Toaster.show(requireContext(), "remove parent")
                    requestFocus()
                }
            }

            layoutParent.setEndIconOnClickListener {
                Toaster.show(requireContext(), "parent details")
            }
        }

        exposedDropDownBreed.setupDropDownAdapter(R.array.list_breed)

        exposedDropDownType.setupDropDownAdapter(R.array.list_type)

        exposedDropDownGroup.setupDropDownAdapter(R.array.list_group)
    }

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
                    setText(requireContext().parseToString(dd, mm, yyyy))
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
            dialog.show()   // Show the dialog.
        }
    }

    private fun hideKeyboard() {
        // Hide the soft keyboard if open.
        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(view?.windowToken, 0)
    }
}