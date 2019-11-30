package com.pr656d.cattlenotes.ui.main.cattle.add

import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.shared.di.FragmentScoped
import com.pr656d.cattlenotes.shared.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Module where classes needed to create the [CattleAddBindingModule] are defined.
 */
@Module
@Suppress("UNUSED")
internal abstract class CattleAddBindingModule {


    /**
     * Generates an [AndroidInjector] for the [CattleAddFragment].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeCattleAddFragment(): CattleAddFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [CattleAddViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(CattleAddViewModel::class)
    internal abstract fun bindCattleAddViewModel(viewModel: CattleAddViewModel): ViewModel
}