package com.pr656d.cattlenotes.ui.cattle.add

import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.shared.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
@Suppress("UNUSED")
abstract class AddEditCattleBindingModule {


    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [AddEditCattleViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(AddEditCattleViewModel::class)
    internal abstract fun bindAddCattleViewModel(viewModel: AddEditCattleViewModel): ViewModel
}