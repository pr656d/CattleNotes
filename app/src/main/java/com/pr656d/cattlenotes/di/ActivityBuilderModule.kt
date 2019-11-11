package com.pr656d.cattlenotes.di

import com.pr656d.cattlenotes.shared.di.ActivityScoped
import com.pr656d.cattlenotes.ui.cashflow.CashflowBuilderModule
import com.pr656d.cattlenotes.ui.cattle.CattleBuilderModule
import com.pr656d.cattlenotes.ui.cattle.details.CattleDetailsActivity
import com.pr656d.cattlenotes.ui.cattle.details.CattleDetailsBuilderModule
import com.pr656d.cattlenotes.ui.launch.LaunchModule
import com.pr656d.cattlenotes.ui.launch.LauncherActivity
import com.pr656d.cattlenotes.ui.login.LoginActivity
import com.pr656d.cattlenotes.ui.login.LoginModule
import com.pr656d.cattlenotes.ui.main.MainActivity
import com.pr656d.cattlenotes.ui.main.MainModule
import com.pr656d.cattlenotes.ui.milking.MilkingBuilderModule
import com.pr656d.cattlenotes.ui.timeline.TimelineBuilderModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * We want Dagger.Android to create a Subcomponent which has a parent Component of whichever module
 * ActivityBuilderModule is on, in our case that will be [AppComponent]. You never
 * need to tell [AppComponent] that it is going to have all these subcomponents
 * nor do you need to tell these subcomponents that [AppComponent] exists.
 * We are also telling Dagger.Android that this generated SubComponent needs to include the
 * specified modules and be aware of a scope annotation [@ActivityScoped].
 * When Dagger.Android annotation processor runs it will create 2 subcomponents for us.
 */
@Module
@Suppress("UNUSED")
abstract class ActivityBuilderModule {

    @ActivityScoped
    @ContributesAndroidInjector(modules = [LaunchModule::class])
    internal abstract fun launcherActivity(): LauncherActivity

    @ActivityScoped
    @ContributesAndroidInjector(
        modules = [
            LoginModule::class
        ]
    )
    internal abstract fun loginActivity(): LoginActivity

    @ActivityScoped
    @ContributesAndroidInjector(
        modules = [
            // activity
            MainModule::class,
            // fragments
            CattleBuilderModule::class,
            TimelineBuilderModule::class,
            MilkingBuilderModule::class,
            CashflowBuilderModule::class
        ]
    )
    internal abstract fun mainActivity(): MainActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [CattleDetailsBuilderModule::class])
    internal abstract fun cattleDetailsActivity(): CattleDetailsActivity
}