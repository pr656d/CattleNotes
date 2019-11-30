package com.pr656d.cattlenotes.ui.main

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import com.google.android.material.bottomappbar.BottomAppBar
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.model.Cattle
import com.pr656d.cattlenotes.shared.base.BaseActivity
import com.pr656d.cattlenotes.shared.utils.common.viewModelProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity<MainViewModel>() {

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

    private val testCattle by lazy {
        Cattle("123456789012", "Janki", "HF")
    }

    override fun provideLayoutId(): Int = R.layout.activity_main

    override fun init() {
        viewModel = viewModelProvider(viewModelFactory)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomAppBar.apply {
                when {
                    topLevelDestinations.contains(destination.id) -> {
                        fabButton.setImageDrawable(getDrawable(R.drawable.ic_add_black))
                        fabAnimationMode = BottomAppBar.FAB_ANIMATION_MODE_SLIDE
                        fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
                        setNavigationIcon(R.drawable.ic_menu_black)
                        replaceMenu(R.menu.menu_main_appbar)
                    }
                }
            }
            viewModel.setActiveMenuItem(destination.id)
        }
    }

    override fun setupObservers() {
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

        fabButton.setOnClickListener {
            when (navController.currentDestination?.id) {
                R.id.cattleListScreen -> navController.navigate(R.id.cattleAddScreen)

                R.id.timelineScreen -> {
                }

                R.id.milkingScreen -> {
                }

                R.id.cashflowScreen -> {
                }
            }
        }

        bottomAppBar.run {
            setNavigationOnClickListener {
                navController.navigate(R.id.mainBottomNavigationDrawer)
            }
            setOnMenuItemClickListener { menuItem ->
                menuItem.onNavDestinationSelected(navController)
            }
        }
    }
}
