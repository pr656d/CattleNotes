package com.pr656d.cattlenotes.ui.breeding.history.ofcattle

import androidx.lifecycle.ViewModel
import com.pr656d.shared.di.FragmentScoped
import com.pr656d.shared.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Module where classes needed to create the [BreedingHistoryOfCattleModule] are defined.
 */
@Module
@Suppress("UNUSED")
internal abstract class BreedingHistoryOfCattleModule {


    /**
     * Generates an [AndroidInjector] for the [BreedingHistoryOfCattleFragment].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeBreedingHistoryOfCattleFragment(): BreedingHistoryOfCattleFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [BreedingHistoryOfCattleViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(BreedingHistoryOfCattleViewModel::class)
    internal abstract fun bindBreedingHistoryOfCattleViewModel(viewModel: BreedingHistoryOfCattleViewModel): ViewModel
}