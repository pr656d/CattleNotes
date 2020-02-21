package com.pr656d.cattlenotes.ui.aboutapp

import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.shared.di.FragmentScoped
import com.pr656d.cattlenotes.shared.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Module where classes needed to create the [AboutAppModule] are defined.
 */
@Module
@Suppress("UNUSED")
internal abstract class AboutAppModule {

    /**
     * Generates an [AndroidInjector] for the [AboutAppFragment].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeAboutAppFragment(): AboutAppFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [AboutAppViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(AboutAppViewModel::class)
    internal abstract fun bindSettingsViewModel(viewModel: AboutAppViewModel): ViewModel
}