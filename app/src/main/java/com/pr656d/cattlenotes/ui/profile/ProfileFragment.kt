package com.pr656d.cattlenotes.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.pr656d.cattlenotes.databinding.FragmentProfileBinding
import com.pr656d.cattlenotes.ui.NavigationFragment
import com.pr656d.cattlenotes.ui.profile.ProfileFragmentDirections.Companion.toLogin
import com.pr656d.shared.domain.result.EventObserver
import javax.inject.Inject

class ProfileFragment : NavigationFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val model by viewModels<ProfileViewModel> { viewModelFactory }

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = model
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.navigateUp.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigateUp()
        })

        model.launchLoginScreen.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(toLogin())
        })
    }

    private fun showMessage(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showMessage(@StringRes messageId: Int) = showMessage(getString(messageId))

}