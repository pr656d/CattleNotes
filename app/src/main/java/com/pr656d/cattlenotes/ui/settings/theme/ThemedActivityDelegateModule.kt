package com.pr656d.cattlenotes.ui.settings.theme

import androidx.lifecycle.ViewModel
import com.pr656d.shared.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
@Suppress("UNUSED")
abstract class ThemedActivityDelegateModule {

    @Singleton
    @Binds
    abstract fun provideThemedActivityDelegate(
        impl: ThemedActivityDelegateImpl
    ): ThemedActivityDelegate

    @Binds
    @IntoMap
    @ViewModelKey(ThemeViewModel::class)
    abstract fun provideThemeViewModel(viewModel: ThemeViewModel): ViewModel
}
