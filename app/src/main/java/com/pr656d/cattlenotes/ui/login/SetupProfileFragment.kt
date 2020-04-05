package com.pr656d.cattlenotes.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.snackbar.Snackbar
import com.pr656d.cattlenotes.databinding.FragmentSetupProfileBinding
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class SetupProfileFragment : DaggerFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val model by activityViewModels<LoginViewModel> { viewModelFactory }

    private lateinit var binding: FragmentSetupProfileBinding
    private lateinit var genderGroup: MaterialButtonToggleGroup

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSetupProfileBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = model

        genderGroup = binding.includeSelectGenderLayout.toggleGroupGender

        genderGroup.addOnButtonCheckedListener { _, checkedId, _ ->
            val previousCheckedId = model.selectedGenderId.value
            if (previousCheckedId != checkedId)
                model.selectedGenderId.postValue(checkedId)
        }

        model.selectedGenderId.observe(viewLifecycleOwner, Observer {
            if (it == null)
                genderGroup.clearChecked()
            else
                genderGroup.check(it)
        })

        model.updateErrorMessage.observe(viewLifecycleOwner, Observer {
            Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG)
                .setAnchorView(binding.fabButtonSaveProfile)
                .show()
        })

        return binding.root
    }
}