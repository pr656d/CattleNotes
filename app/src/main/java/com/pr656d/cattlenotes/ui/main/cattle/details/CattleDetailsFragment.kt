package com.pr656d.cattlenotes.ui.main.cattle.details

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.text.InputType
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.view.forEach
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.model.Cattle
import com.pr656d.cattlenotes.shared.base.BaseFragment
import com.pr656d.cattlenotes.shared.utils.common.CattleValidator
import com.pr656d.cattlenotes.ui.main.cattle.add.AddCattleFragmentDirections
import com.pr656d.cattlenotes.utils.common.EventObserver
import com.pr656d.cattlenotes.utils.common.parseToString
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_cattle_details.*
import kotlinx.android.synthetic.main.layout_cattle_details.*
import java.util.*

class CattleDetailsFragment : BaseFragment() {

    companion object {
        const val TAG = "CattleActivity"
    }

    private val viewModel by viewModels<CattleDetailsViewModel> { viewModelFactory }

    private val args by navArgs<CattleDetailsFragmentArgs>()

    override fun provideLayoutId(): Int = R.layout.fragment_cattle_details

    override fun setupObservers() {
        viewModel.editMode.observe(this, EventObserver {
            setMode(it)
        })

        viewModel.cattle.observe(this, EventObserver {
            it.bindView()
            /**
             * Dropdown don't show full list when it's text is set manually.
             * Need to override adapter again.
             */
            setupDropDownAdapters()
        })

        val handleError: TextInputLayout.(messageId: Int) -> Unit = { messageId: Int ->
            isErrorEnabled = if (messageId != CattleValidator.VALID_MESSAGE_ID) {
                error = getString(messageId)
                true
            } else { false }
        }

        viewModel.showErrorTagNumber.observe(viewLifecycleOwner) {
            layoutTagNumber.handleError(it)
        }

        viewModel.showErrorType.observe(viewLifecycleOwner) {
            layoutType.handleError(it)
        }

        viewModel.showErrorTotalCalving.observe(viewLifecycleOwner) {
            layoutCalving.handleError(it)
        }

        viewModel.saving.observe(viewLifecycleOwner) {
            if (it) {
                val action = AddCattleFragmentDirections.navigateToProgressDialog(R.string.please_wait_text)
                findNavController().navigate(action)
            } else {
                if (findNavController().currentDestination?.id == R.id.progressDialogScreen)
                    findNavController().navigateUp()
            }
        }

    }

    override fun setupView() {
        if (args.cattle != null)
            viewModel.setCattle(args.cattle!!)
        else
            viewModel.changeMode()

        fabButtonCattleDetails.setOnClickListener {
            if (viewModel.isInEditMode())
                viewModel.saveCattle()
            else
                viewModel.changeMode()
        }

        setupDropDownAdapters()

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
            viewModel.setPurchaseAmount(it.toString().toLong())
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
    }

    private fun setMode(editMode: Boolean) {
        requireActivity().toolbar.setTitle(
            if (viewModel.isInEditMode())
                R.string.edit_cattle_details
            else
                R.string.cattle_details
        )

        /**
         * Problem:
         * Can't handle end icon click for ExposedDropDownMenu even when using enabled property
         * directly from XML.
         * One solution is to hide the icon of ExposedDropDownMenu but view theme doesn't match
         * for all views.
         *
         * Solution:
         * Hide all start and end icon from [TextInputLayout] while not in edit mode.
         * When in edit mode show the icons.
         */
        val editTextForDateInput = intArrayOf(
            R.id.editTextDateOfBirth,
            R.id.editTextAiDate,
            R.id.editTextRepeatHeatDate,
            R.id.editTextPregnancyCheckDate,
            R.id.editTextDryOffDate,
            R.id.editTextCalvingDate,
            R.id.editTextPurchaseDate
        )

        val textInputLayoutForExposedDropDownMenu = intArrayOf(
            R.id.layoutBreed, R.id.layoutType, R.id.layoutGroup
        )

        val applyProperties: TextInputLayout.() -> Unit = {
            /**
             * [TextInputLayout] has [TextInputEditText] as child.
             * To access it we have to use [TextInputLayout.editText].
             */
            editText?.run {
                this.isEnabled = editMode
                // Check edit text is type date input category.
                if (editTextForDateInput.contains(this.id)) {
                    // Set start icon visibility.
                    isStartIconVisible = editMode
                }
            }

            // check view is type of ExposedDropDownMenu layout category.
            if (textInputLayoutForExposedDropDownMenu.contains(id))
                isEndIconVisible = editMode    // Set end icon visibility.
        }

        /**
         * Iterate through parent view to find [TextInputLayout].
         * Parent also have [LinearLayout] which contains some child views.
         * Iterate through those too.
         */
        view_cattle_details.forEach { view ->
            if (view is TextInputLayout) view.applyProperties()
            else if (view is LinearLayout) view.forEach {
                if (it is TextInputLayout) it.applyProperties()
            }
        }

        fabButtonCattleDetails.apply {
            hide()
            setImageDrawable(
                if (viewModel.isInEditMode())
                    getDrawable(requireContext(), R.drawable.ic_check_black)
                else
                    getDrawable(requireContext(), R.drawable.ic_edit_black)
            )
            show()
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

        isFocusableInTouchMode = false  // Initially set to false.

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
                    setText(parseToString(dd, mm, yyyy))
                    // After picking date reset focusable to false.
                    isFocusableInTouchMode = false
                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            )
            // Add cancel button click handler.
            dialog.setButton(
                DialogInterface.BUTTON_NEGATIVE,
                getString(R.string.cancel)
            ) { _, which ->
                if (which == DialogInterface.BUTTON_NEGATIVE)
                    isFocusableInTouchMode = false  // Reset focus to false.
            }
            dialog.show()   // Show the dialog.
        }
    }

    private fun Cattle.bindView() {
        val setTextIfNotSame: EditText.(newValue: String?) -> Unit = { newValue: String? ->
            if (text.toString() != newValue) {
                setText(newValue)
            }
        }
        editTextTagNumber.setTextIfNotSame(tagNumber.toString())
        editTextName.setTextIfNotSame(name)
        exposedDropDownBreed.setTextIfNotSame(breed)
        exposedDropDownType.setTextIfNotSame(type)
        editTextCalving.setTextIfNotSame(calving.toString())
        exposedDropDownGroup.setTextIfNotSame(group)
        editTextDateOfBirth.setTextIfNotSame(dateOfBirth)
        editTextAiDate.setTextIfNotSame(aiDate)
        editTextRepeatHeatDate.setTextIfNotSame(repeatHeatDate)
        editTextPregnancyCheckDate.setTextIfNotSame(pregnancyCheckDate)
        editTextDryOffDate.setTextIfNotSame(dryOffDate)
        editTextCalvingDate.setTextIfNotSame(calvingDate)
        /**
         * [NumberFormatException] will be thrown if you pass null for [TextInputEditText.setText]
         * which has input type as number.
         */
        purchaseAmount?.let { editTextPurchaseAmount.setTextIfNotSame(it.toString()) }
        editTextPurchaseDate.setTextIfNotSame(purchaseDate)
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