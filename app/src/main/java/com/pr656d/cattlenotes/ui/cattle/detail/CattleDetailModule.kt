package com.pr656d.cattlenotes.ui.cattle.detail

import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.shared.di.FragmentScoped
import com.pr656d.cattlenotes.shared.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Module where classes needed to create the [CattleDetailModule] are defined.
 */
@Module
@Suppress("UNUSED")
internal abstract class CattleDetailModule {


    /**
     * Generates an [AndroidInjector] for the [CattleDetailFragment].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeCattleDetailsFragment(): CattleDetailFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [CattleDetailViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(CattleDetailViewModel::class)
    internal abstract fun bindCattleDetailsViewModel(viewModel: CattleDetailViewModel): ViewModel
}