package com.pr656d.cattlenotes.ui.cattle_details

import android.os.Bundle
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.shared.base.BaseActivity
import com.pr656d.cattlenotes.shared.utils.common.viewModelProvider

class CattleDetailsActivity : BaseActivity<CattleDetailsViewModel>() {

    companion object {
        const val TAG = "CattleDetailsActivity"
    }

    override fun provideLayoutId(): Int = R.layout.activity_cattle_details

    override fun init() {
        viewModel = viewModelProvider(viewModelFactory)
    }

    override fun setupObservers() { }

    override fun setupView(savedInstanceState: Bundle?) { }
}