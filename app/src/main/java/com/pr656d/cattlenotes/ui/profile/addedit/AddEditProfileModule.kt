package com.pr656d.cattlenotes.ui.profile.addedit

import androidx.lifecycle.ViewModel
import com.pr656d.shared.di.FragmentScoped
import com.pr656d.shared.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Module where classes needed to create the [AddEditProfileModule] are defined.
 */
@Module
@Suppress("UNUSED")
internal abstract class AddEditProfileModule {

    /**
     * Generates an [AndroidInjector] for the [ProfileFragment].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeAddEditProfileFragment(): AddEditProfileFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [AddEditProfileViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(AddEditProfileViewModel::class)
    internal abstract fun bindAddEditProfileViewModel(viewModel: AddEditProfileViewModel): ViewModel
}