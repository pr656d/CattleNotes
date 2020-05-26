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

package com.pr656d.cattlenotes.ui.milking.sms

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.ui.milking.list.MilkingViewModel
import com.pr656d.cattlenotes.utils.getStringId
import com.pr656d.cattlenotes.utils.parentViewModelProvider
import com.pr656d.model.Milk
import dagger.android.support.DaggerAppCompatDialogFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

class SelectMilkSmsSenderDialogFragment : DaggerAppCompatDialogFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: MilkingViewModel

    private lateinit var listAdapter: ArrayAdapter<SmsSourceHolder>

    private var selectedSmsSource: Milk.Source.Sms? = null

    @ExperimentalCoroutinesApi
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        viewModel = parentViewModelProvider(viewModelFactory)

        listAdapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_list_item_single_choice)

        return MaterialAlertDialogBuilder(context)
            .setTitle(R.string.sms_source_title)
            .setPositiveButton(R.string.ok, null)   // We will handle it later
            .setNegativeButton(R.string.cancel, null) // We will handle it later
            .setNeutralButton(R.string.reset, null)   // We will handle it later
            .setSingleChoiceItems(listAdapter, -1) { _, position ->
                listAdapter.getItem(position)?.smsSource?.let {
                    selectedSmsSource = it
                }
            }
            .create()
    }

    @ExperimentalCoroutinesApi
    @SuppressLint("FragmentLiveDataObserve")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.availableMilkSmsSources.observe(this, Observer { sources ->
            listAdapter.clear()
            listAdapter.addAll(sources.map { source ->
                SmsSourceHolder(
                    source,
                    getTitleForMilkSmsSource(source)
                )
            })

            viewModel.smsSource.value?.let {
                updateSelectedItem(it)
            }
        })

        viewModel.smsSource.observe(this, Observer(::updateSelectedItem))

        val alertDialog = (requireDialog() as AlertDialog)

        alertDialog.setOnShowListener {
            /** Handle button click to disable auto dismiss.*/
            alertDialog
                .getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener {
                    viewModel.setSmsSource(selectedSmsSource)
                    dismiss()
                }

            /** Handle button click to disable auto dismiss.*/
            alertDialog
                .getButton(AlertDialog.BUTTON_NEGATIVE)
                .setOnClickListener {
                    dismiss()
                }

            /** Handle button click to disable auto dismiss. */
            alertDialog
                .getButton(AlertDialog.BUTTON_NEUTRAL)
                .setOnClickListener {
                    selectedSmsSource = null
                    updateSelectedItem(null)
                }
        }
    }

    private fun updateSelectedItem(selected: Milk.Source.Sms?) {
        val selectedPosition = (0 until listAdapter.count).indexOfFirst { index ->
            listAdapter.getItem(index)?.smsSource == selected
        }
        (dialog as AlertDialog).listView.setItemChecked(selectedPosition, true)
    }

    private fun getTitleForMilkSmsSource(milkSmsSource: Milk.Source.Sms) =
        getString(milkSmsSource.getStringId())

    companion object {
        fun newInstance() =
            SelectMilkSmsSenderDialogFragment()
    }

    private data class SmsSourceHolder(val smsSource: Milk.Source.Sms, val title: String) {
        override fun toString(): String = title
    }
}