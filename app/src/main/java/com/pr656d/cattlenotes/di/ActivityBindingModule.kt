package com.pr656d.cattlenotes.di

import com.pr656d.cattlenotes.ui.MainActivity
import com.pr656d.cattlenotes.ui.MainBindingModule
import com.pr656d.cattlenotes.ui.aboutapp.AboutAppModule
import com.pr656d.cattlenotes.ui.breeding.active.ActiveBreedingModule
import com.pr656d.cattlenotes.ui.breeding.addedit.AddEditBreedingModule
import com.pr656d.cattlenotes.ui.breeding.history.BreedingHistoryModule
import com.pr656d.cattlenotes.ui.cashflow.CashflowModule
import com.pr656d.cattlenotes.ui.cattle.addedit.AddEditCattleModule
import com.pr656d.cattlenotes.ui.cattle.detail.CattleDetailModule
import com.pr656d.cattlenotes.ui.cattle.list.CattleListBindingModule
import com.pr656d.cattlenotes.ui.launch.LaunchBindingModule
import com.pr656d.cattlenotes.ui.launch.LauncherActivity
import com.pr656d.cattlenotes.ui.login.LoginActivity
import com.pr656d.cattlenotes.ui.login.LoginBindingModule
import com.pr656d.cattlenotes.ui.milking.MilkingModule
import com.pr656d.cattlenotes.ui.settings.SettingsModule
import com.pr656d.cattlenotes.ui.timeline.TimelineModule
import com.pr656d.shared.di.ActivityScoped
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
    @ContributesAndroidInjector(modules = [LoginBindingModule::class])
    internal abstract fun loginActivity(): LoginActivity

    @ActivityScoped
    @ContributesAndroidInjector(
        modules = [
            // activity
            MainBindingModule::class,
            // fragments
            CattleListBindingModule::class,
            CattleDetailModule::class,
            AddEditCattleModule::class,
            ActiveBreedingModule::class,
            AddEditBreedingModule::class,
            BreedingHistoryModule::class,
            TimelineModule::class,
            MilkingModule::class,
            CashflowModule::class,
            SettingsModule::class,
            AboutAppModule::class
        ]
    )
    internal abstract fun mainActivity(): MainActivity
}