package com.pr656d.cattlenotes.ui.cattle_details.details

import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.shared.di.FragmentScoped
import com.pr656d.cattlenotes.shared.di.ViewModelKey
import com.pr656d.cattlenotes.ui.main.cattle.CattleViewModel
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
@Suppress("UNUSED")
internal abstract class CattleDetailsFragmentBindingModule {

    /**
     * Generates an [AndroidInjector] for the [CattleDetailsFragment].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeCattleDetailsFragment(): CattleDetailsFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [CattleDetailsViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(CattleViewModel::class)
    internal abstract fun bindCattleDetailsViewModel(viewModel: CattleDetailsViewModel): ViewModel

}