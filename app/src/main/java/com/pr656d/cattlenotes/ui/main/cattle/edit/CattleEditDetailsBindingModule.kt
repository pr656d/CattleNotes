package com.pr656d.cattlenotes.ui.main.cattle.edit

import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.shared.di.FragmentScoped
import com.pr656d.cattlenotes.shared.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Module where classes needed to create the [CattleEditDetailsBindingModule] are defined.
 */
@Module
@Suppress("UNUSED")
internal abstract class CattleEditDetailsBindingModule {


    /**
     * Generates an [AndroidInjector] for the [CattleEditDetailsFragment].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeCattleEditDetailsFragment(): CattleEditDetailsFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [CattleEditViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(CattleEditViewModel::class)
    internal abstract fun bindCattleEditDetailsViewModel(viewModel: CattleEditViewModel): ViewModel
}