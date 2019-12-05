package com.pr656d.cattlenotes.ui.main.cattle.details

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.text.InputType
import android.view.ViewTreeObserver
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

        val configureEditTextForDateInput: TextInputEditText.() -> Unit = {
            inputType = InputType.TYPE_NULL
            setOnClickListener {
                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view?.windowToken, 0)
                isFocusableInTouchMode = true
                requestFocus()
                showDatePickerDialogAndSetText()
            }
        }

        editTextDateOfBirth.apply { configureEditTextForDateInput() }

        editTextAiDate.apply { configureEditTextForDateInput() }

        editTextRepeatHeatDate.apply { configureEditTextForDateInput() }

        editTextPregnancyCheckDate.apply { configureEditTextForDateInput() }

        editTextDryOffDate.apply { configureEditTextForDateInput() }

        editTextCalvingDate.apply { configureEditTextForDateInput() }

        editTextPurchaseDate.apply { configureEditTextForDateInput() }

        editTextPurchaseAmount.apply {
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus)
                    nsvCattleDetails.apply { viewTreeObserver.addOnGlobalLayoutListener(
                        object : ViewTreeObserver.OnGlobalLayoutListener {
                            override fun onGlobalLayout() {
                                val scrollViewHeight = height

                                if (scrollViewHeight > 0) {
                                    viewTreeObserver.removeOnGlobalLayoutListener(this)

                                    val lastView = getChildAt(childCount - 1)
                                    val lastViewBottom = lastView.bottom + paddingBottom
                                    val deltaScrollY = lastViewBottom - scrollViewHeight - scrollY
                                    /* If you want to see the scroll animation, call this. */
                                    smoothScrollBy(0, deltaScrollY)
                                    /* If you don't want, call this. */
                                    // scrollBy(0, deltaScrollY)
                                }
                            }
                        }
                    )}
            }
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
         * validIDs list contains IDs of [TextInputEditText]
         * which [TextInputEditText.isFocusableInTouchMode] property is being changed manually
         * [DatePickerDialog] is shown in place of soft keyboard.
         *
         * So now reset them to false.
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

        view_cattle_details.forEach { view ->
            if (view is TextInputLayout) {
                view.apply {
                    val editText = view.editText!!
                    editText.isEnabled = editMode
                    if (editTextForDateInput.contains(editText.id)) {
                        editText.isFocusableInTouchMode = false
                        view.isStartIconVisible = editMode
                    }
                    if (textInputLayoutForExposedDropDownMenu.contains(view.id))
                        view.isEndIconVisible = editMode
                }
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

    private fun TextInputEditText.showDatePickerDialogAndSetText() {
        val dialog = DatePickerDialog(
            context, R.style.DatePicker,
            DatePickerDialog.OnDateSetListener { _, yyyy, mm, dd ->
                setText(parseToString(dd, mm, yyyy))
                isFocusableInTouchMode = false
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
        dialog.setButton(
            DialogInterface.BUTTON_NEGATIVE,
            getString(R.string.cancel)
        ) { _, which ->
            if (which == DialogInterface.BUTTON_NEGATIVE)
                isFocusableInTouchMode = false
        }
        dialog.show()
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

        setupDropDownAdapters()
    }
}