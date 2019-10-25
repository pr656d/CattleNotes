package com.pr656d.cattlenotes.di.module

import android.app.Application
import android.content.Context
import com.mindorks.bootcamp.instagram.utils.rx.RxSchedulerProvider
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import com.pr656d.cattlenotes.CattleNotesApplication
import com.pr656d.cattlenotes.di.ApplicationContext
import com.pr656d.cattlenotes.utils.network.NetworkHelper
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Singleton

@Module
class ApplicationModule(private val application: CattleNotesApplication) {

    @Provides
    @Singleton
    fun provideApplication(): Application = application

    @Provides
    @Singleton
    @ApplicationContext
    fun provideContext(): Context = application

    @Provides
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Provides
    fun provideSchedulerProvider(): SchedulerProvider = RxSchedulerProvider()

    @Singleton
    @Provides
    fun provideNetworkHelper(): NetworkHelper = NetworkHelper(application)

}