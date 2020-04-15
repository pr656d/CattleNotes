package com.pr656d.cattlenotes.di

import android.content.Context
import com.pr656d.cattlenotes.CattleNotesApplication
import com.pr656d.shared.analytics.AnalyticsHelper
import com.pr656d.shared.data.breeding.BreedingDataRepository
import com.pr656d.shared.data.breeding.BreedingRepository
import com.pr656d.shared.data.breeding.datasources.BreedingDataSource
import com.pr656d.shared.data.cattle.CattleDataRepository
import com.pr656d.shared.data.cattle.CattleRepository
import com.pr656d.shared.data.cattle.datasources.CattleDataSource
import com.pr656d.shared.data.db.AppDatabase
import com.pr656d.shared.data.db.AppDatabaseDao
import com.pr656d.shared.data.db.BreedingDao
import com.pr656d.shared.data.db.CattleDao
import com.pr656d.shared.data.db.updater.DatabaseLoader
import com.pr656d.shared.data.db.updater.DbLoader
import com.pr656d.shared.data.prefs.PreferenceStorage
import com.pr656d.shared.data.prefs.SharedPreferenceStorage
import com.pr656d.shared.utils.FirebaseAnalyticsHelper
import com.pr656d.shared.utils.NetworkHelper
import com.pr656d.shared.utils.NetworkHelperImpl
import dagger.Module
import dagger.Provides
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
    fun providesPreferenceStorage(context: Context): PreferenceStorage =
        SharedPreferenceStorage(context)

    @Singleton
    @Provides
    fun provideFirebaseAnalyticsHelper(
        context: Context
    ): AnalyticsHelper = FirebaseAnalyticsHelper(context)

    @Singleton
    @Provides
    fun providesAppDatabase(context: Context): AppDatabase = AppDatabase.buildDatabase(context)

    @Singleton
    @Provides
    fun provideCattleDao(appDatabase: AppDatabase): CattleDao = appDatabase.cattleDao()

    @Singleton
    @Provides
    fun provideBreedingDao(appDatabase: AppDatabase): BreedingDao = appDatabase.breedingDao()

    @Singleton
    @Provides
    fun provideAppDatabaseDao(appDatabase: AppDatabase): AppDatabaseDao {
        return object : AppDatabaseDao {
            override fun clear() {
                appDatabase.clearAllTables()
            }
        }
    }

    @Singleton
    @Provides
    fun provideCattleRepository(
        appDatabase: AppDatabase,
        cattleDataSource: CattleDataSource
    ): CattleRepository {
        return CattleDataRepository(
            appDatabase,
            cattleDataSource
        )
    }

    @Singleton
    @Provides
    fun provideBreedingRepository(
        appDatabase: AppDatabase,
        breedingDataSource: BreedingDataSource
    ): BreedingRepository {
        return BreedingDataRepository(
            appDatabase,
            breedingDataSource
        )
    }

    @Singleton
    @Provides
    fun provideDbUpdater(
        cattleDataSource: CattleDataSource,
        breedingDataSource: BreedingDataSource,
        context: Context
    ): DbLoader {
        return DatabaseLoader(cattleDataSource, breedingDataSource, context)
    }

    @Singleton
    @Provides
    fun provideNetworkHelper(
        context: Context
    ): NetworkHelper {
        return NetworkHelperImpl(context)
    }
}
