package com.pr656d.cattlenotes.ui.settings

import androidx.lifecycle.ViewModel
import com.pr656d.shared.di.FragmentScoped
import com.pr656d.shared.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Module where classes needed to create the [SettingsModule] are defined.
 */
@Module
@Suppress("UNUSED")
internal abstract class SettingsModule {

    /**
     * Generates an [AndroidInjector] for the [SettingsFragment].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeSettingsFragment(): SettingsFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [SettingsViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindSettingsViewModel(viewModel: SettingsViewModel): ViewModel

    /**
     * Generates an [AndroidInjector] for the [ThemeSettingDialogFragment].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeThemeSettingFragment(): ThemeSettingDialogFragment
}