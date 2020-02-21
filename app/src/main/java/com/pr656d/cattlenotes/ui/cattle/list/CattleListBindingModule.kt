package com.pr656d.cattlenotes.ui.cattle.list

import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.shared.di.FragmentScoped
import com.pr656d.cattlenotes.shared.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Module where classes needed to create the [CattleListBindingModule] are defined.
 */
@Module
@Suppress("UNUSED")
internal abstract class CattleListBindingModule {

    /**
     * Generates an [AndroidInjector] for the [CattleListFragment].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeCattleFragment(): CattleListFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [CattleListViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(CattleListViewModel::class)
    internal abstract fun bindCattleViewModel(viewModel: CattleListViewModel): ViewModel
}