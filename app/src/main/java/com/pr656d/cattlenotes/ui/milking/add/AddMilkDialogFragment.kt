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

package com.pr656d.cattlenotes.ui.milking.add

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.databinding.FragmentAddEditMilkBinding
import com.pr656d.cattlenotes.utils.executeAfter
import com.pr656d.shared.domain.result.EventObserver
import dagger.android.support.DaggerAppCompatDialogFragment
import javax.inject.Inject

class AddMilkDialogFragment : DaggerAppCompatDialogFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val model by viewModels<AddMilkViewModel> { viewModelFactory }

    private lateinit var binding: FragmentAddEditMilkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set not cancellable
        isCancelable = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // We want to create a dialog, but we also want to use DataBinding for the content view.
        // We can do that by making an empty dialog and adding the content later.
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.add_milk)
            .setPositiveButton(R.string.save, null)     // We will handle button click later.
            .setNegativeButton(R.string.discard, null)  // We will handle button click later.
            .create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddEditMilkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.dismiss.observe(viewLifecycleOwner, EventObserver {
            dismiss()
        })

        binding.executeAfter {
            viewModel = model
            lifecycleOwner = viewLifecycleOwner
        }

        val alertDialog = (requireDialog() as AlertDialog)

        if (showsDialog) {
            alertDialog.setView(binding.root)
        }

        alertDialog.setOnShowListener {
            /** Handle button click to disable auto dismiss.*/
            alertDialog
                .getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener {
                    model.saveMilk()
                }

            /** Handle button click to disable auto dismiss. */
            alertDialog
                .getButton(AlertDialog.BUTTON_NEUTRAL)
                .setOnClickListener {
                    model.dismiss()
                }
        }

        model.showMessage.observe(viewLifecycleOwner, EventObserver {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        })
    }
}