package com.pr656d.cattlenotes.ui.main.cattle.addedit

import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.shared.di.FragmentScoped
import com.pr656d.cattlenotes.shared.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Module where classes needed to create the [AddEditCattleModule] are defined.
 */
@Module
@Suppress("UNUSED")
internal abstract class AddEditCattleModule {

    /**
     * Generates an [AndroidInjector] for the [AddEditCattleFragment].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeAddEditCattleFragment(): AddEditCattleFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [AddEditCattleViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(AddEditCattleViewModel::class)
    internal abstract fun bindAddCattleViewModel(viewModel: AddEditCattleViewModel): ViewModel
}