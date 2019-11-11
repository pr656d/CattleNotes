package com.pr656d.cattlenotes.ui.cattle

import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.shared.di.FragmentScoped
import com.pr656d.cattlenotes.shared.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Module where classes needed to create the [CattleBuilderModule] are defined.
 */
@Module
@Suppress("UNUSED")
internal abstract class CattleBuilderModule {

    /**
     * Generates an [AndroidInjector] for the [CattleFragment].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeCattleFragment(): CattleFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [CattleViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(CattleViewModel::class)
    internal abstract fun bindCattleViewModel(viewModel: CattleViewModel): ViewModel
}