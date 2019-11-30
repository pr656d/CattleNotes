package com.pr656d.cattlenotes.ui.main.cattle.edit

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.text.InputType
import android.view.ViewTreeObserver
import androidx.core.view.forEach
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.model.Cattle
import com.pr656d.cattlenotes.shared.base.BaseFragment
import com.pr656d.cattlenotes.shared.utils.common.viewModelProvider
import com.pr656d.cattlenotes.utils.common.EventObserver
import com.pr656d.cattlenotes.utils.common.parseToString
import kotlinx.android.synthetic.main.content_cattle.*
import java.util.*

class CattleEditDetailsFragment : BaseFragment<CattleEditViewModel>() {

    companion object {
        const val TAG = "CattleActivity"
    }

    private val args by navArgs<CattleEditDetailsFragmentArgs>()

    override fun provideLayoutId(): Int = R.layout.fragment_add_cattle

    override fun init() {
        viewModel = viewModelProvider(viewModelFactory)
    }

    override fun setupObservers() {
        viewModel.cattle.observe(this, EventObserver {
            it.bindView()
        })
    }

    override fun setupView() {
        args.cattle?.let { viewModel.setCattle(it) }

        val configureEditTextForDateInput: TextInputEditText.() -> Unit = {
            inputType = InputType.TYPE_NULL
            setOnClickListener {
                isFocusableInTouchMode = true
                requestFocus()
                showDatePickerDialogAndSetText()
            }
        }

        /**
         * [validIDs] list contains IDs of [TextInputEditText]
         * which [isFocusableInTouchMode] property is being changed manually
         * custom dialog or picker will be shown in place of soft keyboard.
         *
         * So now reset them to false.
         */
        val validIDs = intArrayOf(
            R.id.editTextDateOfBirth, R.id.editTextAiDate, R.id.editTextRepeatHeatDate,
            R.id.editTextPregnancyCheckDate, R.id.editTextDryOffDate, R.id.editTextCalvingDate,
            R.id.editTextPurchaseDate
        )

        view_cattle_details.forEach { view ->
            if (view is TextInputLayout) {
                view.apply {
                    val editText = view.editText!!
                    if (validIDs.contains(editText.id)) {
                        editText.isFocusableInTouchMode = false
                        (editText as TextInputEditText).configureEditTextForDateInput()
                    }
                }
            }
        }

        editTextPurchaseAmount.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                nsvCattleDetails.apply {
                    viewTreeObserver.addOnGlobalLayoutListener(
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
//                                    nsvCattleDetails.scrollBy(0, deltaScrollY)
                                }
                            }
                        }
                    )
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
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel)) { _, which ->
            if (which == DialogInterface.BUTTON_NEGATIVE)
                isFocusableInTouchMode = false
        }
        dialog.show()
    }

//    private fun getCattle(): Cattle {
//        val tagNumber = editTextTagNumber.text.toString()
//        val name = editTextName.text.toString()
//        val type = editTextType.text.toString()
//        val imageUrl = null
////        val breed = editTextBreed.text.toString()
//        val group = editTextGroup.text.toString()
//        val calving = editTextCalving.text.toString().toInt()
//        val dateOfBirth = editTextDateOfBirth.text.toString()
//        val aiDate = editTextAiDate.text.toString()
//        val repeatHeatDate = editTextRepeatHeatDate.text.toString()
//        val pregnancyDate = editTextPregnancyCheckDate.text.toString()
//        val dryOffDate = editTextDryOffDate.text.toString()
//        val calvingDate = editTextCalvingDate.text.toString()
//        val purchaseAmount = editTextPurchaseAmount.text.toString().toLong()
//        val purchaseDate = editTextPurchaseDate.text.toString()
//
//        return Cattle(
//            tagNumber, name, type, imageUrl, breed,
//            group, calving, dateOfBirth, aiDate, repeatHeatDate,
//            pregnancyDate, dryOffDate, calvingDate, purchaseAmount, purchaseDate
//        )
//    }

    private fun Cattle.bindView() {
        editTextTagNumber.setText(this.tagNumber)
        editTextName.setText(this.name)
//        editTextBreed.setText(this.breed)
//        editTextType.setText(this.type)
        editTextCalving.setText(this.calving.toString())
//        editTextGroup.setText(this.group)
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