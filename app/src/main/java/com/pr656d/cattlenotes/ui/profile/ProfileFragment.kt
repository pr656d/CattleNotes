package com.pr656d.cattlenotes.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.databinding.FragmentProfileBinding
import com.pr656d.cattlenotes.ui.NavigationFragment
import com.pr656d.cattlenotes.ui.profile.ProfileFragmentDirections.Companion.toAddEditProfile
import com.pr656d.shared.domain.result.EventObserver
import javax.inject.Inject

class ProfileFragment : NavigationFragment() {

    @Inject lateinit var firebaseAuth: FirebaseAuth

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val model by viewModels<ProfileViewModel> { viewModelFactory }

    private lateinit var binding: FragmentProfileBinding
    private lateinit var genderGroup: MaterialButtonToggleGroup

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

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.menu_item_logout -> {
                    model.logout()
                    true
                }
                else -> false
            }
        }

        genderGroup = binding.includeSelectGenderLayout.toggleGroupGender

        genderGroup.addOnButtonCheckedListener { _, checkedId, _ ->
            val previousCheckedId = model.selectedGenderId.value
            if (previousCheckedId != checkedId)
                model.selectedGenderId.postValue(checkedId)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.selectedGenderId.observe(viewLifecycleOwner, Observer {
            if (it == null)
                genderGroup.clearChecked()
            else
                genderGroup.check(it)
        })

        model.navigateUp.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigateUp()
        })

        model.launchEditProfile.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(toAddEditProfile())
        })

        model.launchLogout.observe(viewLifecycleOwner, EventObserver {
            firebaseAuth.signOut()
        })

        model.showLogoutConfirmation.observe(viewLifecycleOwner, EventObserver {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.logout_message)
                .setPositiveButton(R.string.logout) { _, _ ->
                    model.logout(logoutConfirmation = true)
                }
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show()
        })
    }
}