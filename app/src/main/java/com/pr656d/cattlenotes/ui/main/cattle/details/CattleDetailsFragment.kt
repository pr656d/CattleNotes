package com.pr656d.cattlenotes.ui.main.cattle.details

import androidx.core.view.forEach
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputLayout
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.model.Cattle
import com.pr656d.cattlenotes.shared.base.BaseFragment
import com.pr656d.cattlenotes.shared.utils.common.viewModelProvider
import com.pr656d.cattlenotes.utils.common.EventObserver
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_cattle.*

class CattleDetailsFragment : BaseFragment<CattleDetailsViewModel>() {

    companion object {
        const val TAG = "CattleActivity"
    }

    private val args by navArgs<CattleDetailsFragmentArgs>()

    override fun provideLayoutId(): Int = R.layout.fragment_cattle_details

    override fun init() {
        viewModel = viewModelProvider(viewModelFactory)

        requireActivity().bottomAppBar.navigationIcon = null
    }

    override fun setupObservers() {
        viewModel.cattle.observe(this, EventObserver {
            it.bindView()
        })
    }

    override fun setupView() {
        /**
         * All children of view are type of TextInputEditText
         * Default XML behaviour of view isEnabled is true.
         * So now we have to set isEnabled to false.
         */
        view_cattle_details.forEach { view ->
            if (view is TextInputLayout) view.apply {
                view.editText!!.isEnabled = false
            }
        }

        args.cattle?.let { viewModel.setCattle(it) }
    }

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