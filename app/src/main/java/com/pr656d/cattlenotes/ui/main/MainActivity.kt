package com.pr656d.cattlenotes.ui.main

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.shared.base.BaseActivity
import com.pr656d.cattlenotes.shared.utils.common.viewModelProvider
import com.pr656d.cattlenotes.ui.cashflow.CashflowFragment
import com.pr656d.cattlenotes.ui.cattle.CattleFragment
import com.pr656d.cattlenotes.ui.main.MainFragmentNavigation.*
import com.pr656d.cattlenotes.ui.milking.MilkingFragment
import com.pr656d.cattlenotes.ui.timeline.TimelineFragment
import com.pr656d.cattlenotes.utils.common.EventObserver
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity<MainViewModel>() {

    companion object {
        const val TAG = "MainActivity"
    }

    private var activeFragment: Fragment? = null
    private lateinit var activeMenuItem: MenuItem

    override fun provideLayoutId(): Int = R.layout.activity_main

    override fun setupViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
    }

    override fun setupObservers() {
        super.setupObservers()

        viewModel.fragmentNavigation.observe(this, EventObserver { navigateTo ->
            when (navigateTo) {
                CATTLE_FRAGMENT -> switchFragments(CattleFragment.TAG)
                TIMELINE_FRAGMENT -> switchFragments(TimelineFragment.TAG)
                MILKING_FRAGMENT -> switchFragments(MilkingFragment.TAG)
                CASHFLOW_FRAGMENT -> switchFragments(CashflowFragment.TAG)
            }
        })
    }

    override fun setupView(savedInstanceState: Bundle?) {
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

            setOnMenuItemClickListener { menuItem ->
                /**
                 * When another item is clicked change icon of [activeMenuItem] to unselected.
                 */
                activeMenuItem.let { activeItem ->
                    /**
                     * When clicked item is not
                     * [R.id.itemCattle],[R.id.itemTimeline],[R.id.itemMilking],[R.id.itemCashflow]
                     * then don't set [activeMenuItem]'s icon as unselected.
                     *
                     * new activity will be started for others.
                     */
                    if (
                        menuItem.itemId == R.id.itemCattle ||
                        menuItem.itemId == R.id.itemTimeline ||
                        menuItem.itemId == R.id.itemMilking ||
                        menuItem.itemId == R.id.itemCashflow
                    )
                        menu.findItem(activeItem.itemId).apply {
                            when (itemId) {
                                R.id.itemCattle -> R.drawable.ic_cattle_unselected
                                R.id.itemTimeline -> R.drawable.ic_timeline_unselected
                                R.id.itemMilking -> R.drawable.ic_milking_unselected
                                R.id.itemCashflow -> R.drawable.ic_cashflow_unselected
                                else -> null
                            }?.let { icon -> setIcon(icon) }
                        }
                }

                val result = when (menuItem.itemId) {
                    R.id.itemCattle -> {
                        viewModel.onCattleSelected()
                        menuItem.setIcon(R.drawable.ic_cattle_selected)
                        true
                    }
                    R.id.itemTimeline -> {
                        viewModel.onTimelineSelected()
                        menuItem.setIcon(R.drawable.ic_timeline_selected)
                        true
                    }
                    R.id.itemMilking -> {
                        viewModel.onMilkingSelected()
                        menuItem.setIcon(R.drawable.ic_milking_selected)
                        true
                    }
                    R.id.itemCashflow -> {
                        viewModel.onCashFlowSelected()
                        menuItem.setIcon(R.drawable.ic_cashflow_selected)
                        true
                    }
                    else -> false
                }

                /**
                 * Don't update [activeMenuItem] for any other than below list
                 * [R.id.itemCattle],[R.id.itemTimeline],[R.id.itemMilking],[R.id.itemCashflow]
                 *
                 * Also result should be true
                 */
                if (result && (
                            menuItem.itemId == R.id.itemCattle ||
                            menuItem.itemId == R.id.itemTimeline ||
                            menuItem.itemId == R.id.itemMilking ||
                            menuItem.itemId == R.id.itemCashflow
                            )
                ) { activeMenuItem = menuItem }

                result
            }
        }
    }

    private fun switchFragments(tag: String) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        var fragment: Fragment? = null

        when (tag) {
            activeFragment?.tag -> return

            CattleFragment.TAG -> {
                fragment = supportFragmentManager.findFragmentByTag(CattleFragment.TAG) as CattleFragment?
                if (fragment == null) {
                    fragment = CattleFragment.newInstance()
                    fragmentTransaction.add(R.id.fragment_container, fragment, CattleFragment.TAG)
                }
            }

            TimelineFragment.TAG -> {
                fragment = supportFragmentManager.findFragmentByTag(TimelineFragment.TAG) as TimelineFragment?
                if (fragment == null) {
                    fragment = TimelineFragment.newInstance()
                    fragmentTransaction.add(R.id.fragment_container, fragment, TimelineFragment.TAG)
                }
            }

            MilkingFragment.TAG -> {
                fragment = supportFragmentManager.findFragmentByTag(MilkingFragment.TAG) as MilkingFragment?
                if (fragment == null) {
                    fragment = MilkingFragment.newInstance()
                    fragmentTransaction.add(R.id.fragment_container, fragment, MilkingFragment.TAG)
                }
            }

            CashflowFragment.TAG -> {
                fragment = supportFragmentManager.findFragmentByTag(CashflowFragment.TAG) as CashflowFragment?
                if (fragment == null) {
                    fragment = CashflowFragment.newInstance()
                    fragmentTransaction.add(R.id.fragment_container, fragment, CashflowFragment.TAG)
                }
            }
        }

        fragment?.let { fragmentTransaction.show(it) }

        if (activeFragment != null) fragmentTransaction.hide(activeFragment as Fragment)

        fragmentTransaction.commit()

        activeFragment = fragment
    }
}
