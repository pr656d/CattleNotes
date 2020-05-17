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

package com.pr656d.cattlenotes.ui.breeding.history.ofcattle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.databinding.FragmentBreedingHistoryOfCattleBinding
import com.pr656d.cattlenotes.ui.NavigationFragment
import com.pr656d.cattlenotes.ui.breeding.history.ofcattle.BreedingHistoryOfCattleFragmentDirections.Companion.toAddEditBreeding
import com.pr656d.shared.domain.result.EventObserver
import javax.inject.Inject

class BreedingHistoryOfCattleFragment : NavigationFragment() {

    companion object {
        const val TAG = "BreedingHistoryFragment"
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val model by viewModels<BreedingHistoryOfCattleViewModel> { viewModelFactory }

    private val args by navArgs<BreedingHistoryOfCattleFragmentArgs>()

    private lateinit var binding: FragmentBreedingHistoryOfCattleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        model.setCattle(args.cattleId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBreedingHistoryOfCattleBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = model
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.launchDeleteConfirmation.observe(viewLifecycleOwner, EventObserver {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_breeding)
                .setPositiveButton(R.string.delete) { _, _ ->
                    model.deleteBreeding(it, deleteConfirmation = true)
                }
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show()
        })

        model.launchEditBreeding.observe(viewLifecycleOwner, EventObserver {
            val (cattle, breeding) = it
            findNavController().navigate(
                toAddEditBreeding(
                    cattleId = cattle.id,
                    breedingId = breeding.id
                )
            )
        })

        model.showMessage.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(requireView(), requireContext().getString(it), Snackbar.LENGTH_LONG).show()
        })
    }
}