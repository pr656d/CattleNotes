package com.pr656d.cattlenotes.ui.main

import android.os.Bundle
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

    private var activeFragment: Fragment? = null

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
            setOnMenuItemClickListener {
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
                    R.id.itemCashflow -> {
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
