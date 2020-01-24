package com.pr656d.cattlenotes.ui.main.cattle.breeding.add

import androidx.fragment.app.viewModels
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.ui.main.cattle.breeding.base.BaseBreedingFragment
import com.pr656d.cattlenotes.ui.main.cattle.breeding.base.BaseBreedingViewModel

class AddBreedingFragment : BaseBreedingFragment() {

    companion object {
        const val TAG = "AddBreedingFragment"
    }

    private val viewModel by viewModels<AddBreedingViewModel> { viewModelFactory }

    override fun provideLayoutId(): Int = R.layout.fragment_add_breeding

    override fun getBaseBreedingViewModel(): BaseBreedingViewModel = viewModel

    override fun setupObservers() {
        super.setupObservers()

    }

    override fun setupView() {
        super.setupView()

    }
}