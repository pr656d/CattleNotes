package com.pr656d.cattlenotes.ui.about

import androidx.lifecycle.ViewModel
import com.pr656d.shared.di.FragmentScoped
import com.pr656d.shared.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Module where classes needed to create the [AboutModule] are defined.
 */
@Module
@Suppress("UNUSED")
internal abstract class AboutModule {

    /**
     * Generates an [AndroidInjector] for the [AboutFragment].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeAboutFragment(): AboutFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [AboutViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(AboutViewModel::class)
    internal abstract fun bindAboutViewModel(viewModel: AboutViewModel): ViewModel
}