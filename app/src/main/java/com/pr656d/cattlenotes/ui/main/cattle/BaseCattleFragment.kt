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
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.shared.base.BaseFragment
import com.pr656d.cattlenotes.shared.utils.common.CattleValidator
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

            showErrorOnTagNumber.observe(viewLifecycleOwner) {
                layoutTagNumber.handleError(it)
            }

            showErrorOnType.observe(viewLifecycleOwner) {
                layoutType.handleError(it)
            }

            showErrorOnTotalCalving.observe(viewLifecycleOwner) {
                layoutCalving.handleError(it)
            }

            saving.observe(viewLifecycleOwner) {
                if (it)
                    findNavController().navigate(
                        R.id.navigate_to_progress_dialog,
                        bundleOf("message" to R.string.saving_dialog_message)
                    )
                else if (findNavController().currentDestination?.id == R.id.progressDialogScreen)
                    findNavController().navigateUp()
            }

            showRetrySnackBar.observe(viewLifecycleOwner) {
                Snackbar.make(requireView(), getString(it), Snackbar.LENGTH_INDEFINITE)
                    .setAnchorView(getFabButtonId())
                    .setAction(R.string.retry) { saveCattle() }
                    .show()
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

            editTextCalving.addTextChangedListener {
                setTotalCalving(it.toString())
            }

            exposedDropDownBreed.addTextChangedListener {
                setBreed(it.toString())
            }

            exposedDropDownType.addTextChangedListener {
                setType(it.toString())
            }

            exposedDropDownGroup.addTextChangedListener {
                setGroup(it.toString())
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

            editTextDateOfBirth.apply {
                setupForDateInput()
                addTextChangedListener { this@run.setDob(it.toString()) }
            }

            editTextAiDate.apply {
                setupForDateInput()
                addTextChangedListener { this@run.setAiDate(it.toString()) }
            }

            editTextRepeatHeatDate.apply {
                setupForDateInput()
                addTextChangedListener { this@run.setRepeatHeatDate(it.toString()) }
            }

            editTextPregnancyCheckDate.apply {
                setupForDateInput()
                addTextChangedListener { this@run.setPregnancyCheckDate(it.toString()) }
            }

            editTextDryOffDate.apply {
                setupForDateInput()
                addTextChangedListener { this@run.setDryOffDate(it.toString()) }
            }

            editTextCalvingDate.apply {
                setupForDateInput()
                addTextChangedListener { this@run.setCalvingDate(it.toString()) }
            }

            editTextPurchaseDate.apply {
                setupForDateInput()
                addTextChangedListener { this@run.setPurchaseDate(it.toString()) }
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