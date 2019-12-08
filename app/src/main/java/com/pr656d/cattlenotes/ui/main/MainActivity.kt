package com.pr656d.cattlenotes.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
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
        hashSetOf(
            R.id.cattleListScreen, R.id.timelineScreen, R.id.milkingScreen,
            R.id.cashflowScreen
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

        viewModel.activeMenuItem.observe(this, Observer { menuItem ->
            bottomAppBar.apply {
                menuItem.previousItem?.let {
                    menu.findItem(it.itemId)?.setIcon(it.unselectedIcon)
                }
                menuItem.currentItem.let {
                    menu.findItem(it.itemId)?.setIcon(it.selectedIcon)
                }
            }
        })
    }

    override fun setupView(savedInstanceState: Bundle?) {
        NavigationUI.setupWithNavController(
            toolbar,
            navController,
            AppBarConfiguration(topLevelDestinations)
        )

        bottomAppBar.run {
            setNavigationOnClickListener {
                navController.navigate(R.id.mainBottomNavigationDrawer)
            }
            setOnMenuItemClickListener { menuItem ->
                viewModel.setActiveMenuItem(menuItem.itemId)
                menuItem.onNavDestinationSelected(navController)
            }
        }
    }
}
