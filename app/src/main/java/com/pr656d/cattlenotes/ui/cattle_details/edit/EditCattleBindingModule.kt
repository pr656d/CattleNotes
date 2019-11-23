package com.pr656d.cattlenotes.ui.cattle_details.edit

import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.shared.di.FragmentScoped
import com.pr656d.cattlenotes.shared.di.ViewModelKey
import com.pr656d.cattlenotes.ui.cattle_details.details.CattleDetailsViewModel
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
@Suppress("UNUSED")
internal abstract class EditCattleBindingModule {

    /**
     * Generates an [AndroidInjector] for the [EditCattleFragment].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeEditCattleFragment(): EditCattleFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [CattleDetailsViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(EditCattleViewModel::class)
    internal abstract fun bindCattleDetailsViewModel(viewModel: EditCattleViewModel): ViewModel

}