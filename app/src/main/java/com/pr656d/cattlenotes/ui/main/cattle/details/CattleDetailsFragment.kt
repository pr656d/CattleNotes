package com.pr656d.cattlenotes.ui.main.cattle.details

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.text.InputType
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.view.forEach
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.model.Cattle
import com.pr656d.cattlenotes.shared.base.BaseFragment
import com.pr656d.cattlenotes.utils.common.EventObserver
import com.pr656d.cattlenotes.utils.common.parseToString
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_cattle_details.*
import java.util.*

class CattleDetailsFragment : BaseFragment() {

    companion object {
        const val TAG = "CattleActivity"
    }

    private val viewModel by viewModels<CattleDetailsViewModel> { viewModelFactory }

    private val args by navArgs<CattleDetailsFragmentArgs>()
    private val setupDropDownAdapters: () -> Unit by lazy {
        {
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
    }

    override fun setupView() {
        if (args.cattle != null)
            viewModel.setCattle(args.cattle!!)
        else
            viewModel.changeMode()

        fabButtonCattleDetails.setOnClickListener {
            viewModel.changeMode()
        }

        setupDropDownAdapters()

        editTextDateOfBirth.setupForDateInput()

        editTextAiDate.setupForDateInput()

        editTextRepeatHeatDate.setupForDateInput()

        editTextPregnancyCheckDate.setupForDateInput()

        editTextDryOffDate.setupForDateInput()

        editTextCalvingDate.setupForDateInput()

        editTextPurchaseDate.setupForDateInput()
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

        /**
         * Iterate through parent view to find [TextInputLayout].
         */
        view_cattle_details.forEach { view ->
            if (view is TextInputLayout) {
                view.apply {
                    /**
                     * [TextInputLayout] has [TextInputEditText] as child.
                     * To access it we have to use [TextInputLayout.editText].
                     */
                    view.editText?.run {
                        this.isEnabled = editMode
                        // Check edit text is type date input category.
                        if (editTextForDateInput.contains(this.id)) {
                            // Set start icon visibility.
                            view.isStartIconVisible = editMode
                        }
                    }
                }

                // check view is type of ExposedDropDownMenu layout category.
                if (textInputLayoutForExposedDropDownMenu.contains(view.id))
                    view.isEndIconVisible = editMode    // Set end icon visibility.
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

    private fun getCattle(): Cattle {
        val tagNumber = editTextTagNumber.text.toString()
        val name = editTextName.text.toString()
        val type = exposedDropDownType.text.toString()
        val imageUrl = null
        val breed = exposedDropDownBreed.text.toString()
        val group = exposedDropDownGroup.text.toString()
        val calving = editTextCalving.text.toString().toInt()
        val dateOfBirth = editTextDateOfBirth.text.toString()
        val aiDate = editTextAiDate.text.toString()
        val repeatHeatDate = editTextRepeatHeatDate.text.toString()
        val pregnancyDate = editTextPregnancyCheckDate.text.toString()
        val dryOffDate = editTextDryOffDate.text.toString()
        val calvingDate = editTextCalvingDate.text.toString()
        val purchaseAmount = editTextPurchaseAmount.text.toString().toLong()
        val purchaseDate = editTextPurchaseDate.text.toString()

        return Cattle(
            tagNumber, name, type, imageUrl, breed,
            group, calving, dateOfBirth, aiDate, repeatHeatDate,
            pregnancyDate, dryOffDate, calvingDate, purchaseAmount, purchaseDate
        )
    }

    private fun Cattle.bindView() {
        editTextTagNumber.setText(this.tagNumber)
        editTextName.setText(this.name)
        exposedDropDownBreed.setText(this.breed)
        exposedDropDownType.setText(this.type)
        editTextCalving.setText(this.calving.toString())
        exposedDropDownGroup.setText(this.group)
        editTextDateOfBirth.setText(this.dateOfBirth)
        editTextAiDate.setText(this.aiDate)
        editTextRepeatHeatDate.setText(this.repeatHeatDate)
        editTextPregnancyCheckDate.setText(this.pregnancyCheckDate)
        editTextDryOffDate.setText(this.dryOffDate)
        editTextCalvingDate.setText(this.calvingDate)
        editTextPurchaseAmount.setText(this.purchaseAmount.toString())
        editTextPurchaseDate.setText(this.purchaseDate)
    }
}