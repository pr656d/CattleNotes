package com.pr656d.cattlenotes.ui.main.cattle.add

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.text.InputType
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.core.text.isDigitsOnly
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.shared.base.BaseFragment
import com.pr656d.cattlenotes.shared.utils.common.CattleValidator
import com.pr656d.cattlenotes.ui.main.MainSharedViewModel
import com.pr656d.cattlenotes.utils.common.EventObserver
import com.pr656d.cattlenotes.utils.common.parseToString
import kotlinx.android.synthetic.main.fragment_add_cattle.*
import kotlinx.android.synthetic.main.layout_cattle_details.*
import java.util.*

class AddCattleFragment : BaseFragment() {

    companion object {
        const val TAG = "AddCattleFragment"
    }

    private val viewModel by viewModels<AddCattleViewModel> { viewModelFactory }

    override fun provideLayoutId(): Int = R.layout.fragment_add_cattle

    override fun setupObservers() {
        val mainSharedViewModel by activityViewModels<MainSharedViewModel> { viewModelFactory }
        viewModel.refreshCattleListScreen.observe(viewLifecycleOwner) {
            mainSharedViewModel.refreshCattleList()
        }

        viewModel.navigateUp.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigateUp()
        })

        val handleError: TextInputLayout.(messageId: Int) -> Unit = { messageId: Int ->
            isErrorEnabled = if (messageId != CattleValidator.VALID_MESSAGE_ID) {
                error = getString(messageId)
                true
            } else { false }
        }

        viewModel.showErrorOnTagNumber.observe(viewLifecycleOwner) {
            layoutTagNumber.handleError(it)
        }

        viewModel.showErrorOnType.observe(viewLifecycleOwner) {
            layoutType.handleError(it)
        }

        viewModel.showErrorOnTotalCalving.observe(viewLifecycleOwner) {
            layoutCalving.handleError(it)
        }

        viewModel.saving.observe(viewLifecycleOwner) {
            if (it) {
                val action = AddCattleFragmentDirections.navigateToProgressDialog(R.string.please_wait_text)
                findNavController().navigate(action)
            } else if (findNavController().currentDestination?.id == R.id.progressDialogScreen)
                findNavController().navigateUp()
        }
    }

    override fun setupView() {
        fabButtonAddCattle.setOnClickListener {
            viewModel.saveCattle()
        }

        editTextTagNumber.addTextChangedListener {
            viewModel.setTagNumber(it.toString())
        }

        editTextName.addTextChangedListener {
            viewModel.setName(it.toString())
        }

        editTextCalving.addTextChangedListener {
            viewModel.setTotalCalving(it.toString())
        }

        exposedDropDownBreed.addTextChangedListener {
            viewModel.setBreed(it.toString())
        }

        exposedDropDownType.addTextChangedListener {
            viewModel.setType(it.toString())
        }

        exposedDropDownGroup.addTextChangedListener {
            viewModel.setGroup(it.toString())
        }

        editTextPurchaseAmount.addTextChangedListener {
            val amount = it.toString()
            viewModel.setPurchaseAmount(
                if (amount.isNotBlank() && amount.isDigitsOnly())
                    amount.toLong()
                else
                    null
            )
        }

        editTextDateOfBirth.apply {
            setupForDateInput()
            addTextChangedListener { viewModel.setDob(it.toString()) }
        }

        editTextAiDate.apply {
            setupForDateInput()
            addTextChangedListener { viewModel.setAiDate(it.toString()) }
        }

        editTextRepeatHeatDate.apply {
            setupForDateInput()
            addTextChangedListener { viewModel.setRepeatHeatDate(it.toString()) }
        }

        editTextPregnancyCheckDate.apply {
            setupForDateInput()
            addTextChangedListener { viewModel.setPregnancyCheckDate(it.toString()) }
        }

        editTextDryOffDate.apply {
            setupForDateInput()
            addTextChangedListener { viewModel.setDryOffDate(it.toString()) }
        }

        editTextCalvingDate.apply {
            setupForDateInput()
            addTextChangedListener { viewModel.setCalvingDate(it.toString()) }
        }

        editTextPurchaseDate.apply {
            setupForDateInput()
            addTextChangedListener { viewModel.setPurchaseDate(it.toString()) }
        }

        setupDropDownAdapters()
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
            (requireActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
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

    private fun setupDropDownAdapters() {
        exposedDropDownBreed.setAdapter(
            ArrayAdapter<String>(
                requireContext(),
                R.layout.dropdown_menu_popup_item,
                resources.getStringArray(R.array.list_breed)
            )
        )

        exposedDropDownType.setAdapter(
            ArrayAdapter<String>(
                requireContext(),
                R.layout.dropdown_menu_popup_item,
                resources.getStringArray(R.array.list_type)
            )
        )

        exposedDropDownGroup.setAdapter(
            ArrayAdapter<String>(
                requireContext(),
                R.layout.dropdown_menu_popup_item,
                resources.getStringArray(R.array.list_group)
            )
        )
    }
}