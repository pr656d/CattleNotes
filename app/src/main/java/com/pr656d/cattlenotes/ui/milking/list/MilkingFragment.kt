/*
 * Copyright (c) 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pr656d.cattlenotes.ui.milking.list

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.databinding.FragmentMilkingBinding
import com.pr656d.cattlenotes.ui.NavigationFragment
import com.pr656d.cattlenotes.ui.milking.add.AddMilkDialogFragment
import com.pr656d.cattlenotes.ui.milking.sms.SelectMilkSmsSenderDialogFragment
import com.pr656d.cattlenotes.utils.isPermissionGranted
import com.pr656d.shared.domain.result.EventObserver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

class MilkingFragment : NavigationFragment() {

    companion object {
        const val TAG = "MilkingFragment"
        const val PERMISSION_REQUEST_AT_MILKING = 101
        val PERMISSIONS_REQUIRED = listOf(
            Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS
        )
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val model by viewModels<MilkingViewModel> { viewModelFactory }
    private lateinit var binding: FragmentMilkingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkAndRequestPermission()
    }

    override fun onResume() {
        super.onResume()

        // Update if permission changed.
        model.setPermissionsGranted(isAllPermissionsGranted())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMilkingBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = model
        }

        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.menu_item_sync_with_sms -> {
                    model.syncWithSmsMessages()
                    true
                }

                R.id.menu_item_add_milk -> {
                    model.addMilk()
                    true
                }

                R.id.menu_item_change_sms_source -> {
                    model.changeSmsSource()
                    true
                }

                else -> false
            }
        }

        model.requestPermissions.observe(viewLifecycleOwner, EventObserver {
            requestPermission()
        })

        model.showPermissionExplanation.observe(viewLifecycleOwner, EventObserver {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.sms_permission_required)
                .setMessage(R.string.sms_permission_explanation_for_amc_feature)
                .setPositiveButton(R.string.ok) { _, _ ->
                    model.requestPermission()
                }
                .create()
                .show()
        })

        model.navigateToSmsSourceSelector.observe(viewLifecycleOwner, EventObserver {
            SelectMilkSmsSenderDialogFragment.newInstance()
                .show(childFragmentManager, null)
        })

        model.saveNewMilkConfirmationDialog.observe(viewLifecycleOwner, EventObserver {
            val dialog = MaterialAlertDialogBuilder(requireContext())

            if (it.isEmpty()) {
                dialog
                    .setTitle(R.string.no_milk_found)
                    .setPositiveButton(R.string.ok, null)
            } else {
                dialog
                    .setTitle(getString(R.string.milk_found, it.count()))
                    .setPositiveButton(R.string.save) { _, _ ->
                        model.saveMilk(it)
                    }
                    .setNegativeButton(R.string.cancel, null)
            }

            dialog.create().show()
        })

        model.navigateToAddMilk.observe(viewLifecycleOwner, EventObserver {
            AddMilkDialogFragment().show(childFragmentManager, null)
        })

        model.smsSource.observe(viewLifecycleOwner, Observer {
            // Just observe so that value can be updated.
        })

        model.showMessage.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show()
        })
    }

    /**
     * Check whether all required permissions are granted or not.
     */
    private fun checkAndRequestPermission() {
        if (!isAllPermissionsGranted()) {
            val showPermissionRational = getPendingPermissions().shouldShowPermissionRationale()

            if (showPermissionRational) {
                // User have previously denied the permission request.
                // Show explanation.
                model.showPermissionExplanation()
                return
            } else {
                // Asking for first time.
                // Request for permission.
                requestPermission()
            }
        }
    }

    /**
     * Request for permission.
     */
    private fun requestPermission() {
        val pendingPermissions = getPendingPermissions().also {
            // Safe check. Return, no permissions found to ask for.
            if (it.isEmpty()) return
        }.toTypedArray()

        requestPermissions(pendingPermissions,
            PERMISSION_REQUEST_AT_MILKING
        )
    }

    /**
     * Any permission from this list needs to be shown explanation.
     */
    private fun List<String>.shouldShowPermissionRationale(): Boolean =
        any { ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), it) }

    /**
     * Return pending permissions from permissions list.
     */
    private fun getPendingPermissions(): List<String> =
        PERMISSIONS_REQUIRED.filter { !requireActivity().isPermissionGranted(it) }

    /**
     * Whether all permissions are granted or not.
     */
    private fun isAllPermissionsGranted() = getPendingPermissions().isEmpty()

    private fun openSettingsScreen() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.go_to_settings_for_permission_title)
            .setMessage(R.string.go_to_settings_for_sms_permission_message)
            .setPositiveButton(R.string.go_to_settings) { _, _ ->
                // Open settings page.
                val intent = Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", requireActivity().packageName, null)
                }
                startActivity(intent)
            }
            .setNegativeButton(R.string.not_now, null)
            .create()
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_AT_MILKING) {
            /**
             * Check if all permissions are granted which we have requested for.
             */
            val isGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }

            // Do not update permission from here. We update permission status onResume().

            /**
             * when [shouldShowPermissionRationale] returns
             *      true -> User has previously denied the permission.
             *      false -> User have denied the request and checked don't ask again.
             */
            val permissionDeniedAndCheckedDontAskAgain =
                !getPendingPermissions().shouldShowPermissionRationale()

            if (!isGranted && permissionDeniedAndCheckedDontAskAgain) {
                // User denied for permission and checked don't ask again.
                // Open settings screen of app.
                openSettingsScreen()
            }
        }
    }
}