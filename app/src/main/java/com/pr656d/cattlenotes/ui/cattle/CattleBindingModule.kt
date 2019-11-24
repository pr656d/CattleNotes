package com.pr656d.cattlenotes.ui.cattle

import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.shared.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Module where classes needed to create the [CattleBindingModule] are defined.
 */
@Module
@Suppress("UNUSED")
internal abstract class CattleBindingModule {

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [CattleViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(CattleViewModel::class)
    internal abstract fun bindCattleDetailsViewModel(viewModel: CattleViewModel): ViewModel
}