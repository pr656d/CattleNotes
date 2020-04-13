package com.pr656d.cattlenotes.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.firebase.ui.auth.AuthUI
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.databinding.FragmentLoginBinding
import com.pr656d.shared.domain.result.EventObserver
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class LoginFragment : DaggerFragment() {

    companion object {
        const val TAG = "LoginFragment"

        const val CODE_SIGN_IN = 111
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val model by activityViewModels<LoginViewModel> { viewModelFactory }

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = model
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.launchFirebaseAuthUI.observe(viewLifecycleOwner, EventObserver {
            val providers = mutableListOf(
                AuthUI.IdpConfig.PhoneBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build()
            )

            activity?.startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setLogo(R.drawable.logo)
                    .setTheme(R.style.FirebaseTheme)
                    .build(),
                CODE_SIGN_IN
            )
        })
    }
}
