package com.pr656d.cattlenotes.ui.main.cattle.breeding.base

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.text.InputType
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.shared.base.BaseFragment
import com.pr656d.cattlenotes.utils.common.parseToString
import kotlinx.android.synthetic.main.layout_breeding.*
import java.util.*

abstract class BaseBreedingFragment : BaseFragment() {

    abstract fun getBaseBreedingViewModel(): BaseBreedingViewModel

    override fun setupObservers() {
        getBaseBreedingViewModel().run {

        }
    }

    override fun setupView() {
        getBaseBreedingViewModel().run {
            switchActiveBreedingStatus.setOnCheckedChangeListener { _, isChecked ->

            }

            editTextAiDate.apply {
                setupForDateInput()
                addTextChangedListener {

                }
            }

            editTextRepeatHeatDateExpected.apply {
                setupForDateInput()
                addTextChangedListener {

                }
            }

            checkBoxRepeatHeatStatus.setOnCheckedChangeListener { _, isChecked ->

            }

            editTextRepeatHeatDateActual.apply {
                setupForDateInput()
                addTextChangedListener {

                }
            }

            editTextPregnancyCheckDateExpected.apply {
                setupForDateInput()
                addTextChangedListener {

                }
            }

            checkBoxPregnancyCheckStatus.setOnCheckedChangeListener { _, isChecked ->

            }

            editTextPregnancyCheckDateActual.apply {
                setupForDateInput()
                addTextChangedListener {

                }
            }

            editTextDryOffDateExpected.apply {
                setupForDateInput()
                addTextChangedListener {

                }
            }

            checkBoxDryOffStatus.setOnCheckedChangeListener { _, isChecked ->

            }

            editTextDryOffDateActual.apply {
                setupForDateInput()
                addTextChangedListener {

                }
            }

            editTextCalvingDateExpected.apply {
                setupForDateInput()
                addTextChangedListener {

                }
            }

            checkBoxCalvingStatus.setOnCheckedChangeListener { _, isChecked ->

            }

            editTextCalvingDateActual.apply {
                setupForDateInput()
                addTextChangedListener {

                }
            }
        }
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

            // Disable future date selection
            dialog.datePicker.maxDate = System.currentTimeMillis()

            dialog.show()   // Show the dialog.
        }
    }


    // Hide the soft keyboard if open.
    private fun hideKeyboard() =
        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(requireView().windowToken, 0)
}