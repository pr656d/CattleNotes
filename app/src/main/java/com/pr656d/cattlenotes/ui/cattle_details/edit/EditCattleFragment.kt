package com.pr656d.cattlenotes.ui.cattle_details.edit

import android.view.View
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.model.Cattle
import com.pr656d.cattlenotes.shared.base.BaseFragment
import com.pr656d.cattlenotes.ui.cattle_details.details.CattleDetailsFragmentArgs
import kotlinx.android.synthetic.main.content_cattle_details.*

class EditCattleFragment : BaseFragment<EditCattleViewModel>() {

    override fun provideLayoutId(): Int = R.layout.fragment_edit_cattle_details

    override fun init() {}

    override fun setupObservers() {}

    override fun setupView(view: View) {
        CattleDetailsFragmentArgs
            .fromBundle(requireActivity().intent.extras!!)
            .cattle
            .bindView()
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
}
