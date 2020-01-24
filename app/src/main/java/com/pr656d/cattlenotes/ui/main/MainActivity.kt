package com.pr656d.cattlenotes.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.shared.base.BaseActivity
import com.pr656d.cattlenotes.ui.login.LoginActivity
import com.pr656d.cattlenotes.utils.common.EventObserver
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private val navController by lazy { findNavController(R.id.nav_host_main) }
    private val topLevelDestinations by lazy {
        setOf(
            R.id.navigation_cattle_list,
            R.id.navigation_timeline,
            R.id.navigation_milking,
            R.id.navigation_cashflow
        )
    }

    private val viewModel by viewModels<MainViewModel> { viewModelFactory }

    override fun provideLayoutId(): Int = R.layout.activity_main

    override fun setupObservers() {
        viewModel.redirectToLoginScreen.observe(this, EventObserver {
            startActivity(
                Intent(this, LoginActivity::class.java)
            )
            finish()
        })
    }

    override fun setupView(savedInstanceState: Bundle?) {
        navigation_view.apply {
            setupWithNavController(navController)
            addHeaderView(layoutInflater.inflate(R.layout.nav_header, null))
        }

        toolbar.apply {
            AppBarConfiguration(topLevelDestinations, drawer_layout).let { appBarConfig ->
                setupWithNavController(navController, appBarConfig)

                setNavigationOnClickListener {
                    if (topLevelDestinations.contains(navController.currentDestination?.id))
                        navController.navigateUp(appBarConfig)
                    else
                        onBackPressed()
                }
            }
        }
    }

    override fun onBackPressed() {
        drawer_layout.apply {
            if (isDrawerOpen(GravityCompat.START))
                closeDrawer(GravityCompat.START)
            else
                super.onBackPressed()
        }
    }
}
