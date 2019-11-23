package com.pr656d.cattlenotes.ui.cattle_details.edit

import android.app.DatePickerDialog
import android.text.InputType
import android.view.View
import android.widget.TextView
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.model.Cattle
import com.pr656d.cattlenotes.shared.base.BaseFragment
import com.pr656d.cattlenotes.ui.cattle_details.details.CattleDetailsFragmentArgs
import com.pr656d.cattlenotes.utils.common.parseToString
import kotlinx.android.synthetic.main.content_edit_cattle_details.*
import kotlinx.android.synthetic.main.fragment_edit_cattle_details.*
import java.util.*

class EditCattleFragment : BaseFragment<EditCattleViewModel>() {

    companion object {
        const val TAG = "EditCattleFragment"


    }

    override fun provideLayoutId(): Int = R.layout.fragment_edit_cattle_details

    override fun init() {}

    override fun setupObservers() {}

    override fun setupView(view: View) {
        CattleDetailsFragmentArgs
            .fromBundle(requireActivity().intent.extras!!)
            .cattle
            .bindView()

        fabButton.setOnClickListener {
            viewModel.saveCattle(getCattle())
            requireActivity().onBackPressed()
        }

        editTextDateOfBirth.apply {
            setOnClickListener {
                inputType = InputType.TYPE_NULL
                showDatePickerDialogAndSetText()
            }
        }

        editTextAiDate.apply{
            setOnClickListener {
                showDatePickerDialogAndSetText()
            }
        }

        editTextRepeatHeatDate.apply {
            setOnClickListener {
                showDatePickerDialogAndSetText()
            }
        }

        editTextPregnancyCheckDate.apply {
            setOnClickListener {
                showDatePickerDialogAndSetText()
            }
        }

        editTextDryOffDate.apply {
            setOnClickListener {
                showDatePickerDialogAndSetText()
            }
        }

        editTextCalvingDate.apply {
            setOnClickListener {
                showDatePickerDialogAndSetText()
            }
        }

        editTextPurchaseDate.apply {
            setOnClickListener {
                showDatePickerDialogAndSetText()
            }
        }
    }

    private fun TextView.showDatePickerDialogAndSetText() {
        DatePickerDialog(
            requireContext(), R.style.DatePicker,
            DatePickerDialog.OnDateSetListener { _, yyyy, mm, dd ->
                text = parseToString(dd, mm, yyyy)
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun Cattle.bindView() {
        editTextTagNumber.setText(tagNumber)
        editTextName.setText(name)
        editTextBreed.setText(breed)
        editTextType.setText(type)
        editTextCalving.setText(calving.toString())
        editTextGroup.setText(group)
        editTextDateOfBirth.setText(dateOfBirth)
        editTextAiDate.setText(aiDate)
        editTextRepeatHeatDate.setText(repeatHeatDate)
        editTextPregnancyCheckDate.setText(pregnancyCheckDate)
        editTextDryOffDate.setText(dryOffDate)
        editTextCalvingDate.setText(calvingDate)
        editTextPurchaseAmount.setText(purchaseAmount.toString())
        editTextPurchaseDate.setText(purchaseDate)
    }

    private fun getCattle(): Cattle {
        val tagNumber = editTextTagNumber.text.toString()
        val name = editTextName.text.toString()
        val type = editTextBreed.text.toString()
        val imageUrl = null
        val breed = editTextBreed.text.toString()
        val group = editTextGroup.text.toString()
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
}
