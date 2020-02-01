package com.pr656d.cattlenotes.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.databinding.ActivityMainBinding
import com.pr656d.cattlenotes.ui.login.LoginActivity
import com.pr656d.cattlenotes.utils.EventObserver
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val drawer by lazy { findViewById<DrawerLayout>(R.id.drawer_layout) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val model by viewModels<MainViewModel> { viewModelFactory }

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(
            this, R.layout.activity_main
        )

        binding.lifecycleOwner = this

        val navController = findNavController(R.id.nav_host_main)

        findViewById<NavigationView>(R.id.navigation_view).apply {
            setupWithNavController(navController)
            addHeaderView(layoutInflater.inflate(R.layout.nav_header, null))
        }

        findViewById<Toolbar>(R.id.toolbar).apply {
            val topLevelDestinations = setOf(
                R.id.cattleListScreen,
                R.id.navigation_timeline,
                R.id.navigation_milking,
                R.id.navigation_cashflow
            )

            val appBarConfig = AppBarConfiguration(topLevelDestinations, drawer)

            setupWithNavController(navController, appBarConfig)

            /**
             * Some fragments may need to do something when back pressed. By adding call back using
             * [activity.onBackPressedDispatcher.addCallBack()] in fragment you can handle
             * physical back pressed but not appBar navigation button. It also shows hamburger icon
             * to open [DrawerLayout].
             * So we have to route it to onBackPressed() of activity if it's not [topLevelDestinations].
             */
            setNavigationOnClickListener {
                with(navController.currentDestination?.id) {
                    if (topLevelDestinations.contains(this))
                        navController.navigateUp(appBarConfig)
                    else
                        onBackPressed()
                }
            }
        }

        model.redirectToLoginScreen.observe(this,
            EventObserver {
                startActivity(
                    Intent(this, LoginActivity::class.java)
                )
                finish()
            })
    }

    override fun onBackPressed() {
        drawer.apply {
            if (isDrawerOpen(GravityCompat.START))
                closeDrawer(GravityCompat.START)
            else
                super.onBackPressed()
        }
    }
}
