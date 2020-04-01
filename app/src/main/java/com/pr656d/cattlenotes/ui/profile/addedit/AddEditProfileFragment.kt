package com.pr656d.cattlenotes.ui.profile.addedit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.pr656d.cattlenotes.databinding.FragmentAddEditProfileBinding
import com.pr656d.cattlenotes.ui.NavigationFragment
import com.pr656d.shared.domain.result.EventObserver
import javax.inject.Inject

class AddEditProfileFragment : NavigationFragment() {
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val model by viewModels<AddEditProfileViewModel> { viewModelFactory }

    private lateinit var binding: FragmentAddEditProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddEditProfileBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = model
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.navigateUp.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigateUp()
        })
    }

}