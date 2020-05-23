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

package com.pr656d.cattlenotes.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.databinding.ActivityLoginBinding
import com.pr656d.cattlenotes.ui.MainActivity
import com.pr656d.cattlenotes.ui.login.LoginFragmentDirections.Companion.toSetupProfile
import com.pr656d.cattlenotes.utils.updateForTheme
import com.pr656d.shared.analytics.AnalyticsHelper
import com.pr656d.shared.domain.result.EventObserver
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class LoginActivity : DaggerAppCompatActivity() {

    @Inject lateinit var analyticsHelper: AnalyticsHelper

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

        model.theme.observe(this, Observer(::updateForTheme))

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.lifecycleOwner = this

        navController = findNavController(R.id.nav_host_login)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Set current nav id
            currentNavId = destination.id

            // Send screen view to analytics
            val destinationLabel = destination.label.toString()
            analyticsHelper.setScreenView(destinationLabel, this)
        }

        model.launchSetupProfileScreen.observe(this, EventObserver {
            navigateTo(toSetupProfile())
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

    private fun navigateTo(navDirections: NavDirections) {
        if (navDirections.actionId == currentNavId) {
            return // No need to navigate, already on the screen.
        }
        navController.navigate(navDirections)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            LoginFragment.CODE_SIGN_IN -> {
                val response = IdpResponse.fromResultIntent(data)
                if (resultCode == Activity.RESULT_OK) {
                    model.onLoginSuccess(response?.isNewUser ?: false)
                } else {
                    response?.error?.message?.let {
                        showMessage(it)
                    }
                    model.onLoginFail()
                }
            }
        }
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        if (!navController.popBackStack()) {
            finish()
        }
    }
}