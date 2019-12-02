package com.pr656d.cattlenotes.ui.cattle

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.text.InputType
import android.view.ViewTreeObserver
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.view.forEach
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.model.Cattle
import com.pr656d.cattlenotes.shared.base.BaseFragment
import com.pr656d.cattlenotes.shared.utils.common.viewModelProvider
import com.pr656d.cattlenotes.utils.common.EventObserver
import com.pr656d.cattlenotes.utils.common.parseToString
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_cattle_details.*
import java.util.*

class CattleDetailsFragment : BaseFragment<CattleDetailsViewModel>() {

    companion object {
        const val TAG = "CattleActivity"
    }

    private val args by navArgs<CattleDetailsFragmentArgs>()
    private lateinit var addVisibilityChanged: FloatingActionButton.OnVisibilityChangedListener

    override fun provideLayoutId(): Int = R.layout.fragment_cattle_details

    override fun init() {
        viewModel = viewModelProvider(viewModelFactory)

        requireActivity().bottomAppBar.navigationIcon = null

        addVisibilityChanged = object : FloatingActionButton.OnVisibilityChangedListener() {
            override fun onHidden(fab: FloatingActionButton?) {
                super.onHidden(fab)
                requireActivity().bottomAppBar.apply {
                    replaceMenu(
                        if (viewModel.isInEditMode())
                            R.menu.menu_cattle_edit_details_appbar
                        else
                            R.menu.menu_cattle_details_appbar
                    )
                    toggleFabAlignment()
                }
                requireActivity().bottomAppBar.toggleFabAlignment()
                fab?.setImageDrawable(
                    if (viewModel.isInEditMode())
                        getDrawable(requireContext(), R.drawable.ic_check_black)
                    else
                        getDrawable(requireContext(), R.drawable.ic_edit_black)
                )
                fab?.show()
            }
        }
    }

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

        requireActivity().toolbar.setTitle(
            if (viewModel.isInEditMode()) {
                if (args.cattle != null)
                    R.string.edit_cattle_details
                else
                    R.string.add_cattle
            }
            else {
                R.string.cattle_details
            }
        )

        val configureEditTextForDateInput: TextInputEditText.() -> Unit = {
            inputType = InputType.TYPE_NULL
            setOnClickListener {
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
                    nsvCattleDetails.viewTreeObserver.addOnGlobalLayoutListener(
                        object : ViewTreeObserver.OnGlobalLayoutListener {
                            override fun onGlobalLayout() {
                                val scrollViewHeight = nsvCattleDetails.height

                                if (scrollViewHeight > 0) {
                                    nsvCattleDetails.viewTreeObserver
                                        .removeOnGlobalLayoutListener(this)

                                    val lastView =
                                        nsvCattleDetails.getChildAt(nsvCattleDetails.childCount - 1)
                                    val lastViewBottom =
                                        lastView.bottom + nsvCattleDetails.paddingBottom
                                    val deltaScrollY =
                                        lastViewBottom - scrollViewHeight - nsvCattleDetails.scrollY
                                    /* If you want to see the scroll animation, call this. */
                                    nsvCattleDetails.smoothScrollBy(0, deltaScrollY)
                                    /* If you don't want, call this. */
                                    nsvCattleDetails.scrollBy(0, deltaScrollY)
                                }
                            }
                        }
                    )
            }
        }
    }

    private fun setMode(editMode: Boolean) {
        /**
         * [validIDs] list contains IDs of [TextInputEditText]
         * which [isFocusableInTouchMode] property is being changed manually
         * because [DatePickerDialog] is shown in place of soft keyboard.
         *
         * So now reset them to false.
         */
        val validIDs = intArrayOf(
            R.id.editTextDateOfBirth,
            R.id.editTextAiDate,
            R.id.editTextRepeatHeatDate,
            R.id.editTextPregnancyCheckDate,
            R.id.editTextDryOffDate,
            R.id.editTextCalvingDate,
            R.id.editTextPurchaseDate
        )

        view_cattle_details.forEach { view ->
            if (view is TextInputLayout) {
                view.apply {
                    val editText = view.editText!!
                    editText.isEnabled = editMode
                    if (validIDs.contains(editText.id))
                        editText.isFocusableInTouchMode = false
                }
            }
        }

        requireActivity().fabButton.hide(addVisibilityChanged)
        requireActivity().invalidateOptionsMenu()
    }

    private fun BottomAppBar.toggleFabAlignment() {
        fabAlignmentMode =
            if (viewModel.isInEditMode()) BottomAppBar.FAB_ALIGNMENT_MODE_END
            else BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
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
            getString(com.pr656d.cattlenotes.R.string.cancel)
        ) { _, which ->
            if (which == DialogInterface.BUTTON_NEGATIVE)
                isFocusableInTouchMode = false
        }
        dialog.show()
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

    private fun Cattle.bindView() {
        editTextTagNumber.setText(this.tagNumber)
        editTextName.setText(this.name)
        editTextBreed.setText(this.breed)
        editTextType.setText(this.type)
        editTextCalving.setText(this.calving.toString())
        editTextGroup.setText(this.group)
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