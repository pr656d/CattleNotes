package com.pr656d.cattlenotes.di

import com.pr656d.cattlenotes.CattleNotesApplication
import com.pr656d.cattlenotes.shared.di.ViewModelModule
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
        ViewModelModule::class
    ]
)
interface AppComponent : AndroidInjector<CattleNotesApplication> {
    /**
     * [MainDataManager] used for data caching.
     *
     * Fragments are created every time if we switch to another activity/fragment.
     * That will fetch data again from the database.
     */
    fun mainDataManager(): MainDataManager

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: CattleNotesApplication): AppComponent
    }
}