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
package com.pr656d.cattlenotes.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.databinding.FragmentSettingsBinding
import com.pr656d.cattlenotes.ui.NavigationFragment
import com.pr656d.cattlenotes.ui.milking.sms.SelectMilkSmsSenderDialogFragment
import com.pr656d.cattlenotes.ui.settings.SettingsFragmentDirections.Companion.toCredits
import com.pr656d.cattlenotes.utils.pickATime
import com.pr656d.shared.domain.result.EventObserver
import com.pr656d.shared.utils.TimeUtils
import javax.inject.Inject

class SettingsFragment : NavigationFragment() {

    companion object {
        const val TAG = "SettingsFragment"
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val model by viewModels<SettingsViewModel> { viewModelFactory }

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = model
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.navigateToThemeSelector.observe(
            viewLifecycleOwner,
            EventObserver {
                ThemeSettingDialogFragment.newInstance()
                    .show(parentFragmentManager, null)
            }
        )

        model.navigateToSmsSourceSelector.observe(
            viewLifecycleOwner,
            EventObserver {
                SelectMilkSmsSenderDialogFragment.newInstance()
                    .apply {
                        viewLifecycleOwnerLiveData.observe(
                            this@SettingsFragment.viewLifecycleOwner,
                            Observer {
                                // It will be null when onDestroyView will be called.
                                if (it == null) {
                                    model.refresh()
                                }
                            }
                        )
                    }
                    .show(childFragmentManager, null)
            }
        )

        model.navigateToCredits.observe(
            viewLifecycleOwner,
            EventObserver {
                findNavController().navigate(toCredits())
            }
        )

        model.navigateToOpenSourceLicenses.observe(
            viewLifecycleOwner,
            EventObserver {
                startActivity(
                    Intent(requireContext(), OssLicensesMenuActivity::class.java)
                )
            }
        )

        model.navigateToBreedingReminderTimeSelector.observe(
            viewLifecycleOwner,
            EventObserver {
                requireView().pickATime { _, hourOfDay, minute ->
                    model.setBreedingReminderTime(TimeUtils.toLocalTime(hourOfDay, minute))
                }
            }
        )
    }
}

@BindingAdapter("versionName")
fun setVersionName(view: TextView, versionName: String) {
    view.text = view.resources.getString(R.string.version_name, versionName)
}
