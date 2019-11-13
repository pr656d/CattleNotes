package com.pr656d.cattlenotes.di

import com.pr656d.cattlenotes.shared.di.ActivityScoped
import com.pr656d.cattlenotes.ui.cashflow.CashflowBindingModule
import com.pr656d.cattlenotes.ui.cattle.CattleBindingModule
import com.pr656d.cattlenotes.ui.cattle.add.AddEditCattleActivity
import com.pr656d.cattlenotes.ui.cattle.add.AddEditCattleBindingModule
import com.pr656d.cattlenotes.ui.cattle.details.CattleDetailsActivity
import com.pr656d.cattlenotes.ui.cattle.details.CattleDetailsBindingModule
import com.pr656d.cattlenotes.ui.launch.LaunchBindingModule
import com.pr656d.cattlenotes.ui.launch.LauncherActivity
import com.pr656d.cattlenotes.ui.login.LoginActivity
import com.pr656d.cattlenotes.ui.login.LoginBindingModule
import com.pr656d.cattlenotes.ui.login.LoginModule
import com.pr656d.cattlenotes.ui.main.MainActivity
import com.pr656d.cattlenotes.ui.main.MainBindingModule
import com.pr656d.cattlenotes.ui.milking.MilkingBindingModule
import com.pr656d.cattlenotes.ui.timeline.TimelineBindingModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * We want Dagger.Android to create a Subcomponent which has a parent Component of whichever module
 * ActivityBindingModule is on, in our case that will be [AppComponent]. You never
 * need to tell [AppComponent] that it is going to have all these subcomponents
 * nor do you need to tell these subcomponents that [AppComponent] exists.
 * We are also telling Dagger.Android that this generated SubComponent needs to include the
 * specified modules and be aware of a scope annotation [@ActivityScoped].
 * When Dagger.Android annotation processor runs it will create 2 subcomponents for us.
 */
@Module
@Suppress("UNUSED")
abstract class ActivityBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector(modules = [LaunchBindingModule::class])
    internal abstract fun launcherActivity(): LauncherActivity

    @ActivityScoped
    @ContributesAndroidInjector(
        modules = [
            LoginBindingModule::class,
            LoginModule::class
        ]
    )
    internal abstract fun loginActivity(): LoginActivity

    @ActivityScoped
    @ContributesAndroidInjector(
        modules = [
            // activity
            MainBindingModule::class,
            // fragments
            CattleBindingModule::class,
            TimelineBindingModule::class,
            MilkingBindingModule::class,
            CashflowBindingModule::class
        ]
    )
    internal abstract fun mainActivity(): MainActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [CattleDetailsBindingModule::class])
    internal abstract fun cattleDetailsActivity(): CattleDetailsActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [AddEditCattleBindingModule::class])
    internal abstract fun addEditCattleActivity(): AddEditCattleActivity
}