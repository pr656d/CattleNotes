/*
 * Copyright 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pr656d.cattlenotes.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.databinding.ActivityMainBinding
import com.pr656d.cattlenotes.databinding.NavHeaderBinding
import com.pr656d.cattlenotes.ui.login.LoginActivity
import com.pr656d.cattlenotes.utils.updateForTheme
import com.pr656d.shared.analytics.AnalyticsHelper
import com.pr656d.shared.domain.result.EventObserver
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(), NavigationHost {

    @Inject lateinit var analyticsHelper: AnalyticsHelper

    companion object {
        const val TAG = "MainActivity"

        /** Key for an int extra defining the initial navigation target. */
        const val EXTRA_NAVIGATION_ID = "extra.NAVIGATION_ID"

        private const val NAV_ID_NONE = -1

        private val TOP_LEVEL_DESTINATIONS = setOf(
            R.id.cattleListScreen,
            R.id.timelineScreen,
            R.id.milkingScreen,
            R.id.cashflowScreen,
            R.id.settingsScreen,
            R.id.profileScreen
        )
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    val model by viewModels<MainViewModel> { viewModelFactory }

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawer: DrawerLayout
    private lateinit var navigation: NavigationView
    private lateinit var navController: NavController
    private lateinit var navHeaderBinding: NavHeaderBinding

    private var currentNavId = NAV_ID_NONE

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        model.theme.observe(this, Observer(::updateForTheme))

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.lifecycleOwner = this

        drawer = binding.drawerLayout

        navigation = binding.navigationView

        navHeaderBinding = NavHeaderBinding.inflate(layoutInflater).apply {
            lifecycleOwner = this@MainActivity
        }

        navController = findNavController(R.id.nav_host_main)

        navigation.addHeaderView(navHeaderBinding.root)

        navigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Set current nav id
            currentNavId = destination.id

            // Send screen view to analytics
            val destinationLabel = destination.label.toString()
            analyticsHelper.setScreenView(destinationLabel, this)

            // Handle Drawer lock mode
            val isTopLevelDestination = TOP_LEVEL_DESTINATIONS.contains(destination.id)
            val lockMode = if (isTopLevelDestination) {
                DrawerLayout.LOCK_MODE_UNLOCKED
            } else {
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED
            }
            drawer.setDrawerLockMode(lockMode)
        }

        if (savedInstanceState == null) {
            // default do not navigate to anywhere.
            // We se deep link so this navigation will overlap deep link navigation.
            val initialNavId = intent.getIntExtra(EXTRA_NAVIGATION_ID, NAV_ID_NONE)
            navigation.setCheckedItem(initialNavId) // doesn't trigger listener
            navigateTo(initialNavId)
        }

        model.redirectToLoginScreen.observe(
            this,
            EventObserver {
                startActivity(
                    Intent(this, LoginActivity::class.java)
                )
                finishAffinity()
            }
        )
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        currentNavId = navigation.checkedItem?.itemId ?: NAV_ID_NONE
    }

    private fun navigateTo(navId: Int) {
        if (navId == currentNavId || navId == NAV_ID_NONE) {
            return // user tapped the current item
        }
        navController.navigate(navId)
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(navigation)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun registerToolbarWithNavigation(toolbar: Toolbar) {
        toolbar.apply {
            val appBarConfiguration = AppBarConfiguration(TOP_LEVEL_DESTINATIONS, drawer)

            setupWithNavController(navController, appBarConfiguration)

            /**
             * Some fragments may need to do something when back pressed. By adding call back using
             * [activity.onBackPressedDispatcher.addCallBack()] in fragment you can handle
             * physical back pressed but not appBar navigation button. It also shows hamburger icon
             * to open [DrawerLayout].
             * So we have to route it to onBackPressed() of activity if it's not [TOP_LEVEL_DESTINATIONS].
             */
            setNavigationOnClickListener {
                with(navController.currentDestination?.id) {
                    val isTopLevelDestination = TOP_LEVEL_DESTINATIONS.contains(this)
                    if (isTopLevelDestination)
                        navController.navigateUp(appBarConfiguration)
                    else
                        onBackPressed()
                }
            }
        }
    }
}
