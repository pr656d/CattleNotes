package com.pr656d.cattlenotes.di

import androidx.lifecycle.ViewModelProvider
import com.pr656d.cattlenotes.MainViewModelFactory
import dagger.Binds
import dagger.Module

/**
 * Module used to define the connection between the framework's [ViewModelProvider.Factory] and
 * our own implementation: [MainViewModelFactory].
 */
@Module
@Suppress("UNUSED")
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: MainViewModelFactory):
        ViewModelProvider.Factory
}
