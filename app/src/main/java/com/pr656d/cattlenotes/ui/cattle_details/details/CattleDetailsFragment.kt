package com.pr656d.cattlenotes.ui.cattle_details.details

import android.view.View
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.model.Cattle
import com.pr656d.cattlenotes.shared.base.BaseFragment
import com.pr656d.cattlenotes.shared.utils.common.navigateTo
import kotlinx.android.synthetic.main.content_cattle_details.*
import kotlinx.android.synthetic.main.fragment_cattle_details.*

class CattleDetailsFragment : BaseFragment<CattleDetailsViewModel>() {

    companion object {
        const val TAG = "CattleDetailsFragment"
    }

    override fun provideLayoutId(): Int = R.layout.fragment_cattle_details

    override fun init() {}

    override fun setupObservers() {

    }

    override fun setupView(view: View) {
        CattleDetailsFragmentArgs
            .fromBundle(requireActivity().intent.extras!!)
            .cattle
            .bindView()

        fabButton.setOnClickListener {
            navigateTo(R.id.nav_host_cattle_details, R.id.navigate_to_cattle_edit_details)
        }
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
