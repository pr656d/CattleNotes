/*
 * Copyright 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pr656d.cattlenotes.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.pr656d.cattlenotes.databinding.FragmentSetupProfileBinding
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class SetupProfileFragment : DaggerFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val model by activityViewModels<LoginViewModel> { viewModelFactory }

    private lateinit var binding: FragmentSetupProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSetupProfileBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = model

        val genderGroup = binding.includeSelectGenderLayout.toggleGroupGender

        genderGroup.addOnButtonCheckedListener { _, checkedId, _ ->
            val previousCheckedId = model.selectedGenderId.value
            if (previousCheckedId != checkedId)
                model.selectedGenderId.postValue(checkedId)
        }

        model.selectedGenderId.observe(
            viewLifecycleOwner,
            Observer {
                if (it == null)
                    genderGroup.clearChecked()
                else
                    genderGroup.check(it)
            }
        )

        model.updateErrorMessage.observe(
            viewLifecycleOwner,
            Observer {
                Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG)
                    .setAnchorView(binding.fabButtonSaveProfile)
                    .show()
            }
        )

        return binding.root
    }
}
