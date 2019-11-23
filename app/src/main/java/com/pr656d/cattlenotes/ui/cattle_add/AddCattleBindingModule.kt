package com.pr656d.cattlenotes.ui.cattle_add

import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.shared.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
@Suppress("UNUSED")
abstract class AddCattleBindingModule {


    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [AddCattleViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(AddCattleViewModel::class)
    internal abstract fun bindAddCattleViewModel(viewModel: AddCattleViewModel): ViewModel
}