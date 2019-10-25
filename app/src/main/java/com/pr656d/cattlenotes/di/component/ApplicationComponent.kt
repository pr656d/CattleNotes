package com.pr656d.cattlenotes.di.component

import android.app.Application
import android.content.Context
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import com.pr656d.cattlenotes.CattleNotesApplication
import com.pr656d.cattlenotes.di.ApplicationContext
import com.pr656d.cattlenotes.di.module.ApplicationModule
import com.pr656d.cattlenotes.utils.network.NetworkHelper
import dagger.Component
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    fun inject(app: CattleNotesApplication)

    fun getApplication(): Application

    @ApplicationContext
    fun getContext(): Context

    fun getSchedulerProvider(): SchedulerProvider

    fun getCompositeDisposable(): CompositeDisposable

    fun getNetworkHelper(): NetworkHelper
}