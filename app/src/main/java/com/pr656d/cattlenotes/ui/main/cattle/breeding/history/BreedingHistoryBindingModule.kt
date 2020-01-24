package com.pr656d.cattlenotes.ui.main.cattle.breeding.history

import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.shared.di.FragmentScoped
import com.pr656d.cattlenotes.shared.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Module where classes needed to create the [BreedingHistoryBindingModule] are defined.
 */
@Module
@Suppress("UNUSED")
internal abstract class BreedingHistoryBindingModule {


    /**
     * Generates an [AndroidInjector] for the [BreedingHistoryFragment].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeBreedingHistoryFragment(): BreedingHistoryFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [BreedingHistoryViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(BreedingHistoryViewModel::class)
    internal abstract fun bindCattleDetailsViewModel(viewModel: BreedingHistoryViewModel): ViewModel
}