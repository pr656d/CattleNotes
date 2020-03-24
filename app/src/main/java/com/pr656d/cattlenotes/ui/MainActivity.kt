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
import com.pr656d.shared.domain.result.EventObserver
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(), NavigationHost {

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
            R.id.aboutAppScreen
        )
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawer: DrawerLayout
    private lateinit var navigation: NavigationView
    private lateinit var navController: NavController
    private lateinit var navHeaderBinding: NavHeaderBinding

    private var currentNavId = NAV_ID_NONE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val model by viewModels<MainViewModel> { viewModelFactory }

        // Update for Dark Mode straight away
        updateForTheme(model.currentTheme)

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
            currentNavId = destination.id
            val isTopLevelDestination = TOP_LEVEL_DESTINATIONS.contains(destination.id)
            val lockMode = if (isTopLevelDestination) {
                DrawerLayout.LOCK_MODE_UNLOCKED
            } else {
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED
            }
            drawer.setDrawerLockMode(lockMode)
        }

        if (savedInstanceState == null) {
            // default showing cattle list
            val initialNavId = intent.getIntExtra(EXTRA_NAVIGATION_ID, R.id.cattle_graph)
            navigation.setCheckedItem(initialNavId) // doesn't trigger listener
            navigateTo(initialNavId)
        }

        model.theme.observe(this, Observer(::updateForTheme))

        model.redirectToLoginScreen.observe(this, EventObserver {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        })
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        currentNavId = navigation.checkedItem?.itemId ?: NAV_ID_NONE
    }

    private fun navigateTo(navId: Int) {
        if (navId == currentNavId) {
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
