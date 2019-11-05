package com.pr656d.cattlenotes.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.pr656d.cattlenotes.R
import com.pr656d.cattlenotes.di.component.ActivityComponent
import com.pr656d.cattlenotes.ui.base.BaseActivity
import com.pr656d.cattlenotes.ui.cattle.CattleFragment
import com.pr656d.cattlenotes.ui.cashflow.CashFlowFragment
import com.pr656d.cattlenotes.ui.milking.MilkingFragment
import com.pr656d.cattlenotes.ui.timeline.TimelineFragment
import com.pr656d.cattlenotes.ui.main.MainViewModel.MainFragmentNavigation
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity<MainViewModel>() {

    companion object {
        const val TAG = "MainActivity"


    }

    private var activeFragment: Fragment? = null

    override fun provideLayoutId(): Int = R.layout.activity_main

    override fun injectDependencies(activityComponent: ActivityComponent) =
        activityComponent.inject(this)

    override fun setup() { }

    override fun setupObservers() {
        super.setupObservers()

        viewModel.fragmentNavigation.observe(this, Observer {
            it.getIfNotHandled()?.run {
                when (this) {
                    MainFragmentNavigation.CATTLE_FRAGMENT -> switchFragments(CattleFragment.TAG)
                    MainFragmentNavigation.TIMELINE_FRAGMENT -> switchFragments(TimelineFragment.TAG)
                    MainFragmentNavigation.MILKING_FRAGMENT -> switchFragments(MilkingFragment.TAG)
                    MainFragmentNavigation.CASHFLOW_FRAGMENT -> switchFragments(CashFlowFragment.TAG)
                }
            }
        })
    }

    override fun setupView(savedInstanceState: Bundle?) {
        bottomNavigation.run {
            itemIconTintList = null
            setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.itemCattle -> {
                        viewModel.onCattleSelected()
                        true
                    }
                    R.id.itemTimeline -> {
                        viewModel.onTimelineSelected()
                        true
                    }
                    R.id.itemMilking -> {
                        viewModel.onMilkingSelected()
                        true
                    }
                    R.id.itemExpense -> {
                        viewModel.onCashFlowSelected()
                        true
                    }
                    else -> false
                }
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
                    fragmentTransaction.add(R.id.containerFragment, fragment, CattleFragment.TAG)
                }
            }

            TimelineFragment.TAG -> {
                fragment = supportFragmentManager.findFragmentByTag(TimelineFragment.TAG) as TimelineFragment?
                if (fragment == null) {
                    fragment = TimelineFragment.newInstance()
                    fragmentTransaction.add(R.id.containerFragment, fragment, TimelineFragment.TAG)
                }
            }

            MilkingFragment.TAG -> {
                fragment = supportFragmentManager.findFragmentByTag(MilkingFragment.TAG) as MilkingFragment?
                if (fragment == null) {
                    fragment = MilkingFragment.newInstance()
                    fragmentTransaction.add(R.id.containerFragment, fragment, MilkingFragment.TAG)
                }
            }

            CashFlowFragment.TAG -> {
                fragment = supportFragmentManager.findFragmentByTag(CashFlowFragment.TAG) as CashFlowFragment?
                if (fragment == null) {
                    fragment = CashFlowFragment.newInstance()
                    fragmentTransaction.add(R.id.containerFragment, fragment, CashFlowFragment.TAG)
                }
            }
        }

        fragmentTransaction.show(fragment!!)

        if (activeFragment != null) fragmentTransaction.hide(activeFragment as Fragment)

        fragmentTransaction.commit()

        activeFragment = fragment
    }
}
