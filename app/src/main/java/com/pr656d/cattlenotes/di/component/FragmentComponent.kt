package com.pr656d.cattlenotes.di.component

import com.pr656d.cattlenotes.di.FragmentScope
import com.pr656d.cattlenotes.di.module.FragmentModule
import com.pr656d.cattlenotes.ui.cattle.CattleFragment
import com.pr656d.cattlenotes.ui.expense.CashFlowFragment
import com.pr656d.cattlenotes.ui.milking.MilkingFragment
import com.pr656d.cattlenotes.ui.timeline.TimelineFragment
import dagger.Component

@FragmentScope
@Component(
    dependencies = [ApplicationComponent::class],
    modules = [FragmentModule::class]
)
interface FragmentComponent {

    fun inject(fragment: CattleFragment)

    fun inject(fragment: TimelineFragment)

    fun inject(fragment: MilkingFragment)

    fun inject(fragment: CashFlowFragment)
}