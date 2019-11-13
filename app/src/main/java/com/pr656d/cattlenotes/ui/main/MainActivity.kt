package com.pr656d.cattlenotes.ui.main

import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.navigation.Navigation
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.shared.base.BaseActivity
import com.pr656d.cattlenotes.shared.utils.common.navigateTo
import com.pr656d.cattlenotes.shared.utils.common.viewModelProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity<MainViewModel>() {

    companion object {
        const val TAG = "MainActivity"
    }

    private lateinit var activeMenuItem: MenuItem

    override fun provideLayoutId(): Int = R.layout.activity_main

    override fun init() {
        viewModel = viewModelProvider(viewModelFactory)
    }

    override fun onBackPressed() {
        finish()
    }

    override fun setupView(savedInstanceState: Bundle?) {
        fabButton.setOnClickListener {
            when (activeMenuItem.itemId) {
                R.id.itemCattle -> navigateTo(R.id.nav_host_fragment, R.id.addCattleScreen)
                R.id.itemMilking -> { }
                R.id.itemCashflow -> { }
            }

        }

        bottomAppBar.run {
            /**
             * When app launches first time set active menu item as cattle item.
             * and set it's icon as selected
             */
            activeMenuItem = menu.findItem(R.id.itemCattle).apply {
                setIcon(R.drawable.ic_cattle_selected)
            }

            setNavigationOnClickListener {
                MainNavigationDrawerFragment.newInstance()
                    .show(supportFragmentManager, MainNavigationDrawerFragment.TAG)
            }

            setOnMenuItemClickListener { item ->
                fabButton.isEnabled = true

                if (isValidDestination(item.itemId))
                    when (item.itemId) {
                        R.id.itemCattle -> {
                            navigateTo(R.id.nav_host_fragment, R.id.cattleScreen)
                            item.setIcon(R.drawable.ic_cattle_selected)
                            true
                        }
                        R.id.itemTimeline -> {
                            navigateTo(R.id.nav_host_fragment, R.id.timelineScreen)
                            fabButton.isEnabled = false
                            item.setIcon(R.drawable.ic_timeline_selected)
                            true
                        }
                        R.id.itemMilking -> {
                            navigateTo(R.id.nav_host_fragment, R.id.milkingScreen)
                            item.setIcon(R.drawable.ic_milking_selected)
                            true
                        }
                        R.id.itemCashflow -> {
                            navigateTo(R.id.nav_host_fragment, R.id.cashflowScreen)
                            item.setIcon(R.drawable.ic_cashflow_selected)
                            true
                        }
                        else -> false
                    }.also {
                        /**
                         * If it's true then we are sure that the clicked item is from
                         * [R.id.itemCattle],[R.id.itemTimeline],[R.id.itemMilking],[R.id.itemCashflow]
                         * So now change icon of [activeMenuItem] as unselected
                         * then set [activeMenuItem] as [item]
                         */
                        if (it) {
                            menu.findItem(activeMenuItem.itemId).apply {
                                when (itemId) {
                                    R.id.itemCattle -> R.drawable.ic_cattle_unselected
                                    R.id.itemTimeline -> R.drawable.ic_timeline_unselected
                                    R.id.itemMilking -> R.drawable.ic_milking_unselected
                                    R.id.itemCashflow -> R.drawable.ic_cashflow_unselected
                                    else -> null
                                }?.let { icon -> setIcon(icon) }
                            }
                            activeMenuItem = item
                        }
                    }
                else false
            }
        }
    }

    private fun isValidDestination(@IdRes itemId: Int): Boolean =
        Navigation.findNavController(this@MainActivity, R.id.nav_host_fragment)
            .currentDestination?.id != when (itemId) {
            R.id.itemCattle -> R.id.cattleScreen
            R.id.itemTimeline -> R.id.timelineScreen
            R.id.itemMilking -> R.id.milkingScreen
            R.id.itemCashflow -> R.id.cashflowScreen
            else -> -1  // I don't want to pass null here
        }
}
