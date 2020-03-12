package com.pr656d.cattlenotes.ui.launch

import androidx.lifecycle.ViewModel
import com.pr656d.shared.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Module where classes needed for app launch are defined.
 */
@Module
@Suppress("UNUSED")
internal abstract class LaunchBindingModule {

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [LaunchViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(LaunchViewModel::class)
    internal abstract fun bindLaunchViewModel(viewModel: LaunchViewModel): ViewModel
}