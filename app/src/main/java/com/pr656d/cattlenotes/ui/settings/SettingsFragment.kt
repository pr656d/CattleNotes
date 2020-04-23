package com.pr656d.cattlenotes.ui.settings

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.databinding.FragmentSettingsBinding
import com.pr656d.cattlenotes.ui.NavigationFragment
import com.pr656d.cattlenotes.ui.settings.SettingsFragmentDirections.Companion.toCredits
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

        model.navigateToThemeSelector.observe(viewLifecycleOwner, EventObserver {
            ThemeSettingDialogFragment.newInstance()
                .show(parentFragmentManager, null)
        })

        model.launchCredits.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(toCredits())
        })

        model.launchOpenSourceLicense.observe(viewLifecycleOwner, EventObserver {
            startActivity(
                Intent(requireContext(), OssLicensesMenuActivity::class.java)
            )
        })

        model.navigateToBreedingReminderTimeSelector.observe(viewLifecycleOwner, EventObserver {
            TimePickerDialog(requireContext(), R.style.TimePicker,
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    model.setBreedingReminderTime(TimeUtils.toLocalTime(hourOfDay, minute))
                },
                0, 0, false
            ).show()
        })
    }
}

@BindingAdapter("versionName")
fun setVersionName(view: TextView, versionName: String) {
    view.text = view.resources.getString(R.string.version_name, versionName)
}