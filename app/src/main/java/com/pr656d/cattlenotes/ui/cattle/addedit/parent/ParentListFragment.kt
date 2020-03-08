package com.pr656d.cattlenotes.ui.cattle.addedit.parent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.pr656d.cattlenotes.databinding.FragmentParentListBinding
import com.pr656d.cattlenotes.ui.cattle.addedit.AddEditCattleViewModel
import com.pr656d.cattlenotes.utils.parentViewModelProvider
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class ParentListFragment : DaggerFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var model: AddEditCattleViewModel

    private lateinit var binding: FragmentParentListBinding

    private lateinit var behavior: BottomSheetBehavior<*>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentParentListBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // ViewModel is scoped to parent fragment.
        model = parentViewModelProvider(viewModelFactory)
        binding.viewModel = model

        behavior = BottomSheetBehavior.from(binding.parentListSheet)

        binding.toolbar.setNavigationOnClickListener {
            behavior.state = if (behavior.skipCollapsed)
                    STATE_HIDDEN
                else
                    STATE_COLLAPSED
        }

        binding.toolbar.setOnClickListener {
            behavior.state = STATE_EXPANDED
        }
    }
}