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