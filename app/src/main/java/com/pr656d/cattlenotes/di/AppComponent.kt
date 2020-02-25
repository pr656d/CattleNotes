package com.pr656d.cattlenotes.di

import com.pr656d.cattlenotes.CattleNotesApplication
import com.pr656d.cattlenotes.shared.di.ViewModelModule
import com.pr656d.cattlenotes.ui.settings.theme.ThemedActivityDelegateModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * Main component of the app, created and persisted in the Application class.
 *
 * Whenever a new module is created, it should be added to the list of modules.
 * [AndroidSupportInjectionModule] is the module from Dagger.Android that helps with the
 * generation and location of subcomponents.
 */
@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class,
        ActivityBindingModule::class,
        ViewModelModule::class,
        ThemedActivityDelegateModule::class
    ]
)
interface AppComponent : AndroidInjector<CattleNotesApplication> {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: CattleNotesApplication): AppComponent
    }
}