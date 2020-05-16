package com.pr656d.cattlenotes.ui.milking.add

import androidx.lifecycle.ViewModel
import com.pr656d.shared.di.ChildFragmentScoped
import com.pr656d.shared.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Module where classes needed to create the [AddEditMilkModule] are defined.
 */
@Module
@Suppress("UNUSED")
internal abstract class AddEditMilkModule {

    /**
     * Generates an [AndroidInjector] for the [AddMilkDialogFragment].
     */
    @ChildFragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeAddMilkDialogFragment(): AddMilkDialogFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [AddMilkViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(AddMilkViewModel::class)
    internal abstract fun bindAddMilkViewModel(viewModel: AddMilkViewModel): ViewModel
}