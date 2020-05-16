package com.pr656d.cattlenotes.ui.milking.list

import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.ui.milking.add.AddEditMilkModule
import com.pr656d.cattlenotes.ui.milking.sms.SelectMilkSmsSenderDialogFragment
import com.pr656d.shared.di.FragmentScoped
import com.pr656d.shared.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Module where classes needed to create the [MilkingModule] are defined.
 */
@Module
@Suppress("UNUSED")
internal abstract class MilkingModule {

    /**
     * Generates an [AndroidInjector] for the [MilkingFragment].
     */
    @FragmentScoped
    @ContributesAndroidInjector(modules = [AddEditMilkModule::class])
    internal abstract fun contributeMilkingFragment(): MilkingFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [MilkingViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(MilkingViewModel::class)
    internal abstract fun bindMilkingViewModel(viewModel: MilkingViewModel): ViewModel


    /**
     * Generates an [AndroidInjector] for the [SelectMilkSmsSenderDialogFragment].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeSelectMilkSmsSenderDialogFragment(): SelectMilkSmsSenderDialogFragment
}