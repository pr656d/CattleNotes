package com.pr656d.cattlenotes.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.databinding.ActivityLoginBinding
import com.pr656d.cattlenotes.ui.MainActivity
import com.pr656d.cattlenotes.ui.login.LoginFragmentDirections.Companion.toSetupProfile
import com.pr656d.cattlenotes.utils.updateForTheme
import com.pr656d.shared.domain.result.EventObserver
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class LoginActivity : DaggerAppCompatActivity() {

    companion object {

        /** Key for an int extra defining the initial navigation target. */
        const val EXTRA_NAVIGATION_ID = "extra.NAVIGATION_ID"

        private const val NAV_ID_NONE = -1
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val model by viewModels<LoginViewModel> { viewModelFactory }

    private lateinit var binding: ActivityLoginBinding
    private lateinit var navController: NavController

    private var currentNavId = NAV_ID_NONE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Update for Dark Mode straight away
        updateForTheme(model.currentTheme)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.lifecycleOwner = this
        navController = findNavController(R.id.nav_host_login)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            currentNavId = destination.id
        }

        model.theme.observe(this, Observer(::updateForTheme))

        model.launchSetupProfileScreen.observe(this, EventObserver {
            if (navController.currentDestination?.id != R.id.setupProfileScreen)
                navController.navigate(toSetupProfile())
        })

        model.launchLoginScreen.observe(this, EventObserver {
            navigateTo(R.id.loginScreen)
        })

        model.launchMainScreen.observe(this, EventObserver {
            startActivity(
                Intent(this, MainActivity::class.java)
            )
            finish()
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(EXTRA_NAVIGATION_ID, currentNavId)

        super.onSaveInstanceState(outState)
    }

    private fun navigateTo(navId: Int) {
        if (navId == currentNavId) {
            return // No need to navigate, already on the screen.
        }
        navController.navigate(navId)
    }

    override fun onBackPressed() {
        super.onBackPressed()

        if (!navController.popBackStack()) {
            finish()
        }
    }
}