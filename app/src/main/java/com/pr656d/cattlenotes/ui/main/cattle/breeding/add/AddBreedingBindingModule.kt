package com.pr656d.cattlenotes.ui.main.cattle.breeding.add

import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.shared.di.FragmentScoped
import com.pr656d.cattlenotes.shared.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Module where classes needed to create the [AddBreedingBindingModule] are defined.
 */
@Module
@Suppress("UNUSED")
internal abstract class AddBreedingBindingModule {


    /**
     * Generates an [AndroidInjector] for the [AddBreedingFragment].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeAddBreedingFragment(): AddBreedingFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [AddBreedingViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(AddBreedingViewModel::class)
    internal abstract fun bindCattleDetailsViewModel(viewModel: AddBreedingViewModel): ViewModel
}