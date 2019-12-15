package com.pr656d.cattlenotes.ui.main.cattle.add

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.google.android.material.snackbar.Snackbar
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.ui.main.MainSharedViewModel
import com.pr656d.cattlenotes.ui.main.cattle.BaseCattleFragment
import com.pr656d.cattlenotes.ui.main.cattle.BaseCattleViewModel
import com.pr656d.cattlenotes.utils.common.EventObserver
import kotlinx.android.synthetic.main.fragment_add_cattle.*

class AddCattleFragment : BaseCattleFragment() {

    companion object {
        const val TAG = "AddCattleFragment"
    }

    private val viewModel by viewModels<AddCattleViewModel> { viewModelFactory }
    private val mainSharedVM by activityViewModels<MainSharedViewModel> { viewModelFactory }

    override fun getMainSharedViewModel(): MainSharedViewModel = mainSharedVM

    override fun getBaseCattleViewModel(): BaseCattleViewModel = viewModel

    override fun provideLayoutId(): Int = R.layout.fragment_add_cattle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val transition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = transition
        sharedElementReturnTransition = transition
    }

    override fun setupObservers() {
        super.setupObservers()

        viewModel.navigateUp.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigateUp()
        })

        viewModel.saveError.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(requireView(), getString(it), Snackbar.LENGTH_INDEFINITE)
                .setAnchorView(R.id.fabButtonSaveCattle)
                .setAction(R.string.retry) { viewModel.retrySave() }
                .show()
        })
    }

    override fun setupView() {
        super.setupView()

        fabButtonSaveCattle.setOnClickListener {
            viewModel.onFabClick()
        }
    }
}