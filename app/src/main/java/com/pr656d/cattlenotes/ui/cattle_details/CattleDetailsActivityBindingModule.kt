package com.pr656d.cattlenotes.ui.cattle_details

import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.shared.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Module where classes needed to create the [CattleDetailsActivityBindingModule] are defined.
 */
@Module
@Suppress("UNUSED")
internal abstract class CattleDetailsActivityBindingModule {

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [CattleDetailsViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(CattleDetailsViewModel::class)
    internal abstract fun bindCattleDetailsViewModel(viewModel: CattleDetailsViewModel): ViewModel
}