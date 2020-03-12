package com.pr656d.cattlenotes.ui.settings

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pr656d.cattlenotes.R
import com.pr656d.model.Theme
import dagger.android.support.DaggerAppCompatDialogFragment
import javax.inject.Inject

class ThemeSettingDialogFragment : DaggerAppCompatDialogFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<SettingsViewModel> { viewModelFactory }

    private lateinit var listAdapter: ArrayAdapter<ThemeHolder>

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        listAdapter = ArrayAdapter(requireContext(),
                android.R.layout.simple_list_item_single_choice)

        return MaterialAlertDialogBuilder(context)
                .setTitle(R.string.theme_title)
                .setSingleChoiceItems(listAdapter, 0) { dialog, position ->
                    listAdapter.getItem(position)?.theme?.let {
                        viewModel.setTheme(it)
                    }
                    dialog.dismiss()
                }
                .create()
    }

    @SuppressLint("FragmentLiveDataObserve")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.availableThemes.observe(this, Observer { themes ->
            listAdapter.clear()
            listAdapter.addAll(themes.map { theme -> ThemeHolder(theme, getTitleForTheme(theme)) })

            updateSelectedItem(viewModel.theme.value)
        })

        viewModel.theme.observe(this, Observer(::updateSelectedItem))
    }

    private fun updateSelectedItem(selected: Theme?) {
        val selectedPosition = (0 until listAdapter.count).indexOfFirst { index ->
            listAdapter.getItem(index)?.theme == selected
        }
        (dialog as AlertDialog).listView.setItemChecked(selectedPosition, true)
    }

    private fun getTitleForTheme(theme: Theme) = when (theme) {
        Theme.LIGHT -> getString(R.string.settings_theme_light)
        Theme.DARK -> getString(R.string.settings_theme_dark)
        Theme.SYSTEM -> getString(R.string.settings_theme_system)
        Theme.BATTERY_SAVER -> getString(R.string.settings_theme_battery)
    }

    companion object {
        fun newInstance() = ThemeSettingDialogFragment()
    }

    private data class ThemeHolder(val theme: Theme, val title: String) {
        override fun toString(): String = title
    }
}
