package com.pr656d.cattlenotes.ui.cashflow

import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.shared.di.FragmentScoped
import com.pr656d.cattlenotes.shared.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Module where classes needed to create the [CashflowBuilderModule] are defined.
 */
@Module
@Suppress("UNUSED")
internal abstract class CashflowBuilderModule {

    /**
     * Generates an [AndroidInjector] for the [CashflowFragment].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeCashflowFragment(): CashflowFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [CashflowViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(CashflowViewModel::class)
    internal abstract fun bindCashflowViewModel(viewModel: CashflowViewModel): ViewModel
}