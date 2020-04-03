package com.pr656d.cattlenotes.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButtonToggleGroup
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
            model.selectedGenderId.postValue(checkedId)
        }

        model.selectedGenderId.observe(viewLifecycleOwner, Observer {
            genderGroup.check(it)
        })

        return binding.root
    }
}