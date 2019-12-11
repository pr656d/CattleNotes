package com.pr656d.cattlenotes.ui.main.cattle.details

import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.view.forEach
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.model.Cattle
import com.pr656d.cattlenotes.ui.main.MainSharedViewModel
import com.pr656d.cattlenotes.ui.main.cattle.BaseCattleFragment
import com.pr656d.cattlenotes.ui.main.cattle.BaseCattleViewModel
import com.pr656d.cattlenotes.utils.common.EventObserver
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_cattle_details.*
import kotlinx.android.synthetic.main.layout_cattle_details.*

class CattleDetailsFragment : BaseCattleFragment() {

    companion object {
        const val TAG = "CattleActivity"
    }

    private val args by navArgs<CattleDetailsFragmentArgs>()
    private val viewModel by viewModels<CattleDetailsViewModel> { viewModelFactory }
    private val mainSharedVM by activityViewModels<MainSharedViewModel> { viewModelFactory }

    override fun getBaseCattleViewModel(): BaseCattleViewModel = viewModel

    override fun getMainSharedViewModel(): MainSharedViewModel = mainSharedVM

    override fun provideLayoutId(): Int = R.layout.fragment_cattle_details

    override fun setupObservers() {
        super.setupObservers()

        viewModel.editMode.observe(viewLifecycleOwner) {
            setMode(it)
        }

        viewModel.cattle.observe(viewLifecycleOwner, EventObserver {
            it.bindView()
        })

        viewModel.saveError.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(requireView(), getString(it), Snackbar.LENGTH_INDEFINITE)
                .setAnchorView(R.id.fabButtonCattleDetails)
                .setAction(R.string.retry) { viewModel.retrySave() }
                .show()
        })
    }

    override fun setupView() {
        super.setupView()

        viewModel.setCattle(args.cattle)

        fabButtonCattleDetails.setOnClickListener {
            viewModel.onFabClick()
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
            if (view is TextInputLayout)
                view.applyProperties()
            else if (view is LinearLayout)
                view.forEach {
                    if (it is TextInputLayout)
                        it.applyProperties()
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

    private fun Cattle.bindView() {
        val setTextIfNotSame: EditText.(newValue: String?) -> Unit = { newValue: String? ->
            if (text.toString() != newValue) {
                setText(newValue)
            }
        }
        editTextTagNumber.setTextIfNotSame(tagNumber.toString())
        editTextName.setTextIfNotSame(name)
        exposedDropDownBreed.apply {
            setTextIfNotSame(breed)
            setupDropDownAdapter(R.array.list_breed)
        }
        exposedDropDownType.apply {
            setTextIfNotSame(type)
            setupDropDownAdapter(R.array.list_type)
        }
        editTextCalving.setTextIfNotSame(calving.toString())
        exposedDropDownGroup.apply {
            setTextIfNotSame(group)
            setupDropDownAdapter(R.array.list_group)
        }
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
}