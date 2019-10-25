package com.pr656d.cattlenotes

import android.app.Application
import com.pr656d.cattlenotes.di.component.ApplicationComponent
import com.pr656d.cattlenotes.di.component.DaggerApplicationComponent
import com.pr656d.cattlenotes.di.module.ApplicationModule

class CattleNotesApplication : Application() {

    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        injectDependencies()
    }

    private fun injectDependencies() {
        applicationComponent = DaggerApplicationComponent
            .builder()
            .applicationModule(ApplicationModule(this))
            .build()
        applicationComponent.inject(this)
    }

}