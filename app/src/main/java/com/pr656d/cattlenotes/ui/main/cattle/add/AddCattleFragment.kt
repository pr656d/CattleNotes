package com.pr656d.cattlenotes.ui.main.cattle.add

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.ui.main.cattle.BaseCattleFragment
import com.pr656d.cattlenotes.ui.main.cattle.BaseCattleViewModel
import com.pr656d.cattlenotes.utils.common.EventObserver
import kotlinx.android.synthetic.main.fragment_add_cattle.*

class AddCattleFragment : BaseCattleFragment() {

    companion object {
        const val TAG = "AddCattleFragment"
    }

    private val viewModel by viewModels<AddCattleViewModel> { viewModelFactory }

    override fun getBaseCattleViewModel(): BaseCattleViewModel = viewModel

    override fun provideLayoutId(): Int = R.layout.fragment_add_cattle

    override fun setupObservers() {
        super.setupObservers()

        viewModel.navigateUp.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigateUp()
        })

        viewModel.showError.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(requireView(), getString(it), Snackbar.LENGTH_INDEFINITE)
                .setAnchorView(R.id.fabButtonSaveCattle)
                .setAction(R.string.retry) { viewModel.retrySave() }
                .show()
        })
    }

    override fun setupView() {
        super.setupView()

        fabButtonSaveCattle.setOnClickListener {
            viewModel.onSaveClick()
        }
    }
}