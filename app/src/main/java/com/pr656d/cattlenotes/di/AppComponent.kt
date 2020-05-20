/*
 * Copyright (c) 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pr656d.cattlenotes.di

import com.pr656d.cattlenotes.CattleNotesApplication
import com.pr656d.cattlenotes.ui.profile.ProfileDelegateModule
import com.pr656d.cattlenotes.ui.settings.theme.ThemedActivityDelegateModule
import com.pr656d.shared.di.BroadcastReceiverBindingModule
import com.pr656d.shared.di.ServiceBindingModule
import com.pr656d.shared.di.SharedModule
import com.pr656d.shared.di.ViewModelModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * Main component of the app, created and persisted in the Application class.
 *
 * Whenever a new module is created, it should be added to the list of modules.
 * [AndroidSupportInjectionModule] is the module from Dagger.Android that helps with the
 * generation and location of subcomponents.
 */
@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class,
        SharedModule::class,
        ViewModelModule::class,
        CoroutinesModule::class,
        ActivityBindingModule::class,
        BroadcastReceiverBindingModule::class,
        ServiceBindingModule::class,
        LoginModule::class,
        ThemedActivityDelegateModule::class,
        ProfileDelegateModule::class
    ]
)
interface AppComponent : AndroidInjector<CattleNotesApplication> {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: CattleNotesApplication): AppComponent
    }
}