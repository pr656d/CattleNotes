package com.pr656d.cattlenotes.ui.main.cattle.breeding.active

import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.shared.di.FragmentScoped
import com.pr656d.cattlenotes.shared.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Module where classes needed to create the [ActiveBreedingModule] are defined.
 */
@Module
@Suppress("UNUSED")
internal abstract class ActiveBreedingModule {


    /**
     * Generates an [AndroidInjector] for the [ActiveBreedingFragment].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeActiveBreedingFragment(): ActiveBreedingFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [ActiveBreedingViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(ActiveBreedingViewModel::class)
    internal abstract fun bindCattleDetailsViewModel(viewModel: ActiveBreedingViewModel): ViewModel
}