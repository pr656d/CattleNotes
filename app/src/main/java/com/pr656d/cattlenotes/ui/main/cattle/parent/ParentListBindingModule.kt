package com.pr656d.cattlenotes.ui.main.cattle.parent

import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.shared.di.FragmentScoped
import com.pr656d.cattlenotes.shared.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Module where classes needed to create the [ParentListBindingModule] are defined.
 */
@Module
@Suppress("UNUSED")
internal abstract class ParentListBindingModule {

    /**
     * Generates an [AndroidInjector] for the [ParentListDialogFragment].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeParentListFragment(): ParentListDialogFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [ParentListViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(ParentListViewModel::class)
    internal abstract fun bindParentListViewModel(viewModel: ParentListViewModel): ViewModel
}