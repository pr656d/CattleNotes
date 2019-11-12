package com.pr656d.cattlenotes.ui.timeline

import androidx.lifecycle.ViewModel
import com.pr656d.cattlenotes.shared.di.FragmentScoped
import com.pr656d.cattlenotes.shared.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Module where classes needed to create the [TimelineBindingModule] are defined.
 */
@Module
@Suppress("UNUSED")
internal abstract class TimelineBindingModule {

    /**
     * Generates an [AndroidInjector] for the [TimelineFragment].
     */
    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun contributeTimelineFragment(): TimelineFragment

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [TimelineViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(TimelineViewModel::class)
    internal abstract fun bindTimelineViewModel(viewModel: TimelineViewModel): ViewModel
}