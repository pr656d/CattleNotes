package com.pr656d.cattlenotes.ui.cattle.add

import android.os.Bundle
import androidx.lifecycle.Observer
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.model.Cattle
import com.pr656d.cattlenotes.shared.base.BaseActivity
import com.pr656d.cattlenotes.shared.utils.common.convertToBreed
import com.pr656d.cattlenotes.shared.utils.common.convertToGroup
import com.pr656d.cattlenotes.shared.utils.common.convertToType
import com.pr656d.cattlenotes.shared.utils.common.viewModelProvider
import kotlinx.android.synthetic.main.activity_add_edit_cattle.*
import kotlinx.android.synthetic.main.content_add_edit_cattle.*

class AddEditCattleActivity : BaseActivity<AddEditCattleViewModel>() {

    override fun provideLayoutId(): Int = R.layout.activity_add_edit_cattle

    override fun init() {
        viewModel = viewModelProvider(viewModelFactory)
    }

    override fun setupObservers() {
        super.setupObservers()

        viewModel.loading.observe(this, Observer {

        })
    }

    override fun setupView(savedInstanceState: Bundle?) {
        fabButton.setOnClickListener {
            viewModel.saveCattle(getCattle())
        }
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
            tagNumber, name, type.convertToType(), imageUrl, breed.convertToBreed(),
            group.convertToGroup(), calving, dateOfBirth, aiDate, repeatHeatDate,
            pregnancyDate, dryOffDate, calvingDate, purchaseAmount, purchaseDate
        )
    }
}