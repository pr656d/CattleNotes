package com.pr656d.cattlenotes.ui.main.cattle.addedit.parent

import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.shared.di.ChildFragmentScoped
import com.pr656d.cattlenotes.shared.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Module where classes needed to create the [ParentListDialogModule] are defined.
 */
@Module
@Suppress("UNUSED")
internal abstract class ParentListDialogModule {

    /**
     * Generates an [AndroidInjector] for the [ParentListDialogFragment].
     */
    @ChildFragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeParentListDialogFragment(): ParentListDialogFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [ParentListDialogViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(ParentListDialogViewModel::class)
    internal abstract fun bindParentListDialogViewModel(viewModel: ParentListDialogViewModel): ViewModel
}