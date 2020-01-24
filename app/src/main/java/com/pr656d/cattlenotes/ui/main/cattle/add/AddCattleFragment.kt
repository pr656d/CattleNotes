package com.pr656d.cattlenotes.ui.main.cattle.add

import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.ui.main.cattle.base.BaseCattleFragment
import com.pr656d.cattlenotes.ui.main.cattle.base.BaseCattleViewModel
import kotlinx.android.synthetic.main.fragment_add_cattle.*

class AddCattleFragment : BaseCattleFragment() {

    companion object {
        const val TAG = "AddCattleFragment"
    }

    private val viewModel by viewModels<AddCattleViewModel> { viewModelFactory }

    override fun getBaseCattleViewModel(): BaseCattleViewModel = viewModel

    override fun provideLayoutId(): Int = R.layout.fragment_add_cattle

    override fun getFabButtonId(): Int = R.id.fabButtonSaveCattle

    override fun setupObservers() {
        super.setupObservers()
    }

    override fun setupView() {
        super.setupView()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (viewModel.tagNumber.value != null)
                viewModel.showBackPressedScreen()
            else
                viewModel.navigateUp()
        }

        fabButtonSaveCattle.setOnClickListener {
            viewModel.onSaveClick()
        }
    }
}