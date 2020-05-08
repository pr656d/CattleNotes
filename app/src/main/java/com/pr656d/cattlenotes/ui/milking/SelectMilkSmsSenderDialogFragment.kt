package com.pr656d.cattlenotes.ui.milking

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.utils.parentViewModelProvider
import com.pr656d.model.Milk
import dagger.android.support.DaggerAppCompatDialogFragment
import javax.inject.Inject

class SelectMilkSmsSenderDialogFragment : DaggerAppCompatDialogFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: MilkingViewModel
    private lateinit var listAdapter: ArrayAdapter<SmsSourceHolder>

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        viewModel = parentViewModelProvider(viewModelFactory)

        listAdapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_list_item_single_choice)

        return MaterialAlertDialogBuilder(context)
            .setTitle(R.string.sms_source_title)
            .setSingleChoiceItems(listAdapter, -1) { dialog, position ->
                listAdapter.getItem(position)?.smsSource?.let {
                    viewModel.setSmsSource(it)
                }
                dialog.dismiss()
            }
            .create()
    }

    @SuppressLint("FragmentLiveDataObserve")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.availableMilkSmsSources.observe(this, Observer { sources ->
            listAdapter.clear()
            listAdapter.addAll(sources.map { source ->
                SmsSourceHolder(source, getTitleForMilkSmsSource(source))
            })
        })

        viewModel.smsSource?.let { updateSelectedItem(it) }
    }

    private fun updateSelectedItem(selected: Milk.Source.Sms) {
        val selectedPosition = (0 until listAdapter.count).indexOfFirst { index ->
            listAdapter.getItem(index)?.smsSource == selected
        }
        (dialog as AlertDialog).listView.setItemChecked(selectedPosition, true)
    }

    private fun getTitleForMilkSmsSource(milkSmsSource: Milk.Source.Sms) = when(milkSmsSource) {
        Milk.Source.Sms.BGAMAMCS -> getString(R.string.bgamamcs)
    }

    companion object {
        fun newInstance() = SelectMilkSmsSenderDialogFragment()
    }

    private data class SmsSourceHolder(val smsSource: Milk.Source.Sms, val title: String) {
        override fun toString(): String = title
    }
}