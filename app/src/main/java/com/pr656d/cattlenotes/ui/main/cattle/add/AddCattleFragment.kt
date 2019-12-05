package com.pr656d.cattlenotes.ui.main.cattle.add

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.text.InputType
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.model.Cattle
import com.pr656d.cattlenotes.shared.base.BaseFragment
import com.pr656d.cattlenotes.shared.utils.common.viewModelProvider
import com.pr656d.cattlenotes.ui.main.MainSharedViewModel
import com.pr656d.cattlenotes.utils.common.EventObserver
import com.pr656d.cattlenotes.utils.common.parseToString
import kotlinx.android.synthetic.main.fragment_add_cattle.*
import kotlinx.android.synthetic.main.fragment_cattle_details.editTextAiDate
import kotlinx.android.synthetic.main.fragment_cattle_details.editTextCalving
import kotlinx.android.synthetic.main.fragment_cattle_details.editTextCalvingDate
import kotlinx.android.synthetic.main.fragment_cattle_details.editTextDateOfBirth
import kotlinx.android.synthetic.main.fragment_cattle_details.editTextDryOffDate
import kotlinx.android.synthetic.main.fragment_cattle_details.editTextName
import kotlinx.android.synthetic.main.fragment_cattle_details.editTextPregnancyCheckDate
import kotlinx.android.synthetic.main.fragment_cattle_details.editTextPurchaseAmount
import kotlinx.android.synthetic.main.fragment_cattle_details.editTextPurchaseDate
import kotlinx.android.synthetic.main.fragment_cattle_details.editTextRepeatHeatDate
import kotlinx.android.synthetic.main.fragment_cattle_details.editTextTagNumber
import kotlinx.android.synthetic.main.fragment_cattle_details.exposedDropDownBreed
import kotlinx.android.synthetic.main.fragment_cattle_details.exposedDropDownGroup
import kotlinx.android.synthetic.main.fragment_cattle_details.exposedDropDownType
import kotlinx.android.synthetic.main.fragment_cattle_details.nsvCattleDetails
import java.util.*

class AddCattleFragment : BaseFragment<AddCattleViewModel>() {

    companion object {
        const val TAG = "CattleActivity"
    }

    private val mainSharedViewModel by activityViewModels<MainSharedViewModel> { viewModelFactory }

    override fun provideLayoutId(): Int = R.layout.fragment_add_cattle

    override fun initViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
    }

    override fun setupObservers() {
        viewModel.launchCattleDetails.observe(viewLifecycleOwner, EventObserver {
            val action = AddCattleFragmentDirections.navigateToCattleDetails(it)
            findNavController().navigate(action)
        })

        viewModel.saving.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                mainSharedViewModel.refreshCattleList()
                val action = AddCattleFragmentDirections.navigateToProgressDialog(R.string.please_wait_text)
                findNavController().navigate(action)
            } else {
                findNavController().navigateUp()
            }
        })
    }

    override fun setupView() {
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

        fabButtonAddCattle.setOnClickListener {
            viewModel.saveCattle(getCattle())
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
    }
}