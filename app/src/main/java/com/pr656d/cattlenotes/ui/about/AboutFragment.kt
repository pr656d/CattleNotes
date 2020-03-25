package com.pr656d.cattlenotes.ui.about

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.pr656d.cattlenotes.databinding.FragmentAboutBinding
import com.pr656d.cattlenotes.ui.NavigationFragment
import com.pr656d.cattlenotes.ui.about.AboutFragmentDirections.Companion.toCredits
import com.pr656d.shared.domain.result.EventObserver
import javax.inject.Inject

class AboutFragment : NavigationFragment() {

    companion object {
        const val TAG = "AboutAppFragment"
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val model by viewModels<AboutViewModel> { viewModelFactory }

    private lateinit var binding: FragmentAboutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAboutBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = model
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.launchCredits.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(toCredits())
        })

        model.launchOpenSourceLicense.observe(viewLifecycleOwner, EventObserver {
            startActivity(
                Intent(requireContext(), OssLicensesMenuActivity::class.java)
            )
        })
    }
}