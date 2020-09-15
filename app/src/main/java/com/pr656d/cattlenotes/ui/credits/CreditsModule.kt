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
package com.pr656d.cattlenotes.ui.credits

import androidx.lifecycle.ViewModel
import com.pr656d.shared.di.FragmentScoped
import com.pr656d.shared.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Module where classes needed to create the [CreditsModule] are defined.
 */
@Module
@Suppress("UNUSED")
internal abstract class CreditsModule {

    /**
     * Generates an [AndroidInjector] for the [CreditsFragment].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeCreditsFragment(): CreditsFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [CreditsViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(CreditsViewModel::class)
    internal abstract fun bindCreditsViewModel(viewModel: CreditsViewModel): ViewModel
}
