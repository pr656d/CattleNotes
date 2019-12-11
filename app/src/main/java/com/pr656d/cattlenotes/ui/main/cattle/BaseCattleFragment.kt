package com.pr656d.cattlenotes.ui.main.cattle

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.text.InputType
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.os.bundleOf
import androidx.core.text.isDigitsOnly
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.shared.base.BaseFragment
import com.pr656d.cattlenotes.shared.utils.common.CattleValidator
import com.pr656d.cattlenotes.ui.main.MainSharedViewModel
import com.pr656d.cattlenotes.utils.common.parseToString
import kotlinx.android.synthetic.main.layout_cattle_details.*
import java.util.*

abstract class BaseCattleFragment : BaseFragment() {

    abstract fun getMainSharedViewModel(): MainSharedViewModel

    abstract fun getBaseCattleViewModel(): BaseCattleViewModel

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
        getBaseCattleViewModel().refreshCattleListScreen.observe(viewLifecycleOwner) {
            getMainSharedViewModel().refreshCattleList()
        }

        val handleError: TextInputLayout.(messageId: Int) -> Unit = { messageId: Int ->
            isErrorEnabled = if (messageId != CattleValidator.VALID_MESSAGE_ID) {
                error = getString(messageId)
                true
            } else { false }
        }

        getBaseCattleViewModel().showErrorOnTagNumber.observe(viewLifecycleOwner) {
            layoutTagNumber.handleError(it)
        }

        getBaseCattleViewModel().showErrorOnType.observe(viewLifecycleOwner) {
            layoutType.handleError(it)
        }

        getBaseCattleViewModel().showErrorOnTotalCalving.observe(viewLifecycleOwner) {
            layoutCalving.handleError(it)
        }

        getBaseCattleViewModel().saving.observe(viewLifecycleOwner) {
            if (it)
                findNavController().navigate(
                    R.id.navigate_to_progress_dialog,
                    bundleOf("message" to R.string.saving_dialog_message)
                )
            else if (findNavController().currentDestination?.id == R.id.progressDialogScreen)
                findNavController().navigateUp()
        }
    }

    override fun setupView() {
        editTextTagNumber.addTextChangedListener {
            getBaseCattleViewModel().setTagNumber(it.toString())
        }

        editTextName.addTextChangedListener {
            getBaseCattleViewModel().setName(it.toString())
        }

        editTextCalving.addTextChangedListener {
            getBaseCattleViewModel().setTotalCalving(it.toString())
        }

        exposedDropDownBreed.addTextChangedListener {
            getBaseCattleViewModel().setBreed(it.toString())
        }

        exposedDropDownType.addTextChangedListener {
            getBaseCattleViewModel().setType(it.toString())
        }

        exposedDropDownGroup.addTextChangedListener {
            getBaseCattleViewModel().setGroup(it.toString())
        }

        editTextPurchaseAmount.addTextChangedListener {
            val amount = it.toString()
            getBaseCattleViewModel().setPurchaseAmount(
                if (amount.isNotBlank() && amount.isDigitsOnly())
                    amount.toLong()
                else
                    null
            )
        }

        editTextDateOfBirth.apply {
            setupForDateInput()
            addTextChangedListener { getBaseCattleViewModel().setDob(it.toString()) }
        }

        editTextAiDate.apply {
            setupForDateInput()
            addTextChangedListener { getBaseCattleViewModel().setAiDate(it.toString()) }
        }

        editTextRepeatHeatDate.apply {
            setupForDateInput()
            addTextChangedListener { getBaseCattleViewModel().setRepeatHeatDate(it.toString()) }
        }

        editTextPregnancyCheckDate.apply {
            setupForDateInput()
            addTextChangedListener { getBaseCattleViewModel().setPregnancyCheckDate(it.toString()) }
        }

        editTextDryOffDate.apply {
            setupForDateInput()
            addTextChangedListener { getBaseCattleViewModel().setDryOffDate(it.toString()) }
        }

        editTextCalvingDate.apply {
            setupForDateInput()
            addTextChangedListener { getBaseCattleViewModel().setCalvingDate(it.toString()) }
        }

        editTextPurchaseDate.apply {
            setupForDateInput()
            addTextChangedListener { getBaseCattleViewModel().setPurchaseDate(it.toString()) }
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
            // Hide the soft keyboard if open.
            (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(view?.windowToken, 0)

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
}