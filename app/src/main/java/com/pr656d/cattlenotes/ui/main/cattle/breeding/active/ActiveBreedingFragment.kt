package com.pr656d.cattlenotes.ui.main.cattle.breeding.active

import androidx.fragment.app.viewModels
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.ui.main.cattle.breeding.base.BaseBreedingFragment
import com.pr656d.cattlenotes.ui.main.cattle.breeding.base.BaseBreedingViewModel

class ActiveBreedingFragment : BaseBreedingFragment() {

    companion object {
        const val TAG = "ActiveBreedingFragment"
    }

    private val viewModel by viewModels<ActiveBreedingViewModel> { viewModelFactory }

    override fun provideLayoutId(): Int = R.layout.fragment_active_breeding

    override fun getBaseBreedingViewModel(): BaseBreedingViewModel = viewModel

    override fun setupObservers() {
        super.setupObservers()

    }

    override fun setupView() {
        super.setupView()

    }
}