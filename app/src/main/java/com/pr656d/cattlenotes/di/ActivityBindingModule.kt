/*
 * Copyright 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pr656d.cattlenotes.di

import com.pr656d.cattlenotes.ui.MainActivity
import com.pr656d.cattlenotes.ui.MainBindingModule
import com.pr656d.cattlenotes.ui.breeding.addedit.AddEditBreedingModule
import com.pr656d.cattlenotes.ui.breeding.history.ofcattle.BreedingHistoryOfCattleModule
import com.pr656d.cattlenotes.ui.cashflow.CashflowModule
import com.pr656d.cattlenotes.ui.cattle.addedit.AddEditCattleModule
import com.pr656d.cattlenotes.ui.cattle.detail.CattleDetailModule
import com.pr656d.cattlenotes.ui.cattle.list.CattleListBindingModule
import com.pr656d.cattlenotes.ui.credits.CreditsModule
import com.pr656d.cattlenotes.ui.launch.LaunchBindingModule
import com.pr656d.cattlenotes.ui.launch.LauncherActivity
import com.pr656d.cattlenotes.ui.login.LoginActivity
import com.pr656d.cattlenotes.ui.login.LoginModule
import com.pr656d.cattlenotes.ui.milking.list.MilkingModule
import com.pr656d.cattlenotes.ui.profile.ProfileModule
import com.pr656d.cattlenotes.ui.profile.addedit.AddEditProfileModule
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
            MainBindingModule::class,
            // fragments
            CattleListBindingModule::class,
            CattleDetailModule::class,
            AddEditCattleModule::class,
            AddEditBreedingModule::class,
            BreedingHistoryOfCattleModule::class,
            TimelineModule::class,
            MilkingModule::class,
            CashflowModule::class,
            SettingsModule::class,
            CreditsModule::class,
            ProfileModule::class,
            AddEditProfileModule::class
        ]
    )
    internal abstract fun mainActivity(): MainActivity
}
