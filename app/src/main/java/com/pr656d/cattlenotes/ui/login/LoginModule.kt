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
package com.pr656d.cattlenotes.ui.login

import androidx.lifecycle.ViewModel
import com.pr656d.shared.di.FragmentScoped
import com.pr656d.shared.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Module where classes needed to create the [LoginModule] are defined.
 */
@Module
@Suppress("UNUSED")
internal abstract class LoginModule {
    /**
     * Generates an [AndroidInjector] for the [LoginFragment].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeLoginFragment(): LoginFragment

    /**
     * Generates an [AndroidInjector] for the [SetupProfileFragment].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeSetupProfileFragment(): SetupProfileFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [LoginViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindLoginViewModel(viewModel: LoginViewModel): ViewModel
}
