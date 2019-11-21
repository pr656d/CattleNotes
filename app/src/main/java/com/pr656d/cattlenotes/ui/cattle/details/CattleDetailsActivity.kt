package com.pr656d.cattlenotes.ui.cattle.details

import android.os.Bundle
import androidx.navigation.navArgs
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.model.Cattle
import com.pr656d.cattlenotes.shared.base.BaseActivity
import com.pr656d.cattlenotes.shared.utils.common.viewModelProvider
import com.pr656d.cattlenotes.utils.common.EventObserver
import kotlinx.android.synthetic.main.content_cattle_details.*

class CattleDetailsActivity : BaseActivity<CattleDetailsViewModel>() {

    companion object {
        const val TAG = "CattleDetailsActivity"
    }

    override fun provideLayoutId(): Int = R.layout.activity_cattle_details

    override fun init() {
        viewModel = viewModelProvider(viewModelFactory)

        val args: CattleDetailsActivityArgs by navArgs()
        viewModel.fetchCattle(args.cattleTagNumber)
    }

    override fun setupObservers() {
        viewModel.cattle.observe(this, EventObserver {
            bindData(it)
        })
    }

    override fun setupView(savedInstanceState: Bundle?) {}

    private fun bindData(cattle: Cattle) {
        editTextTagNumber.setText(cattle.tagNumber)
        editTextName.setText(cattle.name)
        editTextType.setText(cattle.type.displayName)
        editTextBreed.setText(cattle.breed?.displayName)
        editTextGroup.setText(cattle.group?.displayName)
        editTextDateOfBirth.setText(cattle.dateOfBirth)
        editTextCalving.setText(cattle.calving.toString())
        editTextAiDate.setText(cattle.aiDate)
        editTextRepeatHeatDate.setText(cattle.repeatHeatDate)
        editTextPregnancyCheckDate.setText(cattle.pregnancyCheckDate)
        editTextPurchaseAmount.setText(cattle.purchaseAmount.toString())
        editTextPurchaseDate.setText(cattle.purchaseDate)
    }
}