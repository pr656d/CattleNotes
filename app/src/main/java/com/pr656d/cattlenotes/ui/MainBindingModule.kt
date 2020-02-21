package com.pr656d.cattlenotes.ui

import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.shared.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Module where classes needed for app launch are defined.
 */
@Module
@Suppress("UNUSED")
internal abstract class MainBindingModule {

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [MainViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    internal abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel
}