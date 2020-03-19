package com.pr656d.cattlenotes.ui.breeding.addedit

import androidx.lifecycle.ViewModel
import com.pr656d.shared.di.FragmentScoped
import com.pr656d.shared.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Module where classes needed to create the [AddEditBreedingModule] are defined.
 */
@Module
@Suppress("UNUSED")
internal abstract class AddEditBreedingModule {


    /**
     * Generates an [AndroidInjector] for the [AddEditBreedingFragment].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeAddBreedingFragment(): AddEditBreedingFragment

    @Binds
    internal abstract fun provideBreedingUiDelegate(
        impl: BreedingUiImplDelegate
    ): BreedingUiDelegate

    @Binds
    internal abstract fun provideBreedingBehaviour(
        impl: BreedingBehaviourImpl
    ): BreedingBehaviour

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [AddEditBreedingViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(AddEditBreedingViewModel::class)
    internal abstract fun bindCattleDetailsViewModel(viewModel: AddEditBreedingViewModel): ViewModel
}