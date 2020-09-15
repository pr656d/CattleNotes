/*
 * Copyright 2020 Cattle Notes. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pr656d.cattlenotes.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pr656d.cattlenotes.CattleNotesApplication
import com.pr656d.shared.analytics.AnalyticsHelper
import com.pr656d.shared.data.breeding.BreedingDataRepository
import com.pr656d.shared.data.breeding.BreedingRepository
import com.pr656d.shared.data.breeding.datasource.BreedingDataSource
import com.pr656d.shared.data.cattle.CattleDataRepository
import com.pr656d.shared.data.cattle.CattleRepository
import com.pr656d.shared.data.cattle.datasource.CattleDataSource
import com.pr656d.shared.data.db.AppDatabase
import com.pr656d.shared.data.db.dao.AppDatabaseDao
import com.pr656d.shared.data.db.dao.AppDatabaseDaoImpl
import com.pr656d.shared.data.db.dao.BreedingDao
import com.pr656d.shared.data.db.dao.CattleDao
import com.pr656d.shared.data.db.dao.MilkDao
import com.pr656d.shared.data.db.updater.DatabaseLoader
import com.pr656d.shared.data.db.updater.DbLoader
import com.pr656d.shared.data.login.datasources.AuthIdDataSource
import com.pr656d.shared.data.login.datasources.AuthStateUserDataSource
import com.pr656d.shared.data.login.datasources.FirebaseAuthStateUserDataSource
import com.pr656d.shared.data.milk.MilkDataRepository
import com.pr656d.shared.data.milk.MilkRepository
import com.pr656d.shared.data.milk.datasource.MilkDataSource
import com.pr656d.shared.data.milk.datasource.MilkDataSourceFromSms
import com.pr656d.shared.data.prefs.PreferenceStorageRepository
import com.pr656d.shared.data.prefs.SharedPreferenceStorageRepository
import com.pr656d.shared.data.prefs.datasource.PreferenceStorage
import com.pr656d.shared.data.prefs.datasource.SharedPreferenceStorage
import com.pr656d.shared.data.user.info.datasources.ObserveFirestoreUserInfoDataSource
import com.pr656d.shared.data.user.info.datasources.ObserveFirestoreUserInfoDataSourceImpl
import com.pr656d.shared.di.DefaultDispatcher
import com.pr656d.shared.domain.breeding.notification.BreedingNotificationAlarmUpdater
import com.pr656d.shared.fcm.FcmTokenUpdater
import com.pr656d.shared.performance.PerformanceHelper
import com.pr656d.shared.utils.FirebaseAnalyticsHelper
import com.pr656d.shared.utils.FirebasePerformanceHelper
import com.pr656d.shared.utils.NetworkHelper
import com.pr656d.shared.utils.NetworkHelperImpl
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton

/**
 * Defines all the classes that need to be provided in the scope of the app.
 *
 * Define here all objects that are shared throughout the app, like SharedPreferences, navigators or
 * others. If some of those objects are singletons, they should be annotated with `@Singleton`.
 */
@Module
class AppModule {

    @Provides
    fun provideContext(application: CattleNotesApplication): Context =
        application.applicationContext

    @Singleton
    @Provides
    fun provideAppDatabase(context: Context): AppDatabase = AppDatabase.buildDatabase(context)

    @Singleton
    @Provides
    fun provideCattleDao(appDatabase: AppDatabase): CattleDao = appDatabase.cattleDao()

    @Singleton
    @Provides
    fun provideBreedingDao(appDatabase: AppDatabase): BreedingDao = appDatabase.breedingDao()

    @Singleton
    @Provides
    fun provideMilkDao(appDatabase: AppDatabase): MilkDao = appDatabase.milkDao()

    @Singleton
    @Provides
    fun provideAppDatabaseDao(
        appDatabase: AppDatabase
    ): AppDatabaseDao = AppDatabaseDaoImpl(appDatabase)

    @Singleton
    @Provides
    fun provideCattleRepository(
        cattleDao: CattleDao,
        cattleDataSource: CattleDataSource
    ): CattleRepository = CattleDataRepository(cattleDao, cattleDataSource)

    @Singleton
    @Provides
    fun provideBreedingRepository(
        breedingDao: BreedingDao,
        breedingDataSource: BreedingDataSource
    ): BreedingRepository = BreedingDataRepository(breedingDao, breedingDataSource)

    @Singleton
    @Provides
    fun provideMilkRepository(
        milkDao: MilkDao,
        milkDataSource: MilkDataSource,
        milkDataSourceFromSms: MilkDataSourceFromSms
    ): MilkRepository = MilkDataRepository(milkDao, milkDataSource, milkDataSourceFromSms)

    @Singleton
    @Provides
    fun provideDbUpdater(
        appDatabaseDao: AppDatabaseDao,
        cattleRepository: CattleRepository,
        breedingRepository: BreedingRepository,
        milkRepository: MilkRepository,
        preferenceStorageRepository: PreferenceStorageRepository,
        context: Context,
        @DefaultDispatcher coroutineDispatcher: CoroutineDispatcher
    ): DbLoader = DatabaseLoader(
        context,
        appDatabaseDao,
        cattleRepository,
        breedingRepository,
        milkRepository,
        preferenceStorageRepository,
        coroutineDispatcher
    )

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun providesAuthIdDataSource(
        firebaseAuth: FirebaseAuth
    ): AuthIdDataSource = object : AuthIdDataSource {
        override fun getUserId() = firebaseAuth.currentUser?.uid
    }

    @ExperimentalCoroutinesApi
    @Singleton
    @Provides
    fun provideAuthStateUserDataSource(
        firebase: FirebaseAuth,
        appDatabaseDao: AppDatabaseDao,
        firestore: FirebaseFirestore,
        preferenceStorageRepository: PreferenceStorageRepository,
        dbLoader: DbLoader,
        breedingNotificationAlarmUpdater: BreedingNotificationAlarmUpdater,
        @DefaultDispatcher coroutineDispatcher: CoroutineDispatcher
    ): AuthStateUserDataSource = FirebaseAuthStateUserDataSource(
        firebase,
        appDatabaseDao,
        FcmTokenUpdater(firestore, coroutineDispatcher),
        preferenceStorageRepository,
        dbLoader,
        breedingNotificationAlarmUpdater,
        coroutineDispatcher
    )

    @ExperimentalCoroutinesApi
    @FlowPreview
    @Singleton
    @Provides
    fun provideObserveFirestoreUserInfoDataSource(
        firestore: FirebaseFirestore,
        authIdDataSource: AuthIdDataSource
    ): ObserveFirestoreUserInfoDataSource = ObserveFirestoreUserInfoDataSourceImpl(
        firestore, authIdDataSource
    )

    @FlowPreview
    @ExperimentalCoroutinesApi
    @Singleton
    @Provides
    fun providePreferenceStorage(context: Context): PreferenceStorage =
        SharedPreferenceStorage(context)

    @Singleton
    @Provides
    fun providePreferenceStorageRepository(
        preferenceStorage: PreferenceStorage
    ): PreferenceStorageRepository = SharedPreferenceStorageRepository(preferenceStorage)

    @Singleton
    @Provides
    fun provideFirebaseAnalyticsHelper(
        context: Context
    ): AnalyticsHelper = FirebaseAnalyticsHelper(context)

    @Singleton
    @Provides
    fun providePerformanceHelper(): PerformanceHelper = FirebasePerformanceHelper()

    @Singleton
    @Provides
    fun provideNetworkHelper(context: Context): NetworkHelper = NetworkHelperImpl(context)
}
